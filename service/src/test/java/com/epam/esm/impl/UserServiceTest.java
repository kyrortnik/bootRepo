package com.epam.esm.impl;

import com.epam.esm.RoleRepository;
import com.epam.esm.User;
import com.epam.esm.UserRepository;
import com.epam.esm.mapper.RequestParamsMapper;
import com.epam.esm.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class UserServiceTest {

    //mock
    private final UserRepository userRepository = Mockito
            .mock(UserRepository.class, withSettings().verboseLogging());

    private final RoleRepository roleRepository = Mockito
            .mock(RoleRepository.class, withSettings().verboseLogging());

    private final AuthenticationManager authenticationManager = Mockito
            .mock(AuthenticationManager.class, withSettings().verboseLogging());

    private final JwtProvider jwtProvider = Mockito
            .mock(JwtProvider.class, withSettings().verboseLogging());

    private final PasswordEncoder passwordEncoder = Mockito
            .mock(PasswordEncoder.class, withSettings().verboseLogging());

    private final RequestParamsMapper requestParamsMapper = Mockito
            .mock(RequestParamsMapper.class, withSettings().verboseLogging());

    private final OrderService orderService = Mockito
            .mock(OrderService.class, withSettings().verboseLogging());


    // class under test
    private final UserService userService = new UserService(
            userRepository,
            authenticationManager,
            roleRepository,
            jwtProvider,
            passwordEncoder,
            requestParamsMapper,
            orderService);

    private final long userId = 1;
    
    private final User firstUser = new User.UserBuilder("username", "password")
            .firstName("John")
            .secondName("Smith")
            .build();

    private final User secondUser = new User.UserBuilder("admin", "admin")
            .firstName("Kanye")
            .secondName("West")
            .build();

    private final User thirdUser = new User.UserBuilder("user", "user")
            .firstName("Kid")
            .secondName("Cudi")
            .build();


    private final List<User> users = Arrays.asList(
            firstUser,
            secondUser,
            thirdUser
    );


    private final int max = 20;
    private final int page = 0;
    private final Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
    private final Sort sort = Sort.by(order);

    private final List<String> sortParams = new ArrayList<>();

    Page<User> usersPage = new PageImpl<>(users);
    Page<User> noUsersPage = new PageImpl<>(new ArrayList<>());

    @Test
    void testGetUserById_idExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(firstUser));

        User foundUser = userService.findUserById(userId);

        verify(userRepository).findById(userId);
        assertEquals(firstUser, foundUser);
    }

    @Test
    void testGetUserById_idDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception noSuchElementException = assertThrows(NoSuchElementException.class,
                () -> userService.findUserById(userId));
        String expectedMessage = String.format("User with id [%s] does not exist", userId);
        String actualMessage = noSuchElementException.getMessage();

        verify(userRepository).findById(userId);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetUsers_usersExist() {
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(userRepository.findAll(PageRequest.of(page, max, sort))).thenReturn(usersPage);

        Page<User> returnUsers = userService.findUsers(sortParams, max, page);

        verify(userRepository).findAll(PageRequest.of(page, max, sort));
        assertEquals(usersPage, returnUsers);
    }

    @Test
    void testGetUsers_noUsersExist() {
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(userRepository.findAll(PageRequest.of(page, max, sort))).thenReturn(noUsersPage);

        Exception noSuchElementException = assertThrows(NoSuchElementException.class,
                () -> userService.findUsers(sortParams, max, page));
        String expectedMessage = "No Users exist";
        String actualMessage = noSuchElementException.getMessage();

        verify(requestParamsMapper).mapParams(sortParams);
        verify(userRepository).findAll(PageRequest.of(page, max, sort));
        assertEquals(expectedMessage, actualMessage);
    }
}