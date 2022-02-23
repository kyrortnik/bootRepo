package com.epam.esm.impl;

import com.epam.esm.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Repository
public class TagRepositoryHibernate implements TagRepository {


    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();
    }

    @Override
    public Optional<Tag> getTag(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Tag.class, id));
    }


    @Override
    public List<Tag> getTags(String order, int max) {
        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT tag FROM Tag tag ORDER BY name " + order;

        return session.createQuery(queryString, Tag.class).setMaxResults(max).getResultList();
    }


    @Override
    public boolean delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("DELETE from Tag where id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Override
//    @Transactional
    public Long create(Tag tag) {
        Session session = sessionFactory.getCurrentSession();
        return (Long) session.save(tag);

    }

    @Override
    public List<Tag> getTagsForCertificate(Long id) {
        Session session = sessionFactory.getCurrentSession();
       return session.createQuery("SELECT t FROM Tag t LEFT JOIN t.certificates c WHERE c.id = :id",Tag.class)
                .setParameter("id",id).list();

    }


    /*Get the most widely used tag of a user with the highest cost of all orders*/
    @Override
    public Optional<Tag> getMostUsedTag() {
        Session firstSession = sessionFactory.getCurrentSession();
        User user = firstSession.createQuery(
                "SELECT u FROM User u LEFT JOIN u.orders o ORDER BY o.totalOrderAmount DESC", User.class)
                .setMaxResults(1)
                .getSingleResult();

        Set<Order> orders = user.getOrders();
//        List<GiftCertificate> giftCertificates = new ArrayList<>();
//        List<Tag> userTags = new ArrayList<>();
//        for (Order order : orders) {
//            Set<GiftCertificate> orderGiftCertificates = order.getGiftCertificates();
//            for (GiftCertificate giftCertificate : orderGiftCertificates) {
//                List<Tag> tags = Collectors.toList(giftCertificate.getTags());
//            }

        return Optional.empty();

        }
//        Set<GiftCertificate> giftCertificates = new HashSet<>();
//        giftCertificates.stream().flatMap(Collection::stream).collect(Collectors.toSet());
//
//        Set<GiftCertificate> userGiftCertificates = sessionFactory.getCurrentSession()

    }

