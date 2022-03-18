package com.epam.esm.impl;

import com.epam.esm.User;
import com.epam.esm.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceTest {

    //mock
    private final UserRepository userRepository = Mockito.mock(UserRepository.class, withSettings().verboseLogging());

    // class under test
    private final UserService userService = new UserService(userRepository);

    private final String order = "ASC";
    private final int max = 20;
    private final int offset = 0;

    private final long userId = 1;

    private final User firstUser = new User(1, "John", "Smith");
    private final User secondUser = new User(2, "Kanye", "West");
    private final User thirdUser = new User(3, "Pete", "Davidson");

    private final List<User> noUsers = new ArrayList<>();

    private final List<User> users = new ArrayList<>(Arrays.asList(
            firstUser,
            secondUser,
            thirdUser
    ));


    @Test
    void testGetUserById_idExists() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(firstUser));

        Optional<User> foundUser = userService.getById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(firstUser, foundUser.get());
    }

    @Test
    void testGetUserById_idDoesNotExist() {

        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());

        Optional<User> returnUser = userService.getById(userId);

        verify(userRepository).getUserById(userId);
        assertFalse(returnUser.isPresent());
        assertEquals(Optional.empty(), returnUser);

    }

    @Test
    void testGetUsers_usersExist() {

        when(userRepository.getUsers(order, max, offset)).thenReturn(users);

        List<User> returnUsers = userService.getUsers(order, max, offset);

        verify(userRepository).getUsers(order, max, offset);
        assertEquals(users, returnUsers);
    }

    @Test
    void testGetUsers_noUsersExist() {
        when(userRepository.getUsers(order, max, offset)).thenReturn(noUsers);

        List<User> returnUsers = userService.getUsers(order, max, offset);

        verify(userRepository).getUsers(order, max, offset);
        assertEquals(noUsers, returnUsers);
    }
}