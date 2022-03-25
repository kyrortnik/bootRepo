package com.epam.esm.impl;

import com.epam.esm.BaseRepository;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.*;


@Repository
public class OrderRepositoryHibernate extends BaseRepository implements OrderRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public OrderRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();
    }


    @Override
    public Optional<Order> getOrderById(Long orderId) {

        Session session = sessionFactory.openSession();
        List<Order> resultList = session.createQuery("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.giftCertificate g LEFT JOIN FETCH g.tags WHERE o.id = :orderId", Order.class)
                .setParameter("orderId",orderId)
                .getResultList();

        session.close();
        return resultList.isEmpty() ?  Optional.empty() :  Optional.of(resultList.get(0));

    }


    @Override
    public Set<Order> getOrders(HashMap<String, Boolean> sortParams, int max, int offset) {
        Session session = sessionFactory.openSession();
        String tableAlias = "o.";
        String query = "SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.giftCertificate g LEFT JOIN FETCH g.tags ORDER BY ";
        String queryWithParams = addParamsToQuery(sortParams, query, tableAlias);

        List<Order> resultList = session.createQuery(queryWithParams, Order.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
        session.close();
        return new HashSet<>(resultList);
    }

    @Override
    public Long createOrder(Order order) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long orderId = (Long) session.save(order);
        session.getTransaction().commit();
        session.close();
        return orderId;

    }

    @Override
    public boolean delete(Long orderId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("DELETE from Order where id = :id")
                .setParameter("id", orderId)
                .executeUpdate() > 0;
    }

    @Override
    public boolean orderAlreadyExists(Order order) {
        Session session = sessionFactory.openSession();
        boolean orderAlreadyExists = session.createQuery("SELECT o FROM Order o WHERE o.user = :user AND o.giftCertificate = :certificate")
                .setParameter("user", order.getUser())
                .setParameter("certificate", order.getGiftCertificate())
                .getResultList().size() > 0;
        session.close();
        return orderAlreadyExists;
    }
}
