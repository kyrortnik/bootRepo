package com.epam.esm;

import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    Optional<User> getUserById(Long id);

    Set<User> getUsers(String order, int max);
}
