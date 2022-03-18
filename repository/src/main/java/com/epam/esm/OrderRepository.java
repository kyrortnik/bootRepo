package com.epam.esm;

import java.util.Optional;
import java.util.Set;

public interface OrderRepository {

    Optional<Order> getOrderById(Long id);

    Set<Order> getOrders(String order, int max, int offset);

    Long createOrder(Order order);

    boolean delete(Long id);

    boolean orderAlreadyExists(Order order);
}
