package com.epam.esm.impl;

import com.epam.esm.User;
import com.epam.esm.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Optional<User> getById(Long userId) {

        return userRepository.getUserById(userId);

    }

    public List<User> getUsers(HashMap<String, Boolean> sortingParams, int max, int offset) {
        return userRepository.getUsers(sortingParams, max, offset);
    }
}
