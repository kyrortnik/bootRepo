package com.epam.esm;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    Optional<User> getUserById(Long id);

    List<User> getUsers(String order, int max, int offset);
}
