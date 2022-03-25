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
import java.util.*;

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
        Session session = sessionFactory.openSession();
        Optional<Tag> tag = Optional.ofNullable(session.get(Tag.class, tagId));
        session.close();
        return tag;
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
        Session session = sessionFactory.openSession();
        boolean tagIsDeleted = session.createQuery("DELETE from Tag where id = :id")
                .setParameter("id", tagId)
                .executeUpdate() > 0;
        session.close();
        return tagIsDeleted;
    }

    @Override
    public Long createTag(Tag tag) {
        Session session = sessionFactory.openSession();
        Long createdTagId = (Long) session.save(tag);
        session.close();
        return createdTagId;
    }

    @Override
    public Optional<Tag> getTagByName(String tagName) {
        Session session = sessionFactory.openSession();
        List<Tag> resultList = session.createQuery("SELECT t FROM Tag t WHERE t.name = :tagName", Tag.class)
                .setParameter("tagName", tagName)
                .getResultList();

        session.close();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));

    }


    @Override
    public Optional<Tag> getMostUsedTagForRichestUser() throws NoResultException {
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


    @Override
    public Set<Tag> replaceExistingTagsWithProxy(Set<Tag> tagsToUpdate) {
        Session session = sessionFactory.openSession();
        Set<Tag> tags = new HashSet<>(tagsToUpdate);
        for (Tag tag : tagsToUpdate) {
            Optional<Tag> existingTag = getTagByName(tag.getName());
            if (existingTag.isPresent()) {
                tags.remove(tag);
                Tag proxyTag = session.load(Tag.class, existingTag.get().getId());
                tags.add(proxyTag);
            }
        }
        session.close();
        return tags;
    }


    private long getRichestUserId(Session session) throws NoResultException {

        return (long) (Integer) session.createNativeQuery(
                "SELECT u.id FROM users AS u\n" +
                        "LEFT JOIN orders AS o ON u.id = o.user_id WHERE o.order_cost IS NOT NULL\n" +
                        "GROUP BY u.id\n" +
                        "ORDER BY SUM(o.order_cost) DESC")
                .setMaxResults(1)
                .getSingleResult();
    }

}
