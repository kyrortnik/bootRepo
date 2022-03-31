//package com.epam.esm.impl;
//
//import com.epam.esm.*;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class OrderServiceTest {
//
//    //mocks
//    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class, withSettings().verboseLogging());
//
//    private final GiftCertificateService giftCertificateService = Mockito.mock(GiftCertificateService.class, withSettings().verboseLogging());
//
//    private final UserService userService = Mockito.mock(UserService.class, withSettings().verboseLogging());
//
//    //class under test
//    private final OrderService orderService = new OrderService(orderRepository, giftCertificateService, userService);
//
//
//    private final long orderId = 1L;
//    private final LocalDateTime orderDate = LocalDateTime.now();
//    private final double orderCost = 99.9;
//
//    private final List<Order> noOrders = new ArrayList<>();
//    private List<Order> orders;
//
//    private final long userId = 1L;
//    private final String firstName = "first name";
//    private final String secondName = "second name";
//    private final User user = new User(userId, firstName, secondName, new HashSet<>(noOrders));
//
//    private GiftCertificate giftCertificateForOrder;
//    private User userForOrder;
//
//    private Order order;
//
//    private GiftCertificate giftCertificate;
//
//    private Set<Tag> tags;
//
//    private final HashMap<String, Boolean> sortParams = new HashMap<>();
//    private final int max = 20;
//    private final int offset = 0;
//
//
//    @BeforeEach
//    void setUp() {
//
//        order = new Order(1L, orderDate, orderCost, giftCertificate, user);
//        Order secondOrder = new Order(2L, orderDate, orderCost, giftCertificate, user);
//        Order thirdOrder = new Order(3L, orderDate, orderCost, giftCertificate, user);
//
//        String giftCertificateName = "gift certificate name";
//        giftCertificateForOrder = new GiftCertificate.GiftCertificateBuilder((giftCertificateName)).build();
//        userForOrder = new User(userId);
//
//        long giftCertificateId = 1L;
//        String giftCertificateDescription = "description";
//        long giftCertificatePrice = 200L;
//        long giftCertificateDuration = 360L;
//
//        giftCertificate = new GiftCertificate();
//        giftCertificate.setId(giftCertificateId);
//        giftCertificate.setName(giftCertificateName);
//        giftCertificate.setDescription(giftCertificateDescription);
//        giftCertificate.setPrice(giftCertificatePrice);
//        giftCertificate.setDuration(giftCertificateDuration);
//        giftCertificate.setCreateDate(LocalDateTime.now());
//        giftCertificate.setLastUpdateDate(LocalDateTime.now());
//        giftCertificate.setTags(tags);
//
//        orders = Arrays.asList(
//                order,
//                secondOrder,
//                thirdOrder
//        );
//
//        tags = new HashSet<>(Arrays.asList(
//                new Tag(1L, "first tag"),
//                new Tag(2L, "second tag"),
//                new Tag(3L, "third tag")
//        ));
//
//        sortParams.put("id", true);
//
//    }
//
//    @AfterEach
//    void tearDown() {
//        order = new Order();
//        giftCertificateForOrder = new GiftCertificate();
//        userForOrder = new User();
//        orders = new ArrayList<>();
//        tags = new HashSet<>();
//        giftCertificate = new GiftCertificate();
//        sortParams.clear();
//    }
//
//
//    @Test
//    void testGetOrderById_idExists() {
//        Order order = new Order(orderId, orderDate, orderCost, giftCertificate, user);
//
//        when(orderRepository.getOrderById(orderId)).thenReturn(Optional.of(order));
//
//        Optional<Order> returnOrder = orderService.getOrderById(orderId);
//
//        verify(orderRepository).getOrderById(orderId);
//        assertTrue(returnOrder.isPresent());
//        assertEquals(order, returnOrder.get());
//    }
//
//    @Test
//    void testGetOrderById_idDoesNotExist() {
//        when(orderRepository.getOrderById(orderId)).thenReturn(Optional.empty());
//
//        Optional<Order> returnOrder = orderService.getOrderById(orderId);
//
//        verify(orderRepository).getOrderById(orderId);
//        assertFalse(returnOrder.isPresent());
//        assertEquals(Optional.empty(), returnOrder);
//    }
//
//    @Test
//    void testGetOrders_ordersExist() {
//
//        when(orderRepository.getOrders(sortParams, max, offset)).thenReturn(orders);
//
//        List<Order> returnOrders = orderService.getOrders(sortParams, max, offset);
//
//        verify(orderRepository).getOrders(sortParams, max, offset);
//        assertEquals(orders, returnOrders);
//    }
//
//    @Test
//    void testGetOrders_noOrdersExist() {
//
//        when(orderRepository.getOrders(sortParams, max, offset)).thenReturn(noOrders);
//
//        List<Order> returnOrders = orderService.getOrders(sortParams, max, offset);
//
//        verify(orderRepository).getOrders(sortParams, max, offset);
//        assertEquals(noOrders, returnOrders);
//    }
//
//    @Test
//    void testOrderAlreadyExists_true() {
//        when(orderRepository.orderAlreadyExists(order)).thenReturn(true);
//
//        boolean result = orderService.orderAlreadyExists(order);
//
//        assertTrue(result);
//    }
//
//    @Test
//    void testOrderAlreadyExists_false() {
//        when(orderRepository.orderAlreadyExists(order)).thenReturn(false);
//
//        boolean result = orderService.orderAlreadyExists(order);
//
//        verify(orderRepository).orderAlreadyExists(order);
//        assertFalse(result);
//    }
//
//
//    @Test
//    void testCreateOrder_userAndGiftCertificateExist() {
//        Order orderFromClient = new Order(giftCertificateForOrder, userForOrder);
//        when(giftCertificateService.getGiftCertificateByName(giftCertificateForOrder.getName())).thenReturn(Optional.of(giftCertificate));
//        orderFromClient.setGiftCertificate(giftCertificate);
//        when(userService.getById(userForOrder.getId())).thenReturn(Optional.of(user));
//        orderFromClient.setUser(user);
//        orderFromClient.setOrderCost(giftCertificate.getPrice());
//        orderFromClient.setOrderDate(LocalDateTime.now());
//        when(orderRepository.createOrder(orderFromClient)).thenReturn(orderId);
//        when(orderRepository.getOrderById(orderId)).thenReturn(Optional.of(orderFromClient));
//        orderFromClient.setId(orderId);
//
//        Optional<Order> createdOrder = orderService.create(orderFromClient);
//
//        verify(giftCertificateService).getGiftCertificateByName(giftCertificateForOrder.getName());
//        verify(userService).getById(userForOrder.getId());
//        verify(orderRepository).createOrder(orderFromClient);
//        verify(orderRepository).getOrderById(orderId);
//        assertTrue(createdOrder.isPresent());
//        assertEquals(createdOrder.get(), orderFromClient);
//    }
//
//    @Test
//    void testCreateOrder_userDoesNotExist() {
//        long nonExistingUserId = 9999L;
//        User nonExistingUser = new User(nonExistingUserId);
//        Order orderFromClient = new Order(giftCertificateForOrder, nonExistingUser);
//        when(userService.getById(nonExistingUserId)).thenThrow(new NoSuchElementException("User with id [" + nonExistingUserId + "] does not exist"));
//
//        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> orderService.create(orderFromClient));
//        String expectedMessage = "User with id [" + nonExistingUserId + "] does not exist";
//        String actualMessage = noSuchElementException.getMessage();
//
//        verify(userService).getById(nonExistingUserId);
//        assertEquals(expectedMessage, actualMessage);
//    }
//
//    @Test
//    void testCreateOrder_giftCertificateDoesNotExist() {
//        String nonExistingGiftCertificateName = "non-existing gift certificate name";
//        GiftCertificate nonExistingGiftCertificate = new GiftCertificate.GiftCertificateBuilder(nonExistingGiftCertificateName).build();
//        Order orderFromClient = new Order(nonExistingGiftCertificate, userForOrder);
//        when(giftCertificateService.getGiftCertificateByName(nonExistingGiftCertificateName)).thenThrow(new NoSuchElementException("Gift Certificate with name [" + nonExistingGiftCertificateName + "] does not exist"));
//
//        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> orderService.create(orderFromClient));
//        String expectedMessage = "Gift Certificate with name [" + nonExistingGiftCertificateName + "] does not exist";
//        String actualMessage = noSuchElementException.getMessage();
//
//        verify(giftCertificateService).getGiftCertificateByName(nonExistingGiftCertificateName);
//        assertEquals(expectedMessage, actualMessage);
//
//    }
//
//    @Test
//    void testCreateOrder_orderAlreadyExist() {
//
//        Order orderFromClient = new Order(giftCertificateForOrder, userForOrder);
//        when(giftCertificateService.getGiftCertificateByName(giftCertificateForOrder.getName())).thenReturn(Optional.of(giftCertificate));
//        orderFromClient.setGiftCertificate(giftCertificate);
//        when(userService.getById(userForOrder.getId())).thenReturn(Optional.of(user));
//        orderFromClient.setUser(user);
//        orderFromClient.setOrderCost(giftCertificate.getPrice());
//        orderFromClient.setOrderDate(LocalDateTime.now());
//        when(orderRepository.orderAlreadyExists(orderFromClient)).thenReturn(true);
//
//        Optional<Order> createdOrder = orderService.create(orderFromClient);
//
//        verify(giftCertificateService).getGiftCertificateByName(giftCertificateForOrder.getName());
//        verify(userService).getById(userForOrder.getId());
//        assertEquals(createdOrder, Optional.empty());
//
//    }
//
//
//    @Test
//    void testDeleteOrder_idExists() {
//        when(orderRepository.delete(orderId)).thenReturn(true);
//
//        boolean result = orderService.deleteOrder(orderId);
//
//        verify(orderRepository).delete(orderId);
//        assertTrue(result);
//    }
//
//    @Test
//    void testDeleteOrder_idDoesNotExist() {
//        when(orderRepository.delete(orderId)).thenReturn(false);
//
//        boolean result = orderService.deleteOrder(orderId);
//
//        verify(orderRepository).delete(orderId);
//        assertFalse(result);
//    }
//
//}