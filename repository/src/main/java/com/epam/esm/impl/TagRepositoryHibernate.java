package com.epam.esm.impl;

import com.epam.esm.BaseRepository;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.*;

@Transactional
@Repository
public class TagRepositoryHibernate extends BaseRepository implements TagRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(TagRepositoryHibernate.class);

    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();
    }

    @Override
    public Optional<Tag> getTagById(Long tagId) {
        LOGGER.info("Entering TagRepositoryHibernate.getTagById()");

        Session session = sessionFactory.openSession();
        Optional<Tag> tag = Optional.ofNullable(session.get(Tag.class, tagId));
        session.close();

        LOGGER.info("Exiting TagRepositoryHibernate.getTagById()");
        return tag;
    }


    @Override
    public List<Tag> getTags(HashMap<String, Boolean> sortParams, int max, int offset) {
        LOGGER.info("Entering TagRepositoryHibernate.getTags()");

        List<Tag> resultList;
        Session session = sessionFactory.openSession();
        String tableAlias = "t.";
        String query = "SELECT t FROM Tag t ORDER BY ";
        String queryWithParams = addParamsToQuery(sortParams, query, tableAlias);

        resultList = session.createQuery(queryWithParams, Tag.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();
        session.close();
        LOGGER.info("Exiting TagRepositoryHibernate.getTags()");
        return resultList;

    }


    @Override
    public boolean delete(Long tagId) {
        LOGGER.info("Entering TagRepositoryHibernate.delete()");

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        boolean tagIsDeleted = session.createQuery("DELETE from Tag where id = :id")
                .setParameter("id", tagId)
                .executeUpdate() > 0;
        session.getTransaction().commit();
        session.close();

        LOGGER.info("Exiting TagRepositoryHibernate.delete()");
        return tagIsDeleted;
    }

    @Override
    public Long createTag(Tag tag) {
        LOGGER.info("Entering TagRepositoryHibernate.createTag()");

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long createdTagId = (Long) session.save(tag);
        session.getTransaction().commit();
        session.close();

        LOGGER.info("Exiting TagRepositoryHibernate.createTag()");
        return createdTagId;
    }

    @Override
    public Optional<Tag> getTagByName(String tagName) {
        LOGGER.info("Entering TagRepositoryHibernate.getTagByName()");

        Optional<Tag> foundTag;
        Session session = sessionFactory.openSession();
        List<Tag> resultList = session.createQuery("SELECT t FROM Tag t WHERE t.name = :tagName", Tag.class)
                .setParameter("tagName", tagName)
                .getResultList();
        session.close();
        foundTag = resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));

        LOGGER.info("Exiting TagRepositoryHibernate.getTagByName()");
        return foundTag;

    }


    @Override
    public Optional<Tag> getMostUsedTagForRichestUser() throws NoResultException {
        LOGGER.info("Entering TagRepositoryHibernate.getMostUsedTagForRichestUser()");

        Optional<Tag> tag;
        Session session = sessionFactory.openSession();
        long richestUserId = getRichestUserId();
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
        tag = getTagByName(mostUsedTagName);
        session.close();

        LOGGER.info("Exiting TagRepositoryHibernate.getMostUsedTagForRichestUser()");
        return tag;
    }


    @Override
    public Set<Tag> replaceExistingTagsWithProxy(Set<Tag> tagsToUpdate) {
        LOGGER.info("Entering TagRepositoryHibernate.replaceExistingTagsWithProxy()");

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

        LOGGER.info("Exiting TagRepositoryHibernate.replaceExistingTagsWithProxy()");
        return tags;
    }


    private long getRichestUserId() throws NoResultException {
        LOGGER.info("Entering TagRepositoryHibernate.getRichestUserId()");

        Session session = sessionFactory.openSession();
        long richestUserId = (long) (Integer) session.createNativeQuery(
                "SELECT u.id FROM users AS u\n" +
                        "LEFT JOIN orders AS o ON u.id = o.user_id WHERE o.order_cost IS NOT NULL\n" +
                        "GROUP BY u.id\n" +
                        "ORDER BY SUM(o.order_cost) DESC")
                .setMaxResults(1)
                .getSingleResult();

        session.close();

        LOGGER.info("Exiting TagRepositoryHibernate.getRichestUserId()");
        return richestUserId;
    }

}
