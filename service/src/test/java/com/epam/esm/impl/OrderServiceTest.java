
package com.epam.esm.impl;

import com.epam.esm.*;
import com.epam.esm.mapper.RequestParamsMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.mockito.Mockito.withSettings;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class OrderServiceTest {

    //mocks
    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class, withSettings().verboseLogging());

    private final GiftCertificateService giftCertificateService = Mockito.mock(GiftCertificateService.class, withSettings().verboseLogging());

    private final UserService userService = Mockito.mock(UserService.class, withSettings().verboseLogging());

    private final RequestParamsMapper requestParamsMapper = Mockito.mock(RequestParamsMapper.class, withSettings().verboseLogging());

    //class under test
    private final OrderService orderService = new OrderService(orderRepository, giftCertificateService, userService, requestParamsMapper);

    private Order order;

    private final long orderId = 1L;
    private final LocalDateTime orderDate = LocalDateTime.of(1997, Month.APRIL, 30, 14, 0);
    private final double orderCost = 200;

    private final List<Order> noOrders = new ArrayList<>();
    private List<Order> orders = new ArrayList<>(Arrays.asList(order));

    private final long userId = 1L;
    private final String firstName = "first name";
    private final String secondName = "second name";
    private final User user = new User.UserBuilder("username", "password")
            .id(userId)
            .firstName(firstName)
            .secondName(secondName)
            .build();
//    private final User user = new User(userId, firstName, secondName, new HashSet<>(noOrders));

    private GiftCertificate giftCertificateForOrder;
    private User userForOrder;


    private GiftCertificate giftCertificate;

    private Set<Tag> tags;


    private final int max = 20;
    private final int page = 0;
    private final Sort.Order sortOrder = new Sort.Order(Sort.Direction.ASC, "id");
    private final Sort sort = Sort.by(sortOrder);

    Page<Order> orderPage = new PageImpl<>(orders);

    private final List<String> sortParams = new ArrayList<>();

    @BeforeEach
    void setUp() {

        sortParams.add("id.desc");
        order = new Order.OrderBuilder()
                .id(1L)
                .orderDate(orderDate)
                .orderCost(orderCost)
                .giftCertificate(giftCertificate)
                .user(user)
                .build();

        Order secondOrder = new Order.OrderBuilder()
                .id(2L)
                .orderDate(orderDate)
                .orderCost(orderCost)
                .giftCertificate(giftCertificate)
                .user(user)
                .build();

        Order thirdOrder = new Order.OrderBuilder()
                .id(3L)
                .orderDate(orderDate)
                .orderCost(orderCost)
                .giftCertificate(giftCertificate)
                .user(user)
                .build();


        String giftCertificateName = "gift certificate name";
        giftCertificateForOrder = new GiftCertificate.GiftCertificateBuilder((giftCertificateName)).build();
        userForOrder = new User.UserBuilder("username", "password").build();

        long giftCertificateId = 1L;
        String giftCertificateDescription = "description";
        long giftCertificatePrice = 200L;
        long giftCertificateDuration = 360L;

        giftCertificate = new GiftCertificate.GiftCertificateBuilder(giftCertificateName)
                .id(giftCertificateId)
                .description(giftCertificateDescription)
                .price(giftCertificatePrice)
                .duration(giftCertificateDuration)
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .tags(tags)
                .build();

        orders = Arrays.asList(
                order,
                secondOrder,
                thirdOrder
        );

        tags = new HashSet<>(Arrays.asList(
                new Tag(1L, "first tag"),
                new Tag(2L, "second tag"),
                new Tag(3L, "third tag")
        ));

    }

    @AfterEach
    void tearDown() {
        order = new Order();
        giftCertificateForOrder = new GiftCertificate();
        userForOrder = new User();
        orders = new ArrayList<>();
        tags = new HashSet<>();
        giftCertificate = new GiftCertificate();
        sortParams.clear();
    }


    @Test
    void testGetOrderById_idExists() {
        Order order = new Order.OrderBuilder()
                .id(orderId)
                .orderDate(orderDate)
                .orderCost(orderCost)
                .giftCertificate(giftCertificate)
                .user(user)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order returnOrder = orderService.getOrderById(orderId);

        verify(orderRepository).findById(orderId);
        assertTrue(nonNull(returnOrder));
        assertEquals(order, returnOrder);
    }

    @Test
    void testGetOrderById_idDoesNotExist() {
        long nonExistingOrderId = 1111L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> orderService.getOrderById(nonExistingOrderId));
        String expectedMessage = String.format("No order with id %s exists", nonExistingOrderId);
        String actualMessage = noSuchElementException.getMessage();

        verify(orderRepository).findById(nonExistingOrderId);
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void testGetOrders_ordersExist() {
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(orderRepository.findAll(PageRequest.of(page, max, sort))).thenReturn(orderPage);

        Page<Order> returnOrders = orderService.getOrders(sortParams, max, page);

        verify(orderRepository).findAll(PageRequest.of(page, max, sort));
        assertEquals(orderPage, returnOrders);
    }

    @Test
    void testGetOrders_noOrdersExist() {

        Page<Order> emptyOrderPage = new PageImpl<>(new ArrayList<>());
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(orderRepository.findAll(PageRequest.of(page, max, sort))).thenReturn(emptyOrderPage);

        Exception noSuchElementException = assertThrows(NoSuchElementException.class,
                () -> orderService.getOrders(sortParams, max, page));

        String expectedMessage = "No Orders exist";
        String actualMessage = noSuchElementException.getMessage();

        verify(requestParamsMapper).mapParams(sortParams);
        verify(orderRepository).findAll(PageRequest.of(page, max, sort));
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testOrderAlreadyExists_true() {
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("giftCertificate", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("user", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Order> orderExample = Example.of(order, customExampleMatcher);
        when(orderRepository.exists(orderExample)).thenReturn(true);

        boolean result = orderService.orderAlreadyExists(order);

        assertTrue(result);
    }

    @Test
    void testOrderAlreadyExists_false() {
        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("giftCertificate", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("user", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<Order> orderExample = Example.of(order, customExampleMatcher);
        when(orderRepository.exists(orderExample)).thenReturn(false);

        boolean result = orderService.orderAlreadyExists(order);

        assertFalse(result);
    }


    @Test
    void testCreateOrder_userAndGiftCertificateExist() {
        Order orderFromClient = new Order.OrderBuilder()
                .giftCertificate(giftCertificateForOrder)
                .user(userForOrder)
                .build();

        Order savedOrder = new Order.OrderBuilder()
                .id(orderId)
                .orderDate(orderDate)
                .orderCost(orderCost)
                .giftCertificate(giftCertificate)
                .user(user)
                .build();

        when(orderService.orderAlreadyExists(orderFromClient)).thenReturn(false);
        when(giftCertificateService
                .findGiftCertificateByName(giftCertificateForOrder.getName())).thenReturn(giftCertificate);
        orderFromClient.setGiftCertificate(giftCertificate);
        when(userService.findUserById(userForOrder.getId())).thenReturn(user);
        orderFromClient.setUser(user);
        orderFromClient.setOrderCost(giftCertificate.getPrice());
        when(orderRepository.save(orderFromClient)).thenReturn(savedOrder);
        orderFromClient.setId(orderId);

        Order createdOrder = orderService.create(orderFromClient);
        orderFromClient.setOrderDate(orderDate);

        verify(orderRepository).save(orderFromClient);
        assertTrue(nonNull(createdOrder));
        assertEquals(orderFromClient, createdOrder);
    }

    @Test
    void testCreateOrder_userDoesNotExist() {
        long nonExistingUserId = 9999L;
        User nonExistingUser = new User
                .UserBuilder("username", "password")
                .id(nonExistingUserId)
                .build();

        Order orderFromClient = new Order
                .OrderBuilder()
                .giftCertificate(giftCertificateForOrder)
                .user(nonExistingUser)
                .build();

        when(userService.findUserById(nonExistingUserId))
                .thenThrow(new NoSuchElementException(String
                        .format("User with id [%s] does not exist", userId)));

        Exception noSuchElementException = assertThrows(NoSuchElementException.class,
                () -> orderService.create(orderFromClient));
        String expectedMessage = String
                .format("User with id [%s] does not exist", userId);
        String actualMessage = noSuchElementException.getMessage();

        verify(userService).findUserById(nonExistingUserId);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testCreateOrder_giftCertificateDoesNotExist() {
        String nonExistingGiftCertificateName = "non-existing gift certificate name";
        GiftCertificate nonExistingGiftCertificate = new GiftCertificate
                .GiftCertificateBuilder(nonExistingGiftCertificateName)
                .build();

        Order orderFromClient = new Order
                .OrderBuilder()
                .giftCertificate(nonExistingGiftCertificate)
                .user(userForOrder)
                .build();

        when(giftCertificateService.findGiftCertificateByName(nonExistingGiftCertificateName))
                .thenThrow(new NoSuchElementException(String
                        .format("Gift Certificate with name [%s] does not exist", nonExistingGiftCertificateName)));

        Exception noSuchElementException = assertThrows(NoSuchElementException.class,
                () -> orderService.create(orderFromClient));
        String expectedMessage = String
                .format("Gift Certificate with name [%s] does not exist", nonExistingGiftCertificateName);
        String actualMessage = noSuchElementException.getMessage();

        verify(giftCertificateService).findGiftCertificateByName(nonExistingGiftCertificateName);
        assertEquals(expectedMessage, actualMessage);

    }
//TODO -- why returns false from repo
//    @Test
//    void testCreateOrder_orderAlreadyExist() {
//
//        Order orderFromClient = new Order.OrderBuilder()
//                .giftCertificate(giftCertificateForOrder)
//                .user(userForOrder)
//                .build();
//
//        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAny()
//                .withMatcher("giftCertificate", ExampleMatcher.GenericPropertyMatchers.exact())
//                .withMatcher("user", ExampleMatcher.GenericPropertyMatchers.exact());
//
//        Example<Order> orderExample = Example.of(order, customExampleMatcher);
//
//        when(giftCertificateService
//                .findGiftCertificateByName(giftCertificateForOrder.getName())).thenReturn(giftCertificate);
//        when(userService.findUserById(userForOrder.getId())).thenReturn(user);
//        when(orderRepository.exists(orderExample)).thenReturn(true);
//
//        Exception duplicateKeyException = assertThrows(DuplicateKeyException.class,
//                () -> orderService.create(orderFromClient));
//        String expectedMessage = "Such order already exists";
//        String actualMessage = duplicateKeyException.getMessage();
//
//        verify(orderRepository).exists(orderExample);
//        assertEquals(expectedMessage, actualMessage);
//
//    }


    @Test
    void testDeleteOrder_idExists() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        boolean orderIsDeleted = orderService.deleteOrder(orderId);

        verify(orderRepository).findById(orderId);
        assertTrue(orderIsDeleted);
    }

    @Test
    void testDeleteOrder_idDoesNotExist() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        boolean orderIsDeleted = orderService.deleteOrder(orderId);

        verify(orderRepository).findById(orderId);
        assertFalse(orderIsDeleted);
    }

}