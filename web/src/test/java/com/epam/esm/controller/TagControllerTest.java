package com.epam.esm.controller;

import com.epam.esm.Tag;
import com.epam.esm.impl.TagService;
import com.epam.esm.security.ApplicationUserDetailsService;
import com.google.gson.Gson;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.NoResultException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(properties = "spring.profiles.active:dev", controllers = TagController.class)
class TagControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private TagService tagService;

    @MockBean
    private ApplicationUserDetailsService applicationUserDetailsService;

    private final String URL = "/api/v1/tags/";
    private final String MOST_USED_TAG = "/mostUsedTagForRichestUser";
    private final long tagId = 11;
    private final String tagName = "tagName";

    private final String jsonPathId = "id";
    private final String jsonPathName = "name";
    private final String jsonPathTotalElements = "totalElements";

    private final String roleAdmin = "ADMIN";
    private final String roleUser = "USER";
    private final String roleGuest = "GUEST";

    private final List<String> sortBy = Arrays.asList("id.asc");
    private final int max = 20;
    private final int page = 0;

    private final Tag firstTag = new Tag(1L, "firstTag");
    private final Tag secondTag = new Tag(2L, "secondTag");
    private final Page<Tag> tagPage = new PageImpl<>(Arrays.asList(firstTag, secondTag));
    private final Tag inputTag = new Tag(tagName);

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getTagById_idExists_is200() throws Exception {
        given(tagService.findById(tagId)).willReturn(
                new Tag(tagId, tagName));

        mockMvc.perform(get(URL + tagId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathId).value(tagId))
                .andExpect(jsonPath(jsonPathName).value(tagName));
    }

    @Test
    public void test_getTagById_isExist_is401() throws Exception {
        mockMvc.perform(get(URL + tagId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = roleGuest)
    public void test_getTagById_idExists_is403() throws Exception {

        mockMvc.perform(get(URL + tagId))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getTagById_idDoesNotExist_is404() throws Exception {
        given(tagService.findById(tagId)).willThrow(NoSuchElementException.class);

        mockMvc.perform(get(URL + tagId))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getTags_is200() throws Exception {
        given(tagService.findTags(sortBy, max, page)).willReturn(
                tagPage);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathTotalElements).value(tagPage.getTotalElements()));

    }

    @Test
    public void test_getTags_is401() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = roleGuest)
    public void test_getTags_is403() throws Exception {

        mockMvc.perform(get(URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getTags_noTags_is404() throws Exception {
        given(tagService.findTags(sortBy, max, page)).willThrow(NoSuchElementException.class);

        mockMvc.perform(get(URL))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_createTag_is201() throws Exception {
        Tag createdTag = new Tag(tagId, tagName);
        Gson gson = new Gson();
        String json = gson.toJson(inputTag);

        given(tagService.create(inputTag)).willReturn(createdTag);

        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(jsonPathId).value(tagId))
                .andExpect(jsonPath(jsonPathName).value(tagName));
    }

    @Test
    public void test_createTag_is401() throws Exception {

        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = roleGuest)
    public void test_createTag_is403() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(inputTag);

        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_createTag_is400() throws Exception {
        given(tagService.create(inputTag)).willThrow(ConstraintViolationException.class);

        Gson gson = new Gson();
        String json = gson.toJson(inputTag);

        mockMvc.perform(post(URL)
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_deleteTag_is200() throws Exception {

        given(tagService.deleteTag(tagId)).willReturn(true);

        mockMvc.perform(delete(URL + tagId))
                .andExpect(status().isOk());
    }

    @Test
    public void test_deleteTag_is401() throws Exception {

        mockMvc.perform(delete(URL + tagId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {roleUser, roleGuest})
    public void test_deleteTag_is403() throws Exception {

        mockMvc.perform(delete(URL + tagId))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = roleAdmin)
    public void test_deleteTag_tagDoesNotExist() throws Exception {

        given(tagService.deleteTag(tagId)).willReturn(false);

        mockMvc.perform(delete(URL + tagId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("No tag with tagId [%s] was found", tagId)));

    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getMostUsedTagForRichestUser_is200() throws Exception {
        Tag mostUsedTag = new Tag(tagId,tagName);
        given(tagService.getMostUsedTagForRichestUser()).willReturn(mostUsedTag);

        mockMvc.perform(get(URL + MOST_USED_TAG))
                .andExpect(status().isOk())
                .andExpect(jsonPath(jsonPathId).value(mostUsedTag.getId()))
                .andExpect(jsonPath(jsonPathName).value(mostUsedTag.getName()));
    }

    @Test
    public void test_getMostUsedTagForRichestUser_is401() throws Exception {

        mockMvc.perform(get(URL + MOST_USED_TAG))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(roles = roleGuest)
    public void test_getMostUsedTagForRichestUser_is403() throws Exception {

        mockMvc.perform(get(URL + MOST_USED_TAG))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = {roleAdmin, roleUser})
    public void test_getMostUsedTagForRichestUser_is404() throws Exception {
        given(tagService.getMostUsedTagForRichestUser()).willThrow(NoResultException.class);

        mockMvc.perform(get(URL + MOST_USED_TAG))
                .andDo(print())
//                .andExpect(status().isNotFound())
                .andExpect(content().string("No orders exist yet."));

    }


}