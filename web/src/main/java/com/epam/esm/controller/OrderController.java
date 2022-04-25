package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.impl.OrderService;
import com.epam.esm.util.DefaultValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping(value = "api/v1/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Order getOrder(@PathVariable Long orderId) {
        LOGGER.debug("Entering OrderController.getOrder()");

        Order order = orderService.getOrderById(orderId);

        order.add(linkTo(methodOn(OrderController.class)
                .getOrder(order.getId()))
                .withSelfRel());

        order.add(linkTo(methodOn(OrderController.class)
                .getOrderGiftCertificate(order.getId()))
                .withRel("gift certificate"));

        order.add(linkTo(methodOn(UserController.class)
                .getUser(order.getUser().getId()))
                .withRel("user"));

        LOGGER.debug("Exiting OrderController.getOrder()");
        return order;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/")
    public Page<Order> getOrders(
            @RequestParam(value = "sort_by", defaultValue = DefaultValue.DEFAULT_SORT_BY) List<String> sortBy,
            @RequestParam(value = "max", defaultValue = DefaultValue.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "page", defaultValue = DefaultValue.DEFAULT_PAGE) int page) {
        LOGGER.debug("Entering OrderController.getOrders()");

        Page<Order> orders = orderService.getOrders(sortBy, max, page);
        orders.forEach(foundOrder -> {
                    foundOrder.add(linkTo(methodOn(OrderController.class)
                            .getOrder(foundOrder.getId()))
                            .withSelfRel());

                    foundOrder.add(linkTo(methodOn(OrderController.class)
                            .getOrderGiftCertificate(foundOrder.getId()))
                            .withRel("gift certificate"));

                    foundOrder.add(linkTo(methodOn(UserController.class)
                            .getUser(foundOrder.getUser().getId()))
                            .withRel("user"));
                }
        );
        LOGGER.debug("Exiting OrderController.getOrders()");
        return orders;
    }


    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public @ResponseBody
    Order createOrder(@RequestBody Order order) {
        LOGGER.debug("Entering OrderController.createOrder()");

        Order createdOrder = orderService.create(order);

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .getOrder(createdOrder.getId()))
                .withSelfRel());

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .deleteOrder(createdOrder.getId()))
                .withRel("delete"));

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .getOrderGiftCertificate(createdOrder.getId()))
                .withRel("gift certificate"));

        LOGGER.debug("Exiting OrderController.createOrder()");
        return createdOrder;
    }


    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        LOGGER.debug("Entering OrderController.deleteOrder()");

        ResponseEntity<String> response = orderService.deleteOrder(orderId)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(String.format("No order with id [%s] was found", orderId), HttpStatus.OK);

        LOGGER.debug("Exiting OrderController.deleteOrder()");
        return response;
    }


    @GetMapping("/{orderId}/giftCertificate")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public GiftCertificate getOrderGiftCertificate(@PathVariable long orderId) {
        LOGGER.debug("Entering OrderController.getOrderGiftCertificate()");
        Order order = orderService.getOrderById(orderId);

        GiftCertificate giftCertificate = order.getGiftCertificate();

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getGiftCertificateById(giftCertificate.getId()))
                .withSelfRel());

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .deleteGiftCertificate(giftCertificate.getId()))
                .withRel("delete"));

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getGiftCertificateTags(giftCertificate.getId()))
                .withRel("tags"));

        LOGGER.debug("Exiting OrderController.getOrderGiftCertificate()");
        return giftCertificate;
    }

}
