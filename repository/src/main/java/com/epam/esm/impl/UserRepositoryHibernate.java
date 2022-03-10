package com.epam.esm.impl;

import com.epam.esm.*;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        List<User> resultSet = session
                .createQuery("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id", User.class)
//                .createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
                .setParameter("id", id).getResultList();

        return resultSet.isEmpty() ? Optional.empty() : Optional.of(resultSet.get(0));



    }

    @Override
    public Set<User> getUsers(String order, int max) {
        return null;
    }
}
