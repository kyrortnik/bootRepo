package com.epam.esm;

import com.epam.esm.controller.UserController;
import com.epam.esm.impl.UserService;
import com.epam.esm.security.ApplicationUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ApplicationUserDetailsService applicationUserDetailsService;

    @Test
    public void test_getUser() throws Exception {

        User persistenceUser = new User.UserBuilder("username", "password")
                .id(1L)
                .build();
        given(userService.findUserById(1L)).willReturn(persistenceUser);

        mockMvc.perform(get("api/v1/users/1"))
                .andExpect(status().isOk());


    }
}