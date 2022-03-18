package com.epam.esm.impl;

import com.epam.esm.User;
import com.epam.esm.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class UserRepositoryHibernate implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();
    }

    @Override
    public Optional<User> getUserById(Long id) {

        Session session = sessionFactory.getCurrentSession();
        User foundUser = session
                .createQuery("SELECT u FROM User u LEFT JOIN FETCH u.orders o WHERE u.id = :userId", User.class)
                .setParameter("userId", id)
                .setMaxResults(1)
                .getSingleResult();

        return Optional.of(foundUser);

    }

    @Override
    public List<User> getUsers(String order, int max, int offset) {
        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT user FROM User ORDER BY name " + order;

        return session.createQuery(queryString, User.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
    }
}
