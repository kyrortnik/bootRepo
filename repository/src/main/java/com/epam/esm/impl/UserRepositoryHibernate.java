package com.epam.esm.impl;

import com.epam.esm.User;
import com.epam.esm.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public List<User> getUsers(HashMap<String, Boolean> sortingParams, int max, int offset) {
        Session session = sessionFactory.openSession();
        String queryString = formatGetUsersQuery(sortingParams);
        List<User> resultList = session.createQuery(queryString, User.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();

        session.close();
        return resultList;
    }


    private String formatGetUsersQuery(HashMap<String, Boolean> sortingParams) {
        Set<Map.Entry<String, Boolean>> paramsPairs = sortingParams.entrySet();
        StringBuilder originalQuery = new StringBuilder("SELECT user FROM User user LEFT JOIN FETCH  user.orders ORDER BY ");
        String comma = ", ";

        for (Map.Entry<String,Boolean> paramPair : paramsPairs) {
            originalQuery.append("user.");
            originalQuery.append(paramPair.getKey());
            originalQuery.append(paramPair.getValue() ? " ASC" : " DESC");
            originalQuery.append(comma);
        }
        return originalQuery.substring(0, originalQuery.length() - 2);

    }
}
