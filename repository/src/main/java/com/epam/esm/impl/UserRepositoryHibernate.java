package com.epam.esm.impl;

import com.epam.esm.BaseRepository;
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
public class UserRepositoryHibernate extends BaseRepository implements UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();
    }

    @Override
    public Optional<User> getUserById(Long id) {

        Session session = sessionFactory.getCurrentSession();
        User foundUser = session
                .createQuery("SELECT u FROM User u LEFT JOIN FETCH u.orders o " +
                        "LEFT JOIN FETCH o.giftCertificate g " +
                        "LEFT JOIN FETCH g.tags " +
                        "WHERE u.id = :userId", User.class)
                .setParameter("userId", id)
                .setMaxResults(1)
                .getSingleResult();

        return Optional.of(foundUser);

    }


    @Override
    public List<User> getUsers(HashMap<String, Boolean> sortingParams, int max, int offset) {

        Session session = sessionFactory.openSession();
        String tableAlias = "user.";
        String query = "SELECT user FROM User user LEFT JOIN FETCH  user.orders ORDER BY ";
        String queryWithParams = addParamsToQuery(sortingParams, query, tableAlias);
        List<User> resultList = session.createQuery(queryWithParams, User.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();

        session.close();
        return resultList;
    }

}
