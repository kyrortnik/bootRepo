package com.epam.esm.impl;

import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
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
    public List<Tag> getTags(String order, int max, int offset) {
        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT tag FROM Tag tag ORDER BY name " + order;

        return session.createQuery(queryString, Tag.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
    }


    @Override
    public boolean delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("DELETE from Tag where id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Override
    public Long createTag(Tag tag) {
        Session session = sessionFactory.getCurrentSession();
        return (Long) session.save(tag);

    }


    @Override
    public Optional<Tag> getTagByName(String tagName) {
        Session session = sessionFactory.getCurrentSession();

        List<Tag> resultList = session.createQuery("SELECT t FROM Tag t WHERE t.name = :tagName", Tag.class)
                .setParameter("tagName", tagName)
                .getResultList();

        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));

    }

    @Override
    public Optional<Tag> getMostUsedTagForRichestUser() {
        Session session = sessionFactory.getCurrentSession();
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


    private long getRichestUserId(Session session) {
        return (long) /*(Integer)*/ session.createNativeQuery(
                "SELECT u.id FROM users AS u\n" +
                        "LEFT JOIN orders AS o ON u.id = o.user_id \n" +
                        "GROUP BY u.id\n" +
                        "ORDER BY SUM(o.order_cost) DESC")
                .setMaxResults(1)
                .getSingleResult();
    }

}
