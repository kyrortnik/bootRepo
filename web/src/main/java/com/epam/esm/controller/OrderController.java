package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.OrderService;
import com.epam.esm.mapper.RequestMapper;
import com.epam.esm.util.GetMethodProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

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
    public Order getOrder(@PathVariable Long orderId) {

        Order order = orderService.getOrderById(orderId).orElseThrow(NoSuchElementException::new);

        order.add(linkTo(methodOn(OrderController.class)
                .getOrder(order.getId()))
                .withSelfRel());

        order.add(linkTo(methodOn(OrderController.class)
                .getOrderGiftCertificate(order.getId()))
                .withRel("gift certificate"));

        order.add(linkTo(methodOn(UserController.class)
                .getUser(order.getUser().getId()))
                .withRel("user"));

        return order;
    }


    @GetMapping("/")
    public Set<Order> getOrders(
            @RequestParam(value = "sort_by", defaultValue = GetMethodProperty.DEFAULT_SORT_BY) Set<String> sortBy,
            @RequestParam(value = "max", defaultValue = GetMethodProperty.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "offset", defaultValue = GetMethodProperty.DEFAULT_OFFSET) int offset) {
        HashMap<String, Boolean> sortingParams = RequestMapper.mapSortingParams(sortBy);
        Set<Order> orders = orderService.getOrders(sortingParams, max, offset);
        if (orders.isEmpty()) {
            throw new NoEntitiesFoundException();
        }

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
        return orders;
    }


    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    Order createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.create(order).orElseThrow(() -> new DuplicateKeyException("Such order already exists"));

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .getOrder(createdOrder.getId()))
                .withSelfRel());

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .deleteOrder( createdOrder.getId()))
                .withRel("delete"));

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .getOrderGiftCertificate( createdOrder.getId()))
                .withRel("gift certificate"));

        return createdOrder;
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        return orderService.delete(orderId)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>("No order with such id was found", HttpStatus.OK);
    }


    @GetMapping("/{orderId}/giftCertificate")
    public GiftCertificate getOrderGiftCertificate(@PathVariable long orderId) {
        Order order = orderService.getOrderById(orderId).orElseThrow(() -> new NoSuchElementException("No such order exists"));

        GiftCertificate giftCertificate = order.getGiftCertificate();

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getCertificateById(giftCertificate.getId()))
                .withSelfRel());

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .deleteGiftCertificate(giftCertificate.getId()))
                .withRel("delete"));

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getGiftCertificateTags(giftCertificate.getId()))
                .withRel("tags"));

        return giftCertificate;
    }

}
