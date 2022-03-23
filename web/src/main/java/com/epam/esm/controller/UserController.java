package com.epam.esm.controller;

import com.epam.esm.Order;
import com.epam.esm.User;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.UserService;
import com.epam.esm.mapper.RequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {


    private static final String MAX_USERS_IN_RESPONSE = "20";
    private static final String DEFAULT_OFFSET = "0";
    private static final String SORT_BY = "asc(first_name)";


    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{userId}")
    public User getUser(@PathVariable Long userId) {

        User user = userService.getById(userId).orElseThrow(() -> new NoSuchElementException("No user with id [" + userId + "] exists"));

        user.add(linkTo(methodOn(UserController.class)
                .getUser(user.getId()))
                .withSelfRel());

        user.add(linkTo(methodOn(UserController.class)
                .getUserOrders(user.getId()))
                .withRel("orders"));

        return user;
    }

    @GetMapping("/")
    public List<User> getUsers(@RequestParam(value = "sort_by", defaultValue = SORT_BY) Set<String> sortBy,
                               @RequestParam(value = "max", defaultValue = MAX_USERS_IN_RESPONSE) int max,
                               @RequestParam(value = "offset", defaultValue = DEFAULT_OFFSET) int offset) {
        HashMap<String, Boolean> sortingParams = RequestMapper.mapSortingParams(sortBy);
        List<User> users = userService.getUsers(sortingParams, max, offset);
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
    public Set<Order> getUserOrders(@PathVariable long userId) {
        User user = userService.getById(userId).orElseThrow(() -> new NoSuchElementException("No user with id [" + userId + "] exists"));

        Set<Order> userOrders = user.getOrders();
        if (userOrders.isEmpty()) {
            throw new NoEntitiesFoundException("No order exists for this user");
        }
        userOrders.forEach(order -> {
                    order.add(linkTo(methodOn(OrderController.class)
                            .getOrder(order.getId()))
                            .withSelfRel());

                    order.add(linkTo(methodOn(OrderController.class)
                            .deleteOrder(order.getId()))
                            .withRel("delete"));

                    order.add(linkTo(methodOn(OrderController.class)
                            .getOrderGiftCertificate(order.getId()))
                            .withRel("giftCertificate"));
                }
        );
        return userOrders;
    }
}
