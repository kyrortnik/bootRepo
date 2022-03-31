package com.epam.esm.impl;

import com.epam.esm.User;
import com.epam.esm.UserPrincipal;
import com.epam.esm.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//TODO - refactor after moving repos to Spring Data
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = getById(1L).orElseThrow( () -> new UsernameNotFoundException("cannot find userName(for now Id): 1"));
       return new UserPrincipal(user);
    }

    public Optional<User> getById(Long userId) {
        LOGGER.debug("Entering UserService.getById()");
        Optional<User> foundUser = userRepository.findById(userId);

        LOGGER.debug("Exiting UserService.getById()");
        return foundUser;

    }

    public Page<User> getUsers(Sort sortingParams, int max, int offset) {
        LOGGER.debug("Entering UserService.getUsers()");

        Page<User> foundUsers = userRepository.findAll(PageRequest.of(offset, max, sortingParams));

        LOGGER.debug("Exiting UserService.getUsers()");
        return foundUsers;
    }
}
