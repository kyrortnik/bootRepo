package com.epam.esm.impl;

import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Repository
public class TagRepositoryHibernate implements TagRepository {

    @Autowired
    private final HibernateTransactionManager transactionManager;
//
//    private static final RowMapper<Tag> MAPPER_TAG =
//            (rs, i) -> new Tag(rs.getLong("id"),
//                    rs.getString("name"));

    @Autowired
    public TagRepositoryHibernate(HibernateTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Optional<Tag> getTag(Long id) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();

        return Optional.ofNullable(session.get(Tag.class, id));
    }


    @Override
    public List<Tag> getTags(String order, int max) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();
        String queryString = "SELECT tag FROM Tag tag ORDER BY name " + order;

        return session.createQuery(queryString, Tag.class)
                .setMaxResults(max).getResultList();

    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();
        return session.createQuery("DELETE from Tag where id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Override
    @Transactional
    public Long create(Tag tag) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();
        return (Long) session.save(tag);

    }

    @Override
    public List<Tag> getTagsForCertificate(Long id) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();
       return session.createQuery("SELECT t FROM Tag t LEFT JOIN t.certificates c WHERE c.id = :id",Tag.class)
                .setParameter("id",id).list();

    }
}
