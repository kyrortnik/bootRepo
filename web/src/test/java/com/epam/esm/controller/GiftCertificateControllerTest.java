package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.Tag;
import com.epam.esm.impl.GiftCertificateService;
import com.epam.esm.impl.TagService;
import com.epam.esm.security.ApplicationUserDetailsService;
import com.google.gson.Gson;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(properties = "spring.profiles.active:dev", controllers = GiftCertificateController.class)
class GiftCertificateControllerTest {


    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private GiftCertificateService giftCertificateService;

    @MockBean
    private ApplicationUserDetailsService applicationUserDetailsService;

    private final String roleAdmin = "ADMIN";
    private final String roleUser = "USER";
    private final String roleGuest = "GUEST";

    private final String URL = "/api/v1/certificates/";
    private final long giftCertificateId = 11L;
    private final String giftCertificateName = "giftCertificateName";
//    private final String giftCertificateDescription = "giftCertificateDescription";
//    private final long giftCertificateDuration = 360;
//    private final long giftCertificatePrice = 300;
//    private final LocalDateTime createDate = LocalDateTime.of(2022,4,25,15,0);
//    private final LocalDateTime lastUpdateDate = LocalDateTime.of(2022,4,25,16,16);
    private final Set<Tag> tags = new HashSet<>(Arrays.asList(new Tag(1L,"tagName")));
    private final Set<Order> orders = new HashSet<>(Arrays.asList(new Order()));

    private final String jsonPathId = "id";
    private final String jsonPathName = "name";
    private final String jsonPathTotalElements = "totalElements";


    private final List<String> sortBy = Collections.singletonList("id.asc");
    private final int max = 20;
    private final int page = 0;

    private final GiftCertificate firstGiftCertificate = new GiftCertificate
            .GiftCertificateBuilder("firstGiftCertificateName")
            .id(1L)
            .build();

    private final GiftCertificate secondGiftCertificate = new GiftCertificate
            .GiftCertificateBuilder("secondGiftCertificateName")
            .id(2L)
            .build();


    private final Page<GiftCertificate> giftCertificatePage = new PageImpl<>(Arrays
            .asList(firstGiftCertificate, secondGiftCertificate));

    private final GiftCertificate inputGiftCertificate = new GiftCertificate
            .GiftCertificateBuilder(giftCertificateName)
            .build();

    private final Set<String> tagNames = new HashSet<>(Arrays.asList("tagName", "secondTagName"));


    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser, roleGuest})
    public void test_getGiftCertificateById_idExists_is200() throws Exception {
        GiftCertificate returnGiftCertificate = new GiftCertificate
                .GiftCertificateBuilder(giftCertificateName)
                .id(giftCertificateId)
                .build();

        given(giftCertificateService.findGiftCertificateById(giftCertificateId)).willReturn(
                returnGiftCertificate);

        mockMvc.perform(get(URL + giftCertificateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathId).value(giftCertificateId))
                .andExpect(jsonPath(jsonPathName).value(giftCertificateName));
    }

    @Test
    public void test_getGiftCertificateById_isExist_is401() throws Exception {
        mockMvc.perform(get(URL + giftCertificateId))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getGiftCertificateById_idDoesNotExist_is404() throws Exception {
        given(giftCertificateService.findGiftCertificateById(giftCertificateId))
                .willThrow(NoSuchElementException.class);

        mockMvc.perform(get(URL + giftCertificateId))
                .andExpect(status().isNotFound());
    }

    //TODO investigate
    @Test
    @WithMockUser(roles = {roleAdmin, roleUser, roleGuest})
    public void test_getGiftCertificates_is200() throws Exception {
//        when(giftCertificateService.findGiftCertificates(tagNames,sortBy,max,page)).thenReturn(giftCertificatePage);
        given(giftCertificateService.findGiftCertificates(tagNames, sortBy, max, page))
                .willReturn(giftCertificatePage);

        mockMvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathTotalElements).value(giftCertificatePage.getTotalElements()));

    }

    @Test
    public void test_getGiftCertificates_is401() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getGiftCertificates_noneExist_is404() throws Exception {
        given(giftCertificateService.findGiftCertificates(tagNames, sortBy, max, page))
                .willThrow(NoSuchElementException.class);

        mockMvc.perform(get(URL))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = roleAdmin)
    public void test_createGiftCertificate_is201() throws Exception {
        GiftCertificate createdGiftCertificate = new GiftCertificate
                .GiftCertificateBuilder(giftCertificateName)
                .id(giftCertificateId)
                .build();

        given(giftCertificateService.create(inputGiftCertificate)).willReturn(createdGiftCertificate);
        Gson gson = new Gson();
        String json = gson.toJson(inputGiftCertificate);


        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(jsonPathId).value(giftCertificateId))
                .andExpect(jsonPath(jsonPathName).value(giftCertificateName));
    }
//
//    @Test
//    public void test_createTag_is401() throws Exception {
//
//        mockMvc.perform(post(URL)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @WithMockUser(roles = roleGuest)
//    public void test_createTag_is403() throws Exception {
//        Gson gson = new Gson();
//        String json = gson.toJson(inputTag);
//
//        mockMvc.perform(post(URL)
//                .contentType(APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().isForbidden());
//
//    }
//
//    @Test
//    @WithMockUser(roles = {roleAdmin, roleUser})
//    public void test_createTag_is400() throws Exception {
//        given(tagService.create(inputTag)).willThrow(ConstraintViolationException.class);
//
//        Gson gson = new Gson();
//        String json = gson.toJson(inputTag);
//
//        mockMvc.perform(post(URL)
//                .contentType(APPLICATION_JSON)
//                .content(json))
//                .andExpect(status().isBadRequest());
//    }


}