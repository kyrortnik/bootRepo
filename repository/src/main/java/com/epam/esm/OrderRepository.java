package com.epam.esm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Long> {


    Set<Order> findByUserId(Long userId);
}
