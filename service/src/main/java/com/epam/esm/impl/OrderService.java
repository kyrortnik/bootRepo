package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import com.epam.esm.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class OrderService {

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

        return orderRepository.getOrderById(id);

    }


    public List<Order> getOrders(HashMap<String, Boolean> sortParams, int max, int offset) {
        return orderRepository.getOrders(sortParams, max, offset);
    }


    public boolean orderAlreadyExists(Order order) {
        return orderRepository.orderAlreadyExists(order);
    }


    public Optional<Order> create(Order order) {

        String giftCertificateName = order.getGiftCertificate().getName();
        Optional<GiftCertificate> giftCertificateFromOrder = giftCertificateService.getGiftCertificateByName(giftCertificateName);

        Long userId = order.getUser().getId();
        Optional<User> user = userService.getById(userId);


        order.setUser(user.orElseThrow(() -> new NoSuchElementException("User with id [" + userId + "] does not exist")));
        order.setGiftCertificate(giftCertificateFromOrder.orElseThrow(() -> new NoSuchElementException("Gift Certificate with name [" + giftCertificateName + "] does not exist")));

        order.setOrderCost(giftCertificateFromOrder.get().getPrice());
        order.setOrderDate(LocalDateTime.now());

        return orderAlreadyExists(order) ? Optional.empty() : orderRepository.getOrderById(orderRepository.createOrder(order));

    }


    public boolean delete(Long id) {
        return orderRepository.delete(id);
    }
}

