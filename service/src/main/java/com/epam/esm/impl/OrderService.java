package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import com.epam.esm.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final CertificateService certificateService;
    private final UserService userService;

    @Autowired
    public OrderService(OrderRepository orderRepository, CertificateService certificateService, UserService userService) {
        this.orderRepository = orderRepository;
        this.certificateService = certificateService;
        this.userService = userService;
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.getOrder(id);
    }


    public Set<Order> getOrders(String order, int max) {
        return orderRepository.getOrders(order, max);
    }



    public boolean orderAlreadyExists(Order order) {
        return orderRepository.orderAlreadyExists(order);
    }

    @Transactional
    public Optional<Order> create(Order order) {

        String giftCertificateName = order.getGiftCertificate().getName();
        Long userId = order.getUser().getId();
        GiftCertificate giftCertificateFromOrder = certificateService.getByName(giftCertificateName)
                .orElseThrow(() -> new NoSuchElementException("gift certificate [" + giftCertificateName + "] doesn't exist"));

        User user = userService.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("user [" + userId + "] doesn't exidt"));

        order.setUser(user);
        order.setGiftCertificate(giftCertificateFromOrder);
        order.setOrderCost(giftCertificateFromOrder.getPrice());
        order.setOrderDate(LocalDateTime.now());

        return orderAlreadyExists(order) ? Optional.empty() : orderRepository.getOrder(orderRepository.createOrder(order));
    }

    public boolean delete(Long id) {
        return orderRepository.delete(id);
    }
}

