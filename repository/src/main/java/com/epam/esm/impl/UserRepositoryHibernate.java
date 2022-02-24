package com.epam.esm.impl;

import com.epam.esm.User;
import com.epam.esm.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public class UserRepositoryHibernate implements UserRepository {
    @Override
    public Optional<User> getUser(Long id) {
        return Optional.empty();
    }

    @Override
    public Set<User> getUsers(String order, int max) {
        return null;
    }
}
