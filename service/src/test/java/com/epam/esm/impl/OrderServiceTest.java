package com.epam.esm.impl;

import com.epam.esm.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    //mocks
    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class, withSettings().verboseLogging());

    private final GiftCertificateService giftCertificateService = Mockito.mock(GiftCertificateService.class, withSettings().verboseLogging());

    private final UserService userService = Mockito.mock(UserService.class, withSettings().verboseLogging());

    //class under test
    private final OrderService orderService = new OrderService(orderRepository, giftCertificateService, userService);

    private final String sortingOrder = "ASC";
    private final int max = 20;
    private final int offset = 0;

    private final Set<Tag> tagsSet = new HashSet<>(Arrays.asList(
            new Tag(1L, "first tag"),
            new Tag(2L, "second tag"),
            new Tag(3L, "third tag")
    ));

    private final long orderId = 1L;
    private final LocalDateTime orderDate = LocalDateTime.now();
    private final double orderCost = 99.9;

    private final long giftCertificateId = 1L;
    private final String giftCertificateName = "gift certificate name";
    private final String giftCertificateDescription = "description";
    private final long giftCertificatePrice = 200L;
    private final long giftCertificateDuration = 360L;
    private final LocalDateTime giftCertificateCreateDate = LocalDateTime.now();
    private final LocalDateTime giftCertificateLastUpdateDate = LocalDateTime.now();
    private final GiftCertificate giftCertificate = new GiftCertificate(
            giftCertificateId,
            giftCertificateName,
            giftCertificateDescription,
            giftCertificatePrice,
            giftCertificateDuration,
            giftCertificateCreateDate,
            giftCertificateLastUpdateDate,
            tagsSet);

    private final long userId = 1L;
    private final String firstName = "first name";
    private final String secondName = "second name";

    private final Set<Order> noOrders = new HashSet<>();

    private final User user = new User(userId, firstName, secondName, noOrders);

    private final GiftCertificate giftCertificateForOrder = new GiftCertificate(giftCertificateName);
    private final User userForOrder = new User(userId);


    private final Order firstOrder = new Order(1L, orderDate, orderCost, giftCertificate, user);
    private final Order secondOrder = new Order(2L, orderDate, orderCost, giftCertificate, user);
    private final Order thirdOrder = new Order(3L, orderDate, orderCost, giftCertificate, user);


    private final Set<Order> ordersSet = new HashSet<>(Arrays.asList(
            firstOrder,
            secondOrder,
            thirdOrder
    ));


    @Test
    void testGetOrderById_idExists() {
        Order order = new Order(orderId, orderDate, orderCost, giftCertificate, user);

        when(orderRepository.getOrderById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> returnOrder = orderService.getOrderById(orderId);

        verify(orderRepository).getOrderById(orderId);
        assertTrue(returnOrder.isPresent());
        assertEquals(order, returnOrder.get());
    }

    @Test
    void testGetOrderById_idDoesNotExist() {
        when(orderRepository.getOrderById(orderId)).thenReturn(Optional.empty());

        Optional<Order> returnOrder = orderService.getOrderById(orderId);

        verify(orderRepository).getOrderById(orderId);
        assertFalse(returnOrder.isPresent());
        assertEquals(Optional.empty(), returnOrder);
    }

//    @Test
//    void testGetOrders_ordersExist() {
//
//        when(orderRepository.getOrders(sortingOrder, max, offset)).thenReturn(ordersSet);
//
//        Set<Order> returnOrders = orderService.getOrders(sortingOrder, max, offset);
//
//        verify(orderRepository).getOrders(sortingOrder, max, offset);
//        assertEquals(ordersSet, returnOrders);
//    }
//
//    @Test
//    void testGetOrders_noOrdersExist() {
//
//        when(orderRepository.getOrders(sortingOrder, max, offset)).thenReturn(noOrders);
//
//        Set<Order> returnOrders = orderService.getOrders(sortingOrder, max, offset);
//
//        verify(orderRepository).getOrders(sortingOrder, max, offset);
//        assertEquals(noOrders, returnOrders);
//    }

    @Test
    void testOrderAlreadyExists_true() {
        when(orderRepository.orderAlreadyExists(firstOrder)).thenReturn(true);

        boolean result = orderService.orderAlreadyExists(firstOrder);

        assertTrue(result);
    }

    @Test
    void testOrderAlreadyExists_false() {
        when(orderRepository.orderAlreadyExists(firstOrder)).thenReturn(false);

        boolean result = orderService.orderAlreadyExists(firstOrder);

        verify(orderRepository).orderAlreadyExists(firstOrder);
        assertFalse(result);
    }


    @Test
    void testCreateOrder_userAndGiftCertificateExist() {
        Order orderFromClient = new Order(giftCertificateForOrder,userForOrder);
        when(giftCertificateService.getGiftCertificateByName(giftCertificateForOrder.getName())).thenReturn(Optional.of(giftCertificate));
        orderFromClient.setGiftCertificate(giftCertificate);
        when(userService.getById(userForOrder.getId())).thenReturn(Optional.of(user));
        orderFromClient.setUser(user);
        orderFromClient.setOrderCost(giftCertificate.getPrice());
        orderFromClient.setOrderDate(LocalDateTime.now());
        when(orderRepository.createOrder(orderFromClient)).thenReturn(orderId);
        when(orderRepository.getOrderById(orderId)).thenReturn(Optional.of(orderFromClient));
        orderFromClient.setId(orderId);

        Optional<Order> createdOrder = orderService.create(orderFromClient);

        verify(giftCertificateService).getGiftCertificateByName(giftCertificateForOrder.getName());
        verify(userService).getById(userForOrder.getId());
        verify(orderRepository).createOrder(orderFromClient);
        verify(orderRepository).getOrderById(orderId);
        assertTrue(createdOrder.isPresent());
        assertEquals(createdOrder.get(),orderFromClient);
    }

    @Test
    void testCreateOrder_userDoesNotExist() {
        long nonExistingUserId = 9999L;
        User nonExistingUser = new User(nonExistingUserId);
        Order orderFromClient = new Order(giftCertificateForOrder,nonExistingUser);
        when(userService.getById(nonExistingUserId)).thenThrow(new NoSuchElementException("User with id [" + nonExistingUserId + "] does not exist"));

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> orderService.create(orderFromClient));
        String expectedMessage = "User with id [" + nonExistingUserId + "] does not exist";
        String actualMessage = noSuchElementException.getMessage();

        verify(userService).getById(nonExistingUserId);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testCreateOrder_giftCertificateDoesNotExist() {
        String nonExistingGiftCertificateName = "non-existing gift certificate name";
        GiftCertificate nonExistingGiftCertificate = new GiftCertificate(nonExistingGiftCertificateName);
        Order orderFromClient = new Order(nonExistingGiftCertificate,userForOrder);
        when(giftCertificateService.getGiftCertificateByName(nonExistingGiftCertificateName)).thenThrow(new NoSuchElementException("Gift Certificate with name [" + nonExistingGiftCertificateName +"] does not exist"));

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> orderService.create(orderFromClient));
        String expectedMessage = "Gift Certificate with name [" + nonExistingGiftCertificateName +"] does not exist";
        String actualMessage = noSuchElementException.getMessage();

        verify(giftCertificateService).getGiftCertificateByName(nonExistingGiftCertificateName);
        assertEquals(expectedMessage, actualMessage);

    }

//    @Test
//    void testCreateOrder_orderAlreadyExist() {
//
//        Order orderFromClient = new Order(giftCertificateForOrder,userForOrder);
//        when(giftCertificateService.getGiftCertificateByName(giftCertificateForOrder.getName())).thenReturn(Optional.of(giftCertificate));
//        orderFromClient.setGiftCertificate(giftCertificate);
//        when(userService.getById(userForOrder.getId())).thenReturn(Optional.of(user));
//        orderFromClient.setUser(user);
//        orderFromClient.setOrderCost(giftCertificate.getPrice());
//        orderFromClient.setOrderDate(LocalDateTime.now());
//        when(orderRepository.createOrder(orderFromClient)).thenThrow(ConstraintViolationException.class);
////        when(orderRepository.getOrderById(orderId)).thenReturn(Optional.of(orderFromClient));
////        orderFromClient.setId(orderId);
//
//        Optional<Order> createdOrder = orderService.create(orderFromClient);
//
//        verify(giftCertificateService).getGiftCertificateByName(giftCertificateForOrder.getName());
//        verify(userService).getById(userForOrder.getId());
////        verify(orderRepository).createOrder(orderFromClient);
////        verify(orderRepository).getOrderById(orderId);
//        assertFalse(createdOrder.isPresent());

//    }


    @Test
    void testDeleteOrder_idExists() {
        when(orderRepository.delete(orderId)).thenReturn(true);

        boolean result = orderService.delete(orderId);

        verify(orderRepository).delete(orderId);
        assertTrue(result);
    }

    @Test
    void testDeleteOrder_idDoesNotExist() {
        when(orderRepository.delete(orderId)).thenReturn(false);

        boolean result = orderService.delete(orderId);

        verify(orderRepository).delete(orderId);
        assertFalse(result);
    }

}