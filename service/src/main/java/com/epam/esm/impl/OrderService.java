package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import com.epam.esm.User;
import com.epam.esm.mapper.RequestParamsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private static final String GIFT_CERTIFICATE_PROPERTY = "giftCertificate";
    private static final String USER_PROPERTY = "user";

    private final OrderRepository orderRepository;

    private final GiftCertificateService giftCertificateService;

    private final UserService userService;

    private final RequestParamsMapper requestParamsMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, GiftCertificateService giftCertificateService,
                        UserService userService, RequestParamsMapper requestParamsMapper) {
        this.orderRepository = orderRepository;
        this.giftCertificateService = giftCertificateService;
        this.userService = userService;
        this.requestParamsMapper = requestParamsMapper;
    }


    public Order getOrderById(Long orderId) {
        LOGGER.debug("Entering OrderService.getOrderById()");

        Order foundOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException(String.format("No order with id %s exists", orderId)));

        LOGGER.debug("Exiting OrderService.getOrderById()");
        return foundOrder;


    }


    public Page<Order> getOrders(List<String> sortBy, int max, int offset) {
        LOGGER.debug("Entering OrderService.getOrders()");

        Sort sortParams = requestParamsMapper.mapParams(sortBy);

        Page<Order> foundOrders = orderRepository.findAll(PageRequest.of(offset, max, sortParams));

        if (foundOrders.isEmpty()) {
            LOGGER.error("NoSuchElementException in OrderService.getOrders()\n" +
                    "No Orders exist");
            throw new NoSuchElementException("No Orders exist");
        }
        LOGGER.debug("Exiting OrderService.getOrders()");
        return foundOrders;
    }


    public boolean orderAlreadyExists(Order order) {
        LOGGER.debug("Entering OrderService.orderAlreadyExists()");

        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher(GIFT_CERTIFICATE_PROPERTY, ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher(USER_PROPERTY, ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Order> orderExample = Example.of(order, customExampleMatcher);
        boolean orderAlreadyExists = orderRepository.exists(orderExample);

        LOGGER.debug("Exiting OrderService.orderAlreadyExists()");
        return orderAlreadyExists;
    }


    public Order create(Order order) {
        LOGGER.debug("Entering OrderService.create()");
        initiateOrder(order);

        if (!orderAlreadyExists(order)) {
            order = orderRepository.save(order);
        } else {
            throw new DuplicateKeyException("Such order already exists");
        }
        LOGGER.debug("Exiting OrderService.create()");
        return order;
    }


    public boolean deleteOrder(Long id) {
        LOGGER.debug("Entering OrderService.deleteOrder()");
        boolean orderIsDeleted;

        orderRepository.deleteById(id);
        orderIsDeleted = !orderRepository.findById(id).isPresent();

        LOGGER.debug("Exiting OrderService.deleteOrder()");

        return orderIsDeleted;
    }

    private void initiateOrder(Order order) {
        Long userId = order.getUser().getId();
        String giftCertificateName = order.getGiftCertificate().getName();
        GiftCertificate giftCertificateFromOrder = giftCertificateService
                .findGiftCertificateByName(giftCertificateName);
        User user = userService.getById(userId);

        order.setUser(user);
        order.setGiftCertificate(giftCertificateFromOrder);
        order.setOrderCost(giftCertificateFromOrder.getPrice());
        order.setOrderDate(LocalDateTime.now());
    }
}

