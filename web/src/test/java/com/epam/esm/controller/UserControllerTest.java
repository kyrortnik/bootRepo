//package com.epam.esm.controller;
//
//import com.epam.esm.Application;
//import com.epam.esm.User;
//import com.epam.esm.config.PersistenceConfig;
//import com.epam.esm.config.WebConfig;
//import com.epam.esm.dto.LoginDto;
//import com.epam.esm.impl.UserService;
//import com.epam.esm.security.ApplicationSecurityConfiguration;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//
//import static org.mockito.Mockito.verify;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import static org.mockito.BDDMockito.given;
//
//
//
//
////@RunWith(SpringRunner.class)
////@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest(webEnvironment =
//        SpringBootTest.WebEnvironment.RANDOM_PORT,
//        classes = {Application.class, PersistenceConfig.class, WebConfig.class, ApplicationSecurityConfiguration.class})
//@WebMvcTest
//class UserControllerTest {
//
//    private final LoginDto signupDto = new LoginDto("larry", "1234", "larry", "miller");
//
//    private final User user = new User.UserBuilder(signupDto.getUsername(), signupDto.getPassword())
//            .firstName(signupDto.getFirstName())
//            .secondName(signupDto.getLastName())
//            .build();
//
////    @Autowired
////    private TestRestTemplate restTemplate;
////
////    @Autowired
////    private JwtRequestHelper jwtRequestHelper;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//
//    @MockBean
//    private UserService userService;
//
//
////    @Test
////    public void test_signin() {
////        when(userService.signin("admin","admin")).thenReturn("jwtToken");
//////        ResponseEntity<String> response = restTemplate.
//////                postForEntity("/users/signin", new LoginDto("admin", "admin"), String.class);
////
////        ResponseEntity<String> response = this.restTemplate.withBasicAuth(
////                "admin", "admin").getForEntity("/users",
////                String.class);
////
////        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
////        verify(userService).signin("admin","admin");
////
////
////    }
//
//
//    @Test
//    public void getAllUsers() throws Exception {
//        given(userService.getUsers(Arrays.asList("id.desc"),20,5))
//                .willReturn(new PageImpl<>(Arrays.asList(new User())));
//
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isOk());
//    }
//}