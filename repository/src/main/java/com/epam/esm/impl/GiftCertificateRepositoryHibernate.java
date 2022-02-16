package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepositoryOptional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Transactional
@Repository
public class GiftCertificateRepositoryHibernate implements GiftCertificateRepositoryOptional {

//    @PersistenceContext
    @Autowired
    private final HibernateTransactionManager transactionManager;


    public GiftCertificateRepositoryHibernate(HibernateTransactionManager transactionManager) {
        this.transactionManager = transactionManager;

    }

    @Override
    public Optional<GiftCertificate> getCertificate(Long id) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();
        return  Optional.ofNullable(session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags WHERE c.id = :id",GiftCertificate.class)
                .setParameter("id",id).list().get(0));
//         Optional.ofNullable(session.get(GiftCertificate.class, id));

    }

    @Override
    public List<GiftCertificate> getCertificates(String order, int max) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();
        String queryString = "SELECT cert FROM GiftCertificate cert ORDER BY name " + order;
        return  session.createQuery(queryString,GiftCertificate.class)
                .setMaxResults(max).getResultList();

    }
//    private static final String GET_CERTIFICATES_WITH_PARAMS =
//            "SELECT cert.id, cert.name, cert.description, cert.price, cert.duration, cert.create_date, cert.last_update_date\n" +
//                    "FROM\n" +
//                    "certificates AS cert\n" +
//                    "LEFT JOIN certificates_tags AS ct\n" +
//                    "ON cert.id = ct.certificate_id\n" +
//                    "LEFT JOIN tags\n" +
//                    "ON ct.tag_id = tags.id  WHERE  tags.name = COALESCE(:tag, tags.name) AND (cert.name LIKE COALESCE(:pattern, cert.name) OR cert.description LIKE COALESCE(:pattern, cert.description))\n" +
//                    "GROUP BY cert.id, cert.name,cert.description,cert.price,cert.duration,cert.create_date, cert.last_update_date\n" +
//                    "ORDER BY cert.name %s LIMIT :max";
//
    @Override
    public List<GiftCertificate> getCertificatesWithParams(String order, int max, String tag, String pattern) {
        Session session  = transactionManager.getSessionFactory().getCurrentSession();
//        session.createNativeQuery()
//        return session.createQuery("SELECT cert FROM GiftCertificate cert " +
//                "LEFT JOIN  ORDER BY :order LIMIT :max" + );
        return null;
    }


    @Override
    public boolean delete(Long id) {
       Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();
       return session.createQuery("DELETE from GiftCertificate where id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }

    @Override
    public Optional<GiftCertificate> update(GiftCertificate giftCertificate, long id) {

        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();

//        GiftCertificate existingGiftCertificate = new GiftCertificate(id);
        giftCertificate.setId(id);
//        session.save(existingGiftCertificate);
//        session.evict(existingGiftCertificate);
        return  Optional.ofNullable((GiftCertificate)session.merge(giftCertificate));

    }

    @Override
    public Long create(GiftCertificate element) {
        Session session = Objects.requireNonNull(transactionManager.getSessionFactory()).getCurrentSession();
        return (Long)session.save(element);
    }
}
