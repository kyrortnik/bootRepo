package com.epam.esm;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> getUserById(Long id);

    List<User> getUsers(HashMap<String, Boolean> sortingParams, int max, int offset);
}
