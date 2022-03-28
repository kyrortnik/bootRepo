package com.epam.esm.impl;

import com.epam.esm.User;
import com.epam.esm.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Optional<User> getById(Long userId) {
        LOGGER.info("Entering UserService.getById()");
        Optional<User> foundUser = userRepository.getUserById(userId);

        LOGGER.info("Exiting UserService.getById()");
        return foundUser;

    }

    public List<User> getUsers(HashMap<String, Boolean> sortingParams, int max, int offset) {
        LOGGER.info("Entering UserService.getUsers()");

        List<User> foundUsers = userRepository.getUsers(sortingParams, max, offset);

        LOGGER.info("Exiting UserService.getUsers()");
        return foundUsers;
    }
}
