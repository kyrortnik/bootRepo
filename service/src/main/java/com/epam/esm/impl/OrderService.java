package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class OrderService {

    private final OrderRepository orderRepository;

    private final CertificateService certificateService;

    @Autowired
    public OrderService(OrderRepository orderRepository, CertificateService certificateService) {
        this.orderRepository = orderRepository;
        this.certificateService = certificateService;
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.getOrder(id);
    }

    public Set<Order> getOrders(String order, int max) {
        return orderRepository.getOrders(order, max);
    }

    @Transactional
    public Optional<Order> create(Order order) {

        order.setOrderDate(LocalDateTime.now());
        Set<GiftCertificate> giftCertificates = order.getGiftCertificates();

        giftCertificates.forEach(giftCertificate -> {
            Optional<GiftCertificate> giftCertificatesFromOrder = certificateService.getByName(giftCertificate.getName());
            if (!giftCertificatesFromOrder.isPresent()) {
                throw new NoSuchElementException("Such Gift Certificate is not present");
            }
            order.updateTotalOrderAmount(giftCertificatesFromOrder.get().getPrice());
        });

        long createdOrderId = orderRepository.createOrder(order);
        return orderRepository.getOrder(createdOrderId);
    }

    public boolean delete(Long id) {
        return orderRepository.delete(id);
    }
}

