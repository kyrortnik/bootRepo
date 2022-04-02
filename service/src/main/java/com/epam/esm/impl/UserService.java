package com.epam.esm.impl;

import com.epam.esm.*;
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

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private final UserRepository userRepository;

    private final AuthGroupRepository authGroupRepository;

    @Autowired
    public UserService(UserRepository userRepository, AuthGroupRepository authGroupRepository) {
        this.userRepository = userRepository;
        this.authGroupRepository = authGroupRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findByUsername(username)
               .orElseThrow( () -> new UsernameNotFoundException("cannot find user [" + username + "]"));
       List<AuthGroup> authGroups = authGroupRepository.findByUsername(username);
       return new UserPrincipal(user, authGroups);
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
