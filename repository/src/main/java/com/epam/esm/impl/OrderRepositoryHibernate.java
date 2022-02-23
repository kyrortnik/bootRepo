package com.epam.esm.impl;

import com.epam.esm.Order;
import com.epam.esm.OrderRepository;
import com.epam.esm.Tag;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    @Override
    public Optional<Order> getOrder(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Order.class, id));
    }

    @Override
    public Set<Order> getOrders(String order, int max) {

        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT order FROM Order order ORDER BY name " + order;

        List<Order> orders = session.createQuery(queryString, Order.class).setMaxResults(max).getResultList();
        return new HashSet<>(orders);
    }

    //TODO -- replace RuntimeException
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
        return session.createQuery("SELECT o FROM Order o WHERE o.userId = :userId AND o.certificateId = :certificateId")
                .setParameter("userId", order.getUser().getId())
                .setParameter("certificateId", order.getGiftCertificate().getId())
                .getResultList().size() > 0;
    }
}
