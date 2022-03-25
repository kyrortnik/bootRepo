package com.epam.esm.impl;

import com.epam.esm.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Repository
public class GiftCertificateRepositoryHibernate extends BaseRepository implements GiftCertificateRepository {

    private final SessionFactory sessionFactory;

    private final TagRepository tagRepository;

    @Autowired
    public GiftCertificateRepositoryHibernate(HibernateTransactionManager transactionManager, TagRepository tagRepository) {
        sessionFactory = transactionManager.getSessionFactory();
        this.tagRepository = tagRepository;

    }

    @Override
    public Optional<GiftCertificate> getCertificateById(Long giftCertificateId) {
        Session session = sessionFactory.openSession();
        List<GiftCertificate> resultList = session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.orders LEFT JOIN FETCH c.tags WHERE c.id = :id",
                        GiftCertificate.class)
                .setParameter("id", giftCertificateId)
                .getResultList();

        session.close();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }


    @Override
    public Optional<GiftCertificate> getGiftCertificateByName(String giftCertificateName) throws NoResultException {
        Session session = sessionFactory.openSession();
        List<GiftCertificate> resultList = session.createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags WHERE c.name =:name ",
                GiftCertificate.class)
                .setParameter("name", giftCertificateName)
                .getResultList();

        session.close();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }


    @Override
    public List<GiftCertificate> getGiftCertificates(HashMap<String, Boolean> sortParams, int max, int offset) {
        Session session = sessionFactory.openSession();
        String tableAlias = "c.";
        String query = "SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags ORDER BY ";
        String queryWithParams = addParamsToQuery(sortParams, query, tableAlias);

        List<GiftCertificate> giftCertificates =
                session.createQuery(queryWithParams, GiftCertificate.class)
                        .setMaxResults(max)
                        .setFirstResult(offset)
                        .getResultList();
        session.close();
        return giftCertificates;
    }


    @Override
    public List<GiftCertificate> getGiftCertificatesByTags(HashMap<String, Boolean> sortParams, int max, Set<Tag> tags, int offset) {
        Session session = sessionFactory.openSession();
        List<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toList());
        String tableAlias = "c.";
        String query = "SELECT DISTINCT c FROM Tag t LEFT JOIN t.certificates c WHERE t.name IN :tags ORDER BY ";
        String queryWithParams = addParamsToQuery(sortParams, query, tableAlias);

        List<GiftCertificate> giftCertificates =
                session.createQuery(queryWithParams, GiftCertificate.class)
                        .setParameterList("tags", tagNames)
                        .setMaxResults(max)
                        .setFirstResult(offset)
                        .getResultList();
        giftCertificates.removeIf(certificate -> !certificate.getTags().containsAll(tags));
        session.close();
        return giftCertificates;
    }


    @Override
    public boolean deleteGiftCertificate(Long giftCertificateId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        boolean isDeletedGiftCertificate =
                session.createQuery("DELETE FROM GiftCertificate WHERE id = :id")
                        .setParameter("id", giftCertificateId)
                        .executeUpdate() > 0;
        session.getTransaction().commit();
        session.close();
        return isDeletedGiftCertificate;

    }


    //TODO -- replace try-with-resources, no need to throw exception
//    @Override
//    public Optional<GiftCertificate> updateGiftCertificate(GiftCertificate changedGiftCertificate, long giftCertificateId) {
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//            GiftCertificate existingGiftCertificate = getCertificateById(giftCertificateId)
//                    .orElseThrow(() -> new NoSuchElementException("No Gift Certificate with name [" + changedGiftCertificate.getName() + "] exists"));
//            Set<Tag> tagsToUpdate = changedGiftCertificate.getTags();
//            Set<Tag> tags = new HashSet<>(tagsToUpdate);
//            for (Tag tag : tagsToUpdate) {
//                Optional<Tag> existingTag = tagRepository.getTagByName(tag.getName());
//                if (existingTag.isPresent()) {
//                    tags.remove(tag);
//                    Tag proxyTag = session.load(Tag.class, existingTag.get().getId());
//                    tags.add(proxyTag);
//                }
//            }
//            mergeTwoCertificates(existingGiftCertificate, changedGiftCertificate);
//            existingGiftCertificate.setTags(tags);
//            Optional<GiftCertificate> mergedGiftCertificate = Optional.of((GiftCertificate) session.merge(existingGiftCertificate));
//            session.getTransaction().commit();
//            return mergedGiftCertificate;
//        } catch (NoSuchElementException e) {
//            throw new NoSuchElementException("Certificate with id [" + giftCertificateId + "] doesn't exist");
//        }
//
//    }

    @Override
    public Optional<GiftCertificate> updateGiftCertificate(GiftCertificate changedGiftCertificate, GiftCertificate existingGiftCertificate) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Set<Tag> processedTags = getTags(changedGiftCertificate.getTags(), session);
        existingGiftCertificate.mergeTwoGiftCertificate(changedGiftCertificate, processedTags);
        Optional<GiftCertificate> mergedGiftCertificate = Optional.of((GiftCertificate) session.merge(existingGiftCertificate));
        session.getTransaction().commit();
        return mergedGiftCertificate;

    }


//    @Override
//    public Long createGiftCertificate(GiftCertificate giftCertificate) {
//        try (Session session = sessionFactory.openSession()) {
//            session.beginTransaction();
//            Set<Tag> tags = new HashSet<>(giftCertificate.getTags());
//            for (Tag tag : giftCertificate.getTags()) {
//                Optional<Tag> existingTag = tagRepository.getTagByName(tag.getName());
//                if (existingTag.isPresent()) {
//                    tags.remove(tag);
//                    Tag proxyTag = session.load(Tag.class, existingTag.get().getId());
//                    tags.add(proxyTag);
//                    proxyTag.addCertificate(giftCertificate);
//                    session.merge(proxyTag);
//                }
//            }
//            giftCertificate.setTags(tags);
//            Long giftCertificateId = (Long) session.save(giftCertificate);
//            session.getTransaction().commit();
//            return giftCertificateId;
//
//        } catch (ConstraintViolationException e) {
//
//            throw new DuplicateKeyException("Certificate with  name [" + giftCertificate.getName() + "] already exists");
//
//        }
//    }


    public Long createGiftCertificate(GiftCertificate giftCertificate) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Set<Tag> tags = getTags(giftCertificate.getTags(), session);
        giftCertificate.setTags(tags);
        Long giftCertificateId = (Long) session.save(giftCertificate);
        session.getTransaction().commit();
        return giftCertificateId;

    }

    //TODO -- to tagRepository
    /**
     * @param tagsToUpdate new Tag Set for GiftCertificate from client
     * @param session      opened Session
     * @return Tags Set where existing tags were replaced with proxies and non-existing tags not changes so they could be created.
     */
    private Set<Tag> getTags(Set<Tag> tagsToUpdate, Session session) {
        Set<Tag> tags = new HashSet<>(tagsToUpdate);
        for (Tag tag : tagsToUpdate) {
            Optional<Tag> existingTag = tagRepository.getTagByName(tag.getName());
            if (existingTag.isPresent()) {
                tags.remove(tag);
                Tag proxyTag = session.load(Tag.class, existingTag.get().getId());
                tags.add(proxyTag);
            }
        }
        return tags;
    }


}
