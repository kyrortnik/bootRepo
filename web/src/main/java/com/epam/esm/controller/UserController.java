package com.epam.esm.controller;

import com.epam.esm.Order;
import com.epam.esm.User;
import com.epam.esm.dto.LoginDto;
import com.epam.esm.impl.UserService;
import com.epam.esm.util.GetMethodProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public User getUser(@PathVariable Long userId) {
        LOGGER.debug("Entering UserController.getUser()");

        User user = userService.getUserById(userId);

        user.add(linkTo(methodOn(UserController.class)
                .getUser(user.getId()))
                .withSelfRel());

        user.add(linkTo(methodOn(UserController.class)
                .getUserOrders(user.getId()))
                .withRel("orders"));

        LOGGER.debug("Exiting UserController.getUser()");
        return user;
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Page<User> getUsers(
            @RequestParam(value = "sort_by", defaultValue = GetMethodProperty.DEFAULT_SORT_BY) List<String> sortBy,
            @RequestParam(value = "max", defaultValue = GetMethodProperty.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "offset", defaultValue = GetMethodProperty.DEFAULT_OFFSET) int offset) {
        LOGGER.debug("Entering UserController.getUsers()");

        Page<User> users = userService.getUsers(sortBy, max, offset);

        users.forEach(user -> {
                    user.add(linkTo(methodOn(UserController.class)
                            .getUser(user.getId()))
                            .withSelfRel());

                    user.add(linkTo(methodOn(UserController.class)
                            .getUserOrders(user.getId()))
                            .withRel("orders"));
                }
        );

        LOGGER.debug("Exiting UserController.getUsers()");
        return users;
    }


    @GetMapping("/{userId}/orders")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Set<Order> getUserOrders(@PathVariable long userId) {
        LOGGER.debug("Entering UserController.getUserOrders()");

        Set<Order> userOrders = userService.getUserOrders(userId);

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

        LOGGER.debug("Exiting UserController.getUserOrders()");
        return userOrders;
    }

    /**
     * Signs in an existing user
     *
     * @param loginDto Dto class containing User parameters
     * @return String representing JWT
     */
    @PostMapping("/signin")
    public String signin(@RequestBody LoginDto loginDto) {
        return userService.signin(loginDto.getUsername(), loginDto.getPassword());
    }

    /**
     * Creates new user with GUEST_ROLE
     *
     * @param loginDto Dto class containing User parameters
     * @return created User
     */
    @PostMapping("/signup")
    public User signup(@RequestBody LoginDto loginDto) {
        return userService.signup(loginDto);
    }


}
