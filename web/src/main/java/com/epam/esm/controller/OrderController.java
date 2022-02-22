package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping(value = "api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final OrderService orderService;

    private static final String MAX_CERTIFICATES_IN_REQUEST = "20";
    private static final String DEFAULT_ORDER = "ASC";

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable Long orderId) {

        Order order = orderService.getById(orderId).orElseThrow(NoSuchElementException::new);

        order.add(linkTo(methodOn(OrderController.class)
                .getOrder(order.getId()))
                .withSelfRel());

        order.add(linkTo(methodOn(OrderController.class)
                .getOrderCertificates(order.getId()))
                .withRel("certificates"));

        return order;
    }

    @GetMapping("/")
    public Set<Order> getOrders(
            @RequestParam(value = "order", defaultValue = DEFAULT_ORDER) String order,
            @RequestParam(value = "max", defaultValue = MAX_CERTIFICATES_IN_REQUEST) int max,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "pattern", required = false) String pattern) {
        Set<Order> orders = orderService.getOrders(order, max);
        if (orders.isEmpty()) {
            throw new NoEntitiesFoundException();
        }

        orders.forEach(foundOrder -> {
                    foundOrder.add(linkTo(methodOn(OrderController.class)
                            .getOrder(foundOrder.getId()))
                            .withSelfRel());

                    foundOrder.add(linkTo(methodOn(OrderController.class)
                            .getOrderCertificates(foundOrder.getId()))
                            .withRel("certificates"));
                }
        );
        return orders;
    }


    @PostMapping("/")
    public @ResponseBody
    Order createOrder(@RequestBody Order order) {
        Optional<Order> createdGiftCertificate = orderService.create(order);
        return createdGiftCertificate.orElseThrow((() -> new NoSuchElementException("No such order exists")));
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        return orderService.delete(orderId)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>("No order with such id was found", HttpStatus.OK);
    }

    @GetMapping("/{orderId}/certificates")
    public Set<GiftCertificate> getOrderCertificates(@RequestParam(value = "orderId") long userId) {
        Order order = orderService.getById(userId).orElseThrow(() -> new NoSuchElementException("No such order exists"));

        Set<GiftCertificate> orderCertificates = order.getGiftCertificates();
        if (orderCertificates.isEmpty()) {
            throw new NoEntitiesFoundException("No certificates exists for this order");
        }
        orderCertificates.forEach(giftCertificate -> {
                    giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                            .getCertificate(giftCertificate.getId()))
                            .withSelfRel());

                    giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                            .deleteGiftCertificate(giftCertificate.getId()))
                            .withRel("delete"));

                    giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                            .getGiftCertificateTags(giftCertificate.getId()))
                            .withRel("tags"));
                }
        );
        return orderCertificates;
    }
}
