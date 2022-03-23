package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import com.epam.esm.User;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.OpInc;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//@Transactional
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
    public Optional<Order> getOrderById(Long orderId) {
        Session session = sessionFactory.openSession();
        Optional<Order> order = Optional.ofNullable(session.createQuery("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.giftCertificate g LEFT JOIN FETCH g.tags WHERE o.id = :orderId",Order.class)
                .setParameter("orderId",orderId)
                .setMaxResults(1)
                .getSingleResult());
        session.close();
        return order;
    }

    @Override
    public Set<Order> getOrders(String order, int max, int offset) {

        Session session = sessionFactory.openSession();
        String queryString = "SELECT order FROM Order order ORDER BY id " + order;

        List<Order> orders = session.createQuery(queryString, Order.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
        session.close();
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
