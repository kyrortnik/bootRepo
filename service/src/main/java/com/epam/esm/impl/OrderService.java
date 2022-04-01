package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import com.epam.esm.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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


    public Optional<Order> getOrderById(Long orderId) {
//        LOGGER.debug("Entering OrderService.getOrderById()");

        Optional<Order> foundOrder = orderRepository.findById(orderId);

//        LOGGER.debug("Exiting OrderService.getOrderById()");
        return foundOrder;


    }


    public Page<Order> getOrders(Sort sortParams, int max, int offset) {
        LOGGER.debug("Entering OrderService.getOrders()");

        Page<Order> foundOrders = orderRepository.findAll(PageRequest.of(offset, max, sortParams));

        LOGGER.debug("Exiting OrderService.getOrders()");
        return foundOrders;
    }


    public boolean orderAlreadyExists(Order order) {
        LOGGER.debug("Entering OrderService.orderAlreadyExists()");

        Example<Order> orderExample = Example.of(order);

        boolean orderAlreadyExists = orderRepository.exists(orderExample);

        LOGGER.debug("Exiting OrderService.orderAlreadyExists()");
        return orderAlreadyExists;
    }


    public Optional<Order> create(Order order) {
        LOGGER.debug("Entering OrderService.create()");
        Optional<Order> createdOrder;

        String giftCertificateName = order.getGiftCertificate().getName();
        Optional<GiftCertificate> giftCertificateFromOrder = giftCertificateService.findGiftCertificateByName(giftCertificateName);

        Long userId = order.getUser().getId();
        Optional<User> user = userService.getById(userId);


        order.setUser(user.orElseThrow(() -> new NoSuchElementException("User with id [" + userId + "] does not exist")));
        order.setGiftCertificate(giftCertificateFromOrder.orElseThrow(() -> new NoSuchElementException("Gift Certificate with name [" + giftCertificateName + "] does not exist")));

        order.setOrderCost(giftCertificateFromOrder.get().getPrice());
        order.setOrderDate(LocalDateTime.now());

        createdOrder = orderAlreadyExists(order) ? Optional.empty() : Optional.of(orderRepository.save(order));

        LOGGER.debug("Exiting OrderService.create()");
        return createdOrder;

    }


    public boolean deleteOrder(Long id) {
        LOGGER.debug("Entering OrderService.deleteOrder()");
        boolean result;

        orderRepository.deleteById(id);
        result = !orderRepository.findById(id).isPresent();

        LOGGER.debug("Exiting OrderService.deleteOrder()");

        return result;


    }
}

