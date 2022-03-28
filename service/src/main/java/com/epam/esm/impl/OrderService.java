package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import com.epam.esm.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private final OrderRepository orderRepository;

    private final GiftCertificateService giftCertificateService;

    private final UserService userService;

    @Autowired
    public OrderService(OrderRepository orderRepository, GiftCertificateService giftCertificateService, UserService userService) {
        this.orderRepository = orderRepository;
        this.giftCertificateService = giftCertificateService;
        this.userService = userService;
    }


    public Optional<Order> getOrderById(Long id) {
        LOGGER.info("Entering OrderService.getOrderById()");

        Optional<Order> foundOrder = orderRepository.getOrderById(id);

        LOGGER.info("Exiting OrderService.getOrderById()");
        return foundOrder;


    }


    public List<Order> getOrders(HashMap<String, Boolean> sortParams, int max, int offset) {
        LOGGER.info("Entering OrderService.getOrders()");

        List<Order> foundOrders = orderRepository.getOrders(sortParams, max, offset);

        LOGGER.info("Exiting OrderService.getOrders()");
        return foundOrders;
    }


    public boolean orderAlreadyExists(Order order) {
        LOGGER.info("Entering OrderService.orderAlreadyExists()");

        boolean orderAlreadyExists = orderRepository.orderAlreadyExists(order);

        LOGGER.info("Exiting OrderService.orderAlreadyExists()");
        return orderAlreadyExists;
    }


    public Optional<Order> create(Order order) {
        LOGGER.info("Entering OrderService.create()");
        Optional<Order> createdOrder;

        String giftCertificateName = order.getGiftCertificate().getName();
        Optional<GiftCertificate> giftCertificateFromOrder = giftCertificateService.getGiftCertificateByName(giftCertificateName);

        Long userId = order.getUser().getId();
        Optional<User> user = userService.getById(userId);


        order.setUser(user.orElseThrow(() -> new NoSuchElementException("User with id [" + userId + "] does not exist")));
        order.setGiftCertificate(giftCertificateFromOrder.orElseThrow(() -> new NoSuchElementException("Gift Certificate with name [" + giftCertificateName + "] does not exist")));

        order.setOrderCost(giftCertificateFromOrder.get().getPrice());
        order.setOrderDate(LocalDateTime.now());

        createdOrder = orderAlreadyExists(order) ? Optional.empty() : orderRepository.getOrderById(orderRepository.createOrder(order));

        LOGGER.info("Exiting OrderService.create()");
        return createdOrder;

    }


    public boolean deleteOrder(Long id) {
        LOGGER.info("Entering OrderService.deleteOrder()");

        boolean orderIsDeleted = orderRepository.delete(id);

        LOGGER.info("Exiting OrderService.deleteOrder()");
        return orderIsDeleted;
    }
}

