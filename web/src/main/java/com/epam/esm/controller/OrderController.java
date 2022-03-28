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

import java.util.*;

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
        LOGGER.info("Entering OrderController.getOrder()");

        Order order = orderService.getOrderById(orderId).orElseThrow(() -> new NoSuchElementException("Order with id [" + orderId + "] not found"));

        order.add(linkTo(methodOn(OrderController.class)
                .getOrder(order.getId()))
                .withSelfRel());

        order.add(linkTo(methodOn(OrderController.class)
                .getOrderGiftCertificate(order.getId()))
                .withRel("gift certificate"));

        order.add(linkTo(methodOn(UserController.class)
                .getUser(order.getUser().getId()))
                .withRel("user"));

        LOGGER.info("Exiting OrderController.getOrder()");
        return order;
    }


    @GetMapping("/")
    public List<Order> getOrders(
            @RequestParam(value = "sort_by", defaultValue = GetMethodProperty.DEFAULT_SORT_BY) List<String> sortBy,
            @RequestParam(value = "max", defaultValue = GetMethodProperty.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "offset", defaultValue = GetMethodProperty.DEFAULT_OFFSET) int offset) {
        LOGGER.info("Entering OrderController.getOrders()");

        LinkedHashMap<String, Boolean> sortingParams = RequestMapper.mapSortingParams(sortBy);
        List<Order> orders = orderService.getOrders(sortingParams, max, offset);
        if (orders.isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in OrderController.getOrders()\n" +
                    "No Orders exist");
            throw new NoEntitiesFoundException("No Orders exist");
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
        LOGGER.info("Exiting OrderController.getOrders()");
        return orders;
    }


    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    Order createOrder(@RequestBody Order order) {
        LOGGER.info("Entering OrderController.createOrder()");

        Order createdOrder = orderService.create(order).orElseThrow(() -> new DuplicateKeyException("Such order already exists"));

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .getOrder(createdOrder.getId()))
                .withSelfRel());

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .deleteOrder(createdOrder.getId()))
                .withRel("delete"));

        createdOrder.add(linkTo(methodOn(OrderController.class)
                .getOrderGiftCertificate(createdOrder.getId()))
                .withRel("gift certificate"));

        LOGGER.info("Exiting OrderController.createOrder()");
        return createdOrder;
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        LOGGER.info("Entering OrderController.deleteOrder()");

        ResponseEntity<String> response = orderService.delete(orderId)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>("No order with id [" + orderId + "] was found", HttpStatus.OK);

        LOGGER.info("Exiting OrderController.deleteOrder()");
        return response;
    }


    @GetMapping("/{orderId}/giftCertificate")
    public GiftCertificate getOrderGiftCertificate(@PathVariable long orderId) {
        LOGGER.info("Entering OrderController.getOrderGiftCertificate()");
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

        LOGGER.info("Exiting OrderController.getOrderGiftCertificate()");
        return giftCertificate;
    }

}
