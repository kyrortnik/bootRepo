package com.epam.esm.impl;

import com.epam.esm.BaseRepository;
import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Repository
public class OrderRepositoryHibernate extends BaseRepository implements OrderRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrderRepositoryHibernate.class);

    private final SessionFactory sessionFactory;

    @Autowired
    public OrderRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();
    }


    @Override
    public Optional<Order> getOrderById(Long orderId) {
        LOGGER.info("Entering OrderRepositoryHibernate.getOrderById()");

        Optional<Order> foundOrder;
        Session session = sessionFactory.openSession();
        List<Order> resultList = session.createQuery("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.giftCertificate g LEFT JOIN FETCH g.tags WHERE o.id = :orderId", Order.class)
                .setParameter("orderId", orderId)
                .getResultList();
        session.close();
        foundOrder = resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));

        LOGGER.info("exiting OrderRepositoryHibernate.getOrderById()");
        return foundOrder;

    }


    @Override
    public List<Order> getOrders(HashMap<String, Boolean> sortParams, int max, int offset) {
        LOGGER.info("Entering OrderRepositoryHibernate.getOrders()");

        Session session = sessionFactory.openSession();
        String tableAlias = "o.";
        String query = "SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.giftCertificate g LEFT JOIN FETCH g.tags ORDER BY ";
        String queryWithParams = addParamsToQuery(sortParams, query, tableAlias);
        List<Order> resultList = session.createQuery(queryWithParams, Order.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
        session.close();

        LOGGER.info("Exiting OrderRepositoryHibernate.getOrders()");
        return resultList;
    }

    @Override
    public Long createOrder(Order order) {
        LOGGER.info("Entering OrderRepositoryHibernate.createOrder()");

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long orderId = (Long) session.save(order);
        session.getTransaction().commit();
        session.close();

        LOGGER.info("Exiting OrderRepositoryHibernate.createOrder()");
        return orderId;

    }

    @Override
    public boolean delete(Long orderId) {
        LOGGER.info("Entering OrderRepositoryHibernate.delete()");

        Session session = sessionFactory.getCurrentSession();
        boolean orderIsDeleted = session.createQuery("DELETE from Order where id = :id")
                .setParameter("id", orderId)
                .executeUpdate() > 0;
        session.close();

        LOGGER.info("Exiting OrderRepositoryHibernate.delete()");
        return orderIsDeleted;
    }

    @Override
    public boolean orderAlreadyExists(Order order) {
        LOGGER.info("Entering OrderRepositoryHibernate.orderAlreadyExists()");

        Session session = sessionFactory.openSession();
        boolean orderAlreadyExists = session.createQuery("SELECT o FROM Order o WHERE o.user = :user AND o.giftCertificate = :certificate")
                .setParameter("user", order.getUser())
                .setParameter("certificate", order.getGiftCertificate())
                .getResultList().size() > 0;
        session.close();

        LOGGER.info("Exiting OrderRepositoryHibernate.orderAlreadyExists()");
        return orderAlreadyExists;
    }
}
