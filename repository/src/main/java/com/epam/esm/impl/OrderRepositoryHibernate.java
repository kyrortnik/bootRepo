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

import javax.persistence.NoResultException;
import java.sql.ResultSet;
import java.util.*;

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


    /*  Session session = sessionFactory.openSession();
        GiftCertificate foundGiftCertificate = session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags WHERE c.name =:name ", GiftCertificate.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getSingleResult();

        session.close();
        return Optional.of(foundGiftCertificate);*/

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        try{
            Session session = sessionFactory.openSession();
            Order order = session.createQuery("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.giftCertificate g LEFT JOIN FETCH g.tags WHERE o.id = :orderId",Order.class)
                    .setParameter("orderId",orderId)
                    .setMaxResults(1)
                    .getSingleResult();

            session.close();
            return Optional.ofNullable(order);
        }catch (NoResultException e){
            throw new NoSuchElementException("No order with id[" + orderId + "] exists");
        }

    }

    @Override
    public Set<Order> getOrders(String order, int max, int offset) {

        Session session = sessionFactory.openSession();
        String queryString = "SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.giftCertificate g LEFT JOIN FETCH g.tags ORDER BY o.id " + order;

        List<Order> orders = session.createQuery(queryString, Order.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
        session.close();
        return new HashSet<>(orders);
    }

    @Override
    public Long createOrder(Order order) {
        Session session = sessionFactory.openSession();
        Long orderId = (Long) session.save(order);
        session.close();
        return orderId;

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
