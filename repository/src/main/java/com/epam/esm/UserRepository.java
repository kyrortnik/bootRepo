package com.epam.esm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository  extends PagingAndSortingRepository<User, Long> {


//    @Override
//    @NonNull Optional<User> findById(@NonNull Long userId);
//
//    @Override
//    @NonNull Page<User> findAll(@NonNull Pageable pageable);

    Optional<User> findByUsername(String username);
}
