package com.epam.esm;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Long> {


    @Override
    @NonNull  Optional<Order> findById(@NonNull Long orderId);


    @Override
    @NonNull  Page<Order> findAll(@NonNull  Pageable pageable);


    @Override
    @NonNull <S extends Order> S save(@NonNull  S order);


    @Override
    void deleteById(@NonNull Long orderId);


    @Override
    <S extends Order> boolean exists(@NonNull Example<S> orderExample);
}
