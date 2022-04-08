//TODO --refactor tests
package com.epam.esm.impl;

import com.epam.esm.AuthGroupRepository;
import com.epam.esm.User;
import com.epam.esm.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceTest {

    //mock
    private final UserRepository userRepository = Mockito.mock(UserRepository.class, withSettings().verboseLogging());
    private final AuthGroupRepository authGroupRepository = Mockito.mock(AuthGroupRepository.class,withSettings().verboseLogging());

    // class under test
//    private final UserService userService = new UserService(userRepository,authGroupRepository);

    private final long userId = 1;

    private User firstUser;
    private User secondUser;
    private User thirdUser;

    private final List<User> noUsers = new ArrayList<>();

    private List<User> users;

    private HashMap<String, Boolean> sortParams;
    private final int max = 20;
    private final int offset = 0;

//    @BeforeEach
//    void setUp() {
//
//        firstUser = new User(1, "John", "Smith");
//        secondUser = new User(2, "Kanye", "West");
//        thirdUser = new User(3, "Pete", "Davidson");
//
//        users = new ArrayList<>(Arrays.asList(
//                firstUser,
//                secondUser,
//                thirdUser
//        ));
//        sortParams = new HashMap<>();
//        sortParams.put("id", true);
//    }
//
//    @AfterEach
//    void tearDown() {
//        firstUser = new User();
//        secondUser = new User();
//        thirdUser = new User();
//        users.clear();
//        sortParams.clear();
//    }
//
//
//    @Test
//    void testGetUserById_idExists() {
//        when(userRepository.getUserById(userId)).thenReturn(Optional.of(firstUser));
//
//        Optional<User> foundUser = userService.getById(userId);
//
//        assertTrue(foundUser.isPresent());
//        assertEquals(firstUser, foundUser.get());
//    }
//
//    @Test
//    void testGetUserById_idDoesNotExist() {
//
//        when(userRepository.getUserById(userId)).thenReturn(Optional.empty());
//
//        Optional<User> returnUser = userService.getById(userId);
//
//        verify(userRepository).getUserById(userId);
//        assertFalse(returnUser.isPresent());
//        assertEquals(Optional.empty(), returnUser);
//
//    }
//
//    @Test
//    void testGetUsers_usersExist() {
//
//        when(userRepository.getUsers(sortParams, max, offset)).thenReturn(users);
//
//        List<User> returnUsers = userService.getUsers(sortParams, max, offset);
//
//        verify(userRepository).getUsers(sortParams, max, offset);
//        assertEquals(users, returnUsers);
//    }
//
//    @Test
//    void testGetUsers_noUsersExist() {
//        when(userRepository.getUsers(sortParams, max, offset)).thenReturn(noUsers);
//
//        List<User> returnUsers = userService.getUsers(sortParams, max, offset);
//
//        verify(userRepository).getUsers(sortParams, max, offset);
//        assertEquals(noUsers, returnUsers);
//    }
}