package com.epam.esm.impl;

import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@Repository
public class OrderRepositoryHibernate implements OrderRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public OrderRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();
    }

//    @Override
//    public Optional<Order> getOrder(Long id) {
//        Session session = sessionFactory.getCurrentSession();
//        return Optional.ofNullable(session.get(Order.class, id));
//    }

    @Override
    public Optional<Order> getOrder(Long id) {
        Session session = sessionFactory.getCurrentSession();
        List<Order> resultSet = session
                .createQuery("SELECT o FROM Order o LEFT JOIN FETCH o.giftCertificate LEFT JOIN FETCH o.user WHERE o.id = :id", Order.class)
                .setParameter("id", id)
                .getResultList();

        return resultSet.isEmpty() ? Optional.empty() : Optional.of(resultSet.get(0));
    }

    @Override
    public Set<Order> getOrders(String order, int max, int offset) {

        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT order FROM Order order ORDER BY name " + order;

        List<Order> orders = session.createQuery(queryString, Order.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
        return new HashSet<>(orders);
    }

    @Override
    public Long createOrder(Order order) {
        Session session = sessionFactory.getCurrentSession();
        return (Long) session.save(order);

    }

    @Override
    public boolean delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("DELETE from Order where id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Override
    public boolean orderAlreadyExists(Order order) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT o FROM Order o WHERE o.user = :user AND o.giftCertificate = :certificate")
                .setParameter("user", order.getUser())
                .setParameter("certificate", order.getGiftCertificate())
                .getResultList().size() > 0;
    }
}
