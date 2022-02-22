package com.epam.esm;

import java.util.Optional;
import java.util.Set;

public interface OrderRepository {

    Optional<Order> getOrder(Long id);

    Set<Order> getOrders(String order, int max);

    Long createOrder(Order order);

    boolean delete(Long id);
}
