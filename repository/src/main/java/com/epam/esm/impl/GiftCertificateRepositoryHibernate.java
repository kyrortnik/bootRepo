package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.GiftCertificateRepositoryOptional;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public class GiftCertificateRepositoryHibernate implements GiftCertificateRepositoryOptional {

    @PersistenceContext
    private final HibernateTransactionManager transactionManager;


    public GiftCertificateRepositoryHibernate(HibernateTransactionManager transactionManager) {
        this.transactionManager = transactionManager;

    }

    @Override
    public Optional<GiftCertificate> getCertificate(Long id) {
        Session session = transactionManager.getSessionFactory().openSession();
        return Optional.ofNullable(session.get(GiftCertificate.class, id));

    }

    @Override
    public List<GiftCertificate> getCertificates(String order, int max) {
        return null;
    }

    @Override
    public List<GiftCertificate> getCertificatesWithParams(String order, int max, String tag, String pattern) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public boolean update(GiftCertificate element, long id) {
        return false;
    }

    @Override
    public GiftCertificate create(GiftCertificate element) {
        return null;
    }
}
