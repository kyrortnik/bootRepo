package com.epam.esm.impl;

import com.epam.esm.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class GiftCertificateRepositoryHibernate extends BaseRepository implements GiftCertificateRepository {

    public static final Logger LOGGER = LoggerFactory.getLogger(BaseRepository.class);

    private final SessionFactory sessionFactory;

    private final TagRepository tagRepository;

    @Autowired
    public GiftCertificateRepositoryHibernate(HibernateTransactionManager transactionManager, TagRepository tagRepository) {
        sessionFactory = transactionManager.getSessionFactory();
        this.tagRepository = tagRepository;

    }

    @Override
    public Optional<GiftCertificate> getCertificateById(Long giftCertificateId) {
        LOGGER.info("Entering GiftCertificateRepositoryHibernate.getCertificateById()");
        Optional<GiftCertificate> foundGiftCertificate;

        Session session = sessionFactory.openSession();
        List<GiftCertificate> resultList = session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.orders LEFT JOIN FETCH c.tags WHERE c.id = :id",
                        GiftCertificate.class)
                .setParameter("id", giftCertificateId)
                .getResultList();
        session.close();

        foundGiftCertificate = resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
        LOGGER.info("Exiting GiftCertificateRepositoryHibernate.getCertificateById()");
        return foundGiftCertificate;
    }


    @Override
    public Optional<GiftCertificate> getGiftCertificateByName(String giftCertificateName) {
        LOGGER.info("Entering GiftCertificateRepositoryHibernate.getGiftCertificateByName()");
        Optional<GiftCertificate> foundGiftCertificate;

        Session session = sessionFactory.openSession();
        List<GiftCertificate> resultList = session.createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags WHERE c.name =:name ",
                GiftCertificate.class)
                .setParameter("name", giftCertificateName)
                .getResultList();
        session.close();

        foundGiftCertificate = resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
        LOGGER.info("Exiting GiftCertificateRepositoryHibernate.getCertificateById()");
        return foundGiftCertificate;
    }


    @Override
    public List<GiftCertificate> getGiftCertificates(HashMap<String, Boolean> sortParams, int max, int offset) {
        LOGGER.info("Entering GiftCertificateRepositoryHibernate.getGiftCertificates()");

        List<GiftCertificate> giftCertificates;
        Session session = sessionFactory.openSession();
        String tableAlias = "c.";
        String query = "SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags ORDER BY ";
        String queryWithParams = addParamsToQuery(sortParams, query, tableAlias);
        giftCertificates =
                session.createQuery(queryWithParams, GiftCertificate.class)
                        .setMaxResults(max)
                        .setFirstResult(offset)
                        .getResultList();
        session.close();

        LOGGER.info("Exiting GiftCertificateRepositoryHibernate.getGiftCertificates()");
        return giftCertificates;
    }


    @Override
    public List<GiftCertificate> getGiftCertificatesByTags(HashMap<String, Boolean> sortParams, int max, Set<Tag> tags, int offset) {
        LOGGER.info("Entering GiftCertificateRepositoryHibernate.getGiftCertificatesByTags()");

        List<GiftCertificate> giftCertificates;
        Session session = sessionFactory.openSession();
        List<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toList());
        String tableAlias = "c.";
        String query = "SELECT DISTINCT c FROM Tag t LEFT JOIN t.certificates c WHERE t.name IN :tags ORDER BY ";
        String queryWithParams = addParamsToQuery(sortParams, query, tableAlias);
        giftCertificates =
                session.createQuery(queryWithParams, GiftCertificate.class)
                        .setParameterList("tags", tagNames)
                        .setMaxResults(max)
                        .setFirstResult(offset)
                        .getResultList();
        giftCertificates.removeIf(certificate -> !certificate.getTags().containsAll(tags));
        session.close();

        LOGGER.info("Exiting GiftCertificateRepositoryHibernate.getGiftCertificatesByTags()");
        return giftCertificates;
    }


    @Override
    public boolean deleteGiftCertificate(Long giftCertificateId) {
        LOGGER.info("Entering GiftCertificateRepositoryHibernate.deleteGiftCertificate()");

        boolean isDeletedGiftCertificate;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        isDeletedGiftCertificate =
                session.createQuery("DELETE FROM GiftCertificate WHERE id = :id")
                        .setParameter("id", giftCertificateId)
                        .executeUpdate() > 0;
        session.getTransaction().commit();
        session.close();
        LOGGER.info("Exiting GiftCertificateRepositoryHibernate.deleteGiftCertificate()");
        return isDeletedGiftCertificate;

    }


    @Override
    public Optional<GiftCertificate> updateGiftCertificate(GiftCertificate changedGiftCertificate, GiftCertificate existingGiftCertificate) {
        LOGGER.info("Entering GiftCertificateRepositoryHibernate.updateGiftCertificate()");

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Set<Tag> processedTags = tagRepository.replaceExistingTagsWithProxy(changedGiftCertificate.getTags());
        existingGiftCertificate.mergeTwoGiftCertificate(changedGiftCertificate, processedTags);
        Optional<GiftCertificate> mergedGiftCertificate = Optional.of((GiftCertificate) session.merge(existingGiftCertificate));
        session.getTransaction().commit();

        LOGGER.info("Exiting GiftCertificateRepositoryHibernate.updateGiftCertificate()");
        return mergedGiftCertificate;

    }


    @Override
    public Long createGiftCertificate(GiftCertificate giftCertificate) {
        LOGGER.info("Entering GiftCertificateRepositoryHibernate.createGiftCertificate()");

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Set<Tag> processedTags = tagRepository.replaceExistingTagsWithProxy(giftCertificate.getTags());
        giftCertificate.setTags(processedTags);
        Long giftCertificateId = (Long) session.save(giftCertificate);
        session.getTransaction().commit();

        LOGGER.info("Exiting GiftCertificateRepositoryHibernate.createGiftCertificate()");
        return giftCertificateId;
    }


}
