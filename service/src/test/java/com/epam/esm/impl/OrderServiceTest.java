package com.epam.esm.impl;

import com.epam.esm.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    //mocks
    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class, withSettings().verboseLogging());

    private final GiftCertificateService giftCertificateService = Mockito.mock(GiftCertificateService.class, withSettings().verboseLogging());

    private final UserService userService = Mockito.mock(UserService.class, withSettings().verboseLogging());

    //class under test
    private final OrderService orderService = new OrderService(orderRepository, giftCertificateService, userService);

    private final long orderId = 1L;
    private final LocalDateTime orderDate = LocalDateTime.now();
    private final double orderCost = 99.9;
    private final GiftCertificate giftCertificate = new GiftCertificate();
    private final User user = new User();

    @Test
    void testGetOrderById_idExists() {
        Order order = new Order(orderId, orderDate,orderCost,giftCertificate,user);

        when(tagRepository.getTagById(tagId)).thenReturn(Optional.of(order));

        Optional<Tag> returnTag = tagService.getById(tagId);

        verify(tagRepository).getTagById(tagId);
        assertTrue(returnTag.isPresent());
        assertEquals(order, returnTag.get());
    }

    @Test
    void getOrders() {
    }

    @Test
    void orderAlreadyExists() {
    }

    @Test
    void create() {
    }

    @Test
    void delete() {
    }
}