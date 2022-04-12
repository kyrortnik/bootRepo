package com.epam.esm.impl;

import com.epam.esm.*;
import com.epam.esm.dto.LoginDto;
import com.epam.esm.mapper.RequestParamsMapper;
import com.epam.esm.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final RequestParamsMapper requestParamsMapper;

    private final OrderService orderService;

    @Autowired
    public UserService(UserRepository userRepository,
                       AuthenticationManager authenticationManager, RoleRepository roleRepository,
                       JwtProvider jwtProvider, PasswordEncoder passwordEncoder,
                       RequestParamsMapper requestParamsMapper, OrderService orderService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.requestParamsMapper = requestParamsMapper;
        this.orderService = orderService;
    }


    public User getById(Long userId) {
        LOGGER.debug("Entering UserService.getById");
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("User with id [%s] does not exist", userId)));

        LOGGER.debug("Exiting UserService.getById");
        return foundUser;

    }

    public Page<User> getUsers(List<String> sortBy, int max, int offset) {
        LOGGER.debug("Entering UserService.getUsers()");

        Sort sortingParams = requestParamsMapper.mapParams(sortBy);

        Page<User> foundUsers = userRepository.findAll(PageRequest.of(offset, max, sortingParams));

        LOGGER.debug("Exiting UserService.getUsers()");
        return foundUsers;
    }


    /**
     * Sign in a user into the application, with JWT-enabled authentication
     *
     * @param username username
     * @param password password
     * @return String of the Java Web Token, HttpClientErrorException thrown otherwise
     */
    public String signin(String username, String password) throws HttpClientErrorException {
        LOGGER.debug("New user attempting to sign in");
        Optional<String> token = Optional.empty();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                token = Optional.of(jwtProvider.createToken(username, user.get().getRoles()));
            } catch (AuthenticationException e) {
                LOGGER.debug("Log in failed for user {}", username);
            }
        }
        return token.orElseThrow(() -> new HttpClientErrorException(HttpStatus.FORBIDDEN, "Login Failed"));
    }

    /**
     * Sign ups a new user
     *
     * @param loginDto Dto class representing new User parameters
     * @return created User,  HttpClientErrorException thrown otherwise
     */
    public User signup(LoginDto loginDto) {
        Optional<User> sighedUpUser = Optional.empty();
        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        String firstName = loginDto.getFirstName();
        String lastname = loginDto.getLastName();

        if (!userRepository.findByUsername(username).isPresent()) {
            Optional<Role> role = roleRepository.findByName("ROLE_GUEST");
            User newUser = new User.UserBuilder(username, passwordEncoder.encode(password))
                    .firstName(firstName)
                    .secondName(lastname)
                    .role(role.orElseThrow(() -> new HttpServerErrorException(HttpStatus.NOT_FOUND, "User without a role")))
                    .build();
            sighedUpUser = Optional.of(userRepository.save(newUser));
        }
        return sighedUpUser.orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "User already exists"));

    }

    public Set<Order> getUserOrders(Long userId) {
        LOGGER.debug("Entering UserService.getUserOrders");

        Set<Order> userOrders = orderService.getOrdersForUser(userId);

        if (userOrders.isEmpty()) {
            LOGGER.error("NoSuchElementException in UserService.getUserOrders\n" +
                    "No orders for this user");
            throw new NoSuchElementException("No orders for this user");
        }

        LOGGER.debug("Exiting UserService.getUserOrders");
        return userOrders;
    }

}
