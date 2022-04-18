package com.epam.esm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.BDDAssertions.then;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test_getUserByUsername() {
        //given

        User savedUser = userRepository.save(new User.UserBuilder("admin", "admin").build());

        //when
       Optional<User> foundUser = userRepository.findByUsername("admin");
        //then

        assertTrue(foundUser.isPresent());
        then(foundUser.get().getId()).isNotNull();
        then(foundUser.get().getUsername()).isEqualTo(savedUser.getUsername());

    }

}