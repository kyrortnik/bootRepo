package com.epam.esm;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository {

    Optional<Order> getOrderById(Long orderId);

    //TODO -- refactor order
//    Set<Order> getOrders(String order, int max, int offset);

    Set<Order> getOrders(HashMap<String,Boolean> sortParams, int max, int offset);

    Long createOrder(Order order);

    boolean delete(Long orderId);

    boolean orderAlreadyExists(Order order);
}
