package com.epam.esm.impl;

import com.epam.esm.*;
import com.epam.esm.dto.LoginDto;
import com.epam.esm.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

//    private final AuthGroupRepository authGroupRepository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    @Autowired
    public UserService(UserRepository userRepository
            /*AuthGroupRepository authGroupRepository*/,
                       AuthenticationManager authenticationManager, RoleRepository roleRepository,
                       JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
//        this.authGroupRepository = authGroupRepository;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
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


    /**
     * Sign in a user into the application, with JWT-enabled authentication
     *
     * @param username username
     * @param password password
     * @return Optional of the Java Web Token, empty otherwise
     */
    public Optional<String> signin(String username, String password) {
        LOGGER.info("New user attempting to sign in");
        Optional<String> token = Optional.empty();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                token = Optional.of(jwtProvider.createToken(username, user.get().getRoles()));
            } catch (AuthenticationException e) {
                LOGGER.info("Log in failed for user {}", username);
            }
        }
        return token;
    }

    public Optional<User> signup(LoginDto loginDto) {
        Optional<User> sighedUpUser = Optional.empty();
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        String firstName = loginDto.getFirstName();
        String lastname = loginDto.getLastName();

        if (!userRepository.findByUsername(username).isPresent()) {
            Optional<Role> role = roleRepository.findByName("ROLE_GUEST");
            User newUser = new User.UserBuilder(username, password)
                    .firstName(firstName)
                    .secondName(lastname)
                    .role(role.orElseThrow(() -> new RuntimeException("User without a role")))
                    .build();
            sighedUpUser = Optional.of(userRepository.save(newUser));
        }
        return sighedUpUser;

    }


}
