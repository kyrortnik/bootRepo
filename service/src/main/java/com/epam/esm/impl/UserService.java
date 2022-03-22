package com.epam.esm.impl;

import com.epam.esm.User;
import com.epam.esm.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<User> getById(Long userId) {
        try {
            return userRepository.getUserById(userId);
        } catch (NoResultException e) {
            throw new NoSuchElementException("User with id [" + userId + "] does not exist");
        }

    }

    //    public List<User> getUsers(String order, int max, int offset) {
//        return userRepository.getUsers(order, max, offset);
//    }
    public List<User> getUsers(HashMap<String, Boolean> sortingParams, int max, int offset) {
        return userRepository.getUsers(sortingParams, max, offset);
    }
}
