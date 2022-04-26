package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.User;
import com.epam.esm.impl.OrderService;
import com.epam.esm.security.ApplicationUserDetailsService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(properties = "spring.profiles.active:dev", controllers = OrderController.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private OrderService orderService;

    @MockBean
    private ApplicationUserDetailsService applicationUserDetailsService;

    private final String URL = "/api/v1/orders/";
    private final String GIFT_CERTIFICATE = "/giftCertificate";
    private final long orderId = 11L;
    private final User user  = new User
            .UserBuilder("username","password")
            .build();
    private final GiftCertificate giftCertificate = new GiftCertificate
            .GiftCertificateBuilder("giftCertificateName")
            .id(1L)
            .build();

    private final String jsonPathId = "id";
    private final String jsonPathName = "name";
    private final String jsonPathTotalElements = "totalElements";

    private final String roleAdmin = "ADMIN";
    private final String roleUser = "USER";
    private final String roleGuest = "GUEST";

    private final List<String> sortBy = Collections.singletonList("id.asc");
    private final int max = 20;
    private final int page = 0;

    private final Order firstOrder = new Order
            .OrderBuilder()
            .id(1)
            .giftCertificate(new GiftCertificate.GiftCertificateBuilder("firstGiftCertificate").build())
            .user(new User.UserBuilder("username","password").build())
            .build();

    private final Order secondOrder = new Order
            .OrderBuilder()
            .id(2)
            .giftCertificate(new GiftCertificate.GiftCertificateBuilder("secondGiftCertificate").build())
            .user(new User.UserBuilder("username1","password1").build())
            .build();


    private final Page<Order> orderPage = new PageImpl<>(Arrays.asList(firstOrder,secondOrder));
    private final Order inputOrder = new Order
            .OrderBuilder()
            .user(new User.UserBuilder("userName","password").build())
            .giftCertificate(new GiftCertificate.GiftCertificateBuilder("giftCertificate").build())
            .build();

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getOrderById_idExists_is200() throws Exception {
        Order order = new Order
                .OrderBuilder()
                .id(orderId)
                .giftCertificate(giftCertificate)
                .user(user)
                .build();

        given(orderService.findOrderById(orderId)).willReturn(
                order);

        mockMvc.perform(get(URL + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathId).value(orderId));
    }

    @Test
    public void test_getOrderById_isExist_is401() throws Exception {
        mockMvc.perform(get(URL + orderId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = roleGuest)
    public void test_getOrderById_idExists_is403() throws Exception {

        mockMvc.perform(get(URL + orderId))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getOrderById_idDoesNotExist_is404() throws Exception {
        given(orderService.findOrderById(orderId)).willThrow(NoSuchElementException.class);

        mockMvc.perform(get(URL + orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getOrders_is200() throws Exception {
        given(orderService.findOrders(sortBy, max, page)).willReturn(
                orderPage);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathTotalElements).value(orderPage.getTotalElements()));

    }

    @Test
    public void test_getOrders_is401() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = roleGuest)
    public void test_getOrders_is403() throws Exception {

        mockMvc.perform(get(URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getOrders_noOrders_is404() throws Exception {
        given(orderService.findOrders(sortBy, max, page)).willThrow(NoSuchElementException.class);

        mockMvc.perform(get(URL))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_createOrder_is201() throws Exception {
        Order createdOrder = new Order
                .OrderBuilder()
                .id(orderId)
                .user(user)
                .giftCertificate(giftCertificate)
                .build();
        Gson gson = new Gson();
        String json = gson.toJson(inputOrder);

        given(orderService.create(inputOrder)).willReturn(createdOrder);

        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(jsonPathId).value(orderId));
    }

    @Test
    public void test_createOrder_is401() throws Exception {

        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = roleGuest)
    public void test_createOrder_is403() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(inputOrder);

        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {roleAdmin})
    public void test_createOrder_is400() throws Exception {
        given(orderService.create(inputOrder)).willThrow(DuplicateKeyException.class);

        Gson gson = new Gson();
        String json = gson.toJson(inputOrder);

        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = roleAdmin)
    public void test_deleteOrder_is200() throws Exception {

        given(orderService.deleteOrder(orderId)).willReturn(true);

        mockMvc.perform(delete(URL + orderId))
                .andExpect(status().isOk());
    }

    @Test
    public void test_deleteOrder_is401() throws Exception {

        mockMvc.perform(delete(URL + orderId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {roleUser, roleGuest})
    public void test_deleteOrder_is403() throws Exception {

        mockMvc.perform(delete(URL + orderId))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = roleAdmin)
    public void test_deleteOrder_orderDoesNotExist() throws Exception {
        given(orderService.deleteOrder(orderId)).willReturn(false);

        mockMvc.perform(delete(URL + orderId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("No order with id [%s] was found", orderId)));

    }

    @Test
    @WithMockUser(roles = roleAdmin)
    public void test_getOrderGiftCertificate_is200() throws Exception {
        Order returnOrder = new Order
                .OrderBuilder()
                .id(orderId)
                .user(user)
                .giftCertificate(giftCertificate)
                .build();

        given(orderService.findOrderById(orderId)).willReturn(returnOrder);

        mockMvc.perform(get(URL + orderId + GIFT_CERTIFICATE))
                .andExpect(status().isOk())
        .andExpect(jsonPath(jsonPathId).value(giftCertificate.getId()))
        .andExpect(jsonPath(jsonPathName).value(giftCertificate.getName()));
    }

    @Test
    public void test_getOrderGiftCertificate_is401() throws Exception {

        mockMvc.perform(get(URL + orderId + GIFT_CERTIFICATE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = roleGuest)
    public void test_getOrderGiftCertificate_is403() throws Exception {

        mockMvc.perform(get(URL + orderId + GIFT_CERTIFICATE))
                .andExpect(status().isForbidden());

    }

//    @Test
//    @WithMockUser(roles = roleAdmin)
//    public void test_getOrderGiftCertificate_orderDoesNotExist() throws Exception {
//
//        given(orderService.findOrderById(orderId)).willThrow(new NoSuchElementException(String
//                .format("No order with id %s exists", orderId)));
//
//
//        mockMvc.perform(get(URL + orderId + GIFT_CERTIFICATE))
//                .andExpect(status().isOk())
//                .andExpect(content().string(String.format("No order with id %s exists", orderId)));
//
//    }

}