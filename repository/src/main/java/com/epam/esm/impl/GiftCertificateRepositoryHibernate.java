package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
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
public class GiftCertificateRepositoryHibernate implements GiftCertificateRepository {

    private final SessionFactory sessionFactory;


    @Autowired
    public GiftCertificateRepositoryHibernate(HibernateTransactionManager transactionManager) {
        sessionFactory = transactionManager.getSessionFactory();

    }

    @Override
    public Optional<GiftCertificate> getCertificate(Long id) {

        Session session = sessionFactory.getCurrentSession();
        List<GiftCertificate> resultSet = session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags WHERE c.id = :id", GiftCertificate.class)
                .setParameter("id", id).getResultList();

        return resultSet.isEmpty() ? Optional.empty() : Optional.of(resultSet.get(0));

    }

    @Override
    public List<GiftCertificate> getCertificates(String order, int max) {
        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags ORDER BY c.name " + order;
        return session.createQuery(queryString, GiftCertificate.class).setMaxResults(max).getResultList();

    }

    @Override
    public List<GiftCertificate> getCertificatesWithParams(String order, int max, String tag, String pattern) {
        Session session = sessionFactory.getCurrentSession();
        String query =
                "SELECT c FROM GiftCertificate c LEFT JOIN c.tags t LEFT JOIN FETCH c.tags WHERE (t.name = :tag OR :tag is null) " +
                        "AND (description LIKE :pattern OR c.name LIKE :pattern OR :pattern is null) " +
                        "ORDER BY c.name " + order;


        return session.createQuery(query, GiftCertificate.class)
                .setParameter("tag", tag)
                .setParameter("pattern", pattern)
                .setMaxResults(max)
                .getResultList();

    }


    @Override
    public boolean delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("DELETE from GiftCertificate where id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    //TODO puts nulls - not a patch
    @Override
    public Optional<GiftCertificate> update(GiftCertificate giftCertificate, long id) {
        Session session = sessionFactory.getCurrentSession();

        giftCertificate.setId(id);
        return Optional.ofNullable((GiftCertificate) session.merge(giftCertificate));

    }

    //TODO -- new tags not created while creating certificate
    //TODO -- duplicateKeyEx not handled
    @Override
    public Long create(GiftCertificate giftCertificate) {
        Session session = sessionFactory.getCurrentSession();
        return (Long) session.save(giftCertificate);
    }
}
