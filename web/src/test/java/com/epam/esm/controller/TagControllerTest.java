package com.epam.esm.controller;


import com.epam.esm.Tag;
import com.epam.esm.config.PersistenceConfig;
import com.epam.esm.config.WebConfig;
import com.epam.esm.impl.TagService;
import com.epam.esm.security.ApplicationSecurityConfiguration;
import com.epam.esm.security.JwtRequestHelper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {Application.class, PersistenceConfig.class, WebConfig.class, ApplicationSecurityConfiguration.class})
class TagControllerTest {

    private final String tagName = "tagName";

    private final Tag tag = new Tag(tagName);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtRequestHelper jwtRequestHelper;

    @MockBean
    private TagService tagService;

//    @Test
//    public void create() {
//        when(tagService.create(tag)).thenReturn(tag);
////        restTemplate.postForEntity("/tags", new Tag("tagName"), Void.class);
//        restTemplate.exchange("/tags", HttpMethod.POST,
//                new HttpEntity(new Tag("tagName"), JwtRequestHelper.loggedInAs("admin",
//                "ROLE_ADMIN")));
//        verify(this.tagService).create(tag);
//    }

}