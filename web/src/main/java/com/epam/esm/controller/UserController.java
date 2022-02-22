package com.epam.esm.controller;

import com.epam.esm.Order;
import com.epam.esm.User;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RequestMapping(value = "api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {


    private static final String MAX_CERTIFICATES_IN_REQUEST = "20";
    private static final String DEFAULT_ORDER = "ASC";
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }


    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {

        User user = service.getById(userId).orElseThrow(NoSuchElementException::new);

        user.add(linkTo(methodOn(UserController.class)
                .getUser(user.getId()))
                .withSelfRel());

        user.add(linkTo(methodOn(UserController.class)
                .getUserOrders(user.getId()))
                .withRel("orders"));

        return user;
    }

    @GetMapping("/")
    public Set<User> getUsers(@RequestParam(value = "order", defaultValue = MAX_CERTIFICATES_IN_REQUEST) String order,
                              @RequestParam(value = "max", defaultValue = DEFAULT_ORDER) int max) {
        Set<User> users = service.getUsers(order, max);
        if (users.isEmpty()) {
            throw new NoEntitiesFoundException();
        }
        users.forEach(user -> {
                    user.add(linkTo(methodOn(UserController.class)
                            .getUser(user.getId()))
                            .withSelfRel());

                    user.add(linkTo(methodOn(UserController.class)
                            .getUserOrders(user.getId()))
                            .withRel("orders"));
                }
        );
        return users;
    }


    @GetMapping("/{userId}/orders")
    public Set<Order> getUserOrders(@RequestParam(value = "userId") long userId) {
        User user = service.getById(userId).orElseThrow(() -> new NoSuchElementException("No such user exists"));

        Set<Order> userOrders = user.getOrders();
        if (userOrders.isEmpty()) {
            throw new NoEntitiesFoundException();
        }
        userOrders.forEach(order -> {
                    order.add(linkTo(methodOn(OrderController.class)
                            .getOrder(order.getId()))
                            .withSelfRel());

                    order.add(linkTo(methodOn(OrderController.class)
                            .deleteOrder(order.getId()))
                            .withRel("delete"));

                    order.add(linkTo(methodOn(OrderController.class)
                            .getOrderCertificates(order.getId()))
                            .withRel("products"));
                }
        );
        return userOrders;
    }
}