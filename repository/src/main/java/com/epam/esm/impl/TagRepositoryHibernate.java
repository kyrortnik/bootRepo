package com.epam.esm.impl;

import com.epam.esm.BaseRepository;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@Repository
public class TagRepositoryHibernate extends BaseRepository implements TagRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();
    }

    @Override
    public Optional<Tag> getTagById(Long tagId) {
        Session session = sessionFactory.getCurrentSession();
        return Optional.ofNullable(session.get(Tag.class, tagId));
    }


    @Override
    public List<Tag> getTags(HashMap<String, Boolean> sortParams, int max, int offset) {
        Session session = sessionFactory.openSession();
        String tableAlias = "t.";
        String query = "SELECT t FROM Tag t ORDER BY ";
        String queryWithParams = addParamsToQuery(sortParams, query, tableAlias);

        List<Tag> resultList = session.createQuery(queryWithParams, Tag.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
        session.close();
        return resultList;

    }


    @Override
    public boolean delete(Long tagId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("DELETE from Tag where id = :id")
                .setParameter("id", tagId)
                .executeUpdate() > 0;
    }

    @Override
    public Long createTag(Tag tag) {
        Session session = sessionFactory.getCurrentSession();
        return (Long) session.save(tag);

    }


//    @Override
//    public Optional<Tag> getTagByName(String tagName) {
//        Session session = sessionFactory.getCurrentSession();
//
//        List<Tag> resultList = session.createQuery("SELECT t FROM Tag t WHERE t.name = :tagName", Tag.class)
//                .setParameter("tagName", tagName)
//                .getResultList();
//
//        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
//
//    }

    @Override
    public Optional<Tag> getTagByName(String tagName) {
        try {
            Session session = sessionFactory.openSession();

            Optional<Tag> tag = Optional.of(session.createQuery("SELECT t FROM Tag t WHERE t.name = :tagName", Tag.class)
                    .setParameter("tagName", tagName)
                    .setMaxResults(1)
                    .getSingleResult());

            session.close();
            return tag;
        } catch (NoResultException e) {
            throw new NoSuchElementException("No tag with name [" + tagName + "] exists");
        }


    }

    @Override
    public Optional<Tag> getMostUsedTagForRichestUser() {
//        Session session = sessionFactory.getCurrentSession();
        Session session = sessionFactory.openSession();

        long richestUserId = getRichestUserId(session);

        String mostUsedTagName = (String) session.createNativeQuery(
                "SELECT t.name FROM tags AS t\n" +
                        "LEFT JOIN certificates_tags AS ct ON t.id = ct.tag_id\n" +
                        "LEFT JOIN certificates AS c ON ct.certificate_id =  c.id\n" +
                        "LEFT JOIN orders AS o ON c.id = o.gift_certificate_id\n" +
                        "LEFT JOIN users AS u ON o.user_id = u.id WHERE u.id = :id\n" +
                        "GROUP BY t.name\n" +
                        "ORDER BY COUNT(t.name) DESC")
                .setMaxResults(1)
                .setParameter("id", richestUserId)
                .getSingleResult();

        return getTagByName(mostUsedTagName);
    }


    //    private long getRichestUserId(Session session) {
//        return (long) (Integer) session.createNativeQuery(
//                "SELECT u.id FROM users AS u\n" +
//                        "LEFT JOIN orders AS o ON u.id = o.user_id \n" +
//                        "GROUP BY u.id\n" +
//                        "ORDER BY SUM(o.order_cost) DESC")
//                .setMaxResults(1)
//                .getSingleResult();
//    }
    private long getRichestUserId(Session session) {
        try {
            return (long) (Integer) session.createNativeQuery(
                    "SELECT u.id FROM users AS u\n" +
                            "LEFT JOIN orders AS o ON u.id = o.user_id WHERE o.order_cost IS NOT NULL\n" +
                            "GROUP BY u.id\n" +
                            "ORDER BY SUM(o.order_cost) DESC")
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new NoSuchElementException("No tags exist");
        }

    }

}
