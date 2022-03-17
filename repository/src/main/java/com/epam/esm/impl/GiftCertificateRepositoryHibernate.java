package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Transactional
@Repository
public class GiftCertificateRepositoryHibernate implements GiftCertificateRepository {

    private final SessionFactory sessionFactory;

    private final TagRepository tagRepository;

    @Autowired
    public GiftCertificateRepositoryHibernate(HibernateTransactionManager transactionManager, TagRepository tagRepository) {
        sessionFactory = transactionManager.getSessionFactory();
        this.tagRepository = tagRepository;

    }

    @Override
    public Optional<GiftCertificate> getCertificateById(Long id) {

        Session session = sessionFactory.openSession();
        List<GiftCertificate> resultSet = session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.orders LEFT JOIN FETCH c.tags WHERE c.id = :id", GiftCertificate.class)
                .setParameter("id", id).getResultList();

        session.close();
        return resultSet.isEmpty() ? Optional.empty() : Optional.of(resultSet.get(0));

    }

    @Override
    public Optional<GiftCertificate> getGiftCertificateByName(String name) {

        Session session = sessionFactory.openSession();
        List<GiftCertificate> resultSet = session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags WHERE c.name =:name ", GiftCertificate.class)
                .setParameter("name", name).getResultList();

        session.close();
        return resultSet.isEmpty() ? Optional.empty() : Optional.of(resultSet.get(0));
    }

    @Override
    public List<GiftCertificate> getCertificates(String order, int max, int offset) {

        Session session = sessionFactory.openSession();
        String queryString = "SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags ORDER BY c.name " + order;

        List<GiftCertificate> resultList =
                session.createQuery(queryString, GiftCertificate.class)
                        .setMaxResults(max)
                        .setFirstResult(offset)
                        .getResultList();
        session.close();
        return resultList;
    }


    @Override
    public List<GiftCertificate> getCertificatesByTags(String order, int max, Set<Tag> tags, int offset) {

        List<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toList());
        Session session = sessionFactory.openSession();
        String queryString = "SELECT DISTINCT c FROM Tag t LEFT JOIN t.certificates c WHERE t.name IN :tags ORDER BY c.name " + order;

        List<GiftCertificate> giftCertificates =
                session.createQuery(queryString, GiftCertificate.class)
                        .setParameterList("tags", tagNames)
                        .setMaxResults(max)
                        .setFirstResult(offset)
                        .getResultList();
        giftCertificates.removeIf(certificate -> !certificate.getTags().containsAll(tags));
        session.close();
        return giftCertificates;
    }


    @Override
    public boolean delete(Long id) {

        Session session = sessionFactory.openSession();
        boolean isDeletedGiftCertificate =
                session.createQuery("DELETE FROM GiftCertificate WHERE id = :id")
                        .setParameter("id", id)
                        .executeUpdate() > 0;
        session.close();
        return isDeletedGiftCertificate;

    }


    @Override
    public Optional<GiftCertificate> update(GiftCertificate changedGiftCertificate, long giftCertificateId) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            GiftCertificate existingGiftCertificate = getCertificateById(giftCertificateId)
                    .orElseThrow(() -> new NoSuchElementException("No Gift Certificate with name [" + changedGiftCertificate.getName() + "] exists"));
            Set<Tag> tagsToUpdate = changedGiftCertificate.getTags();
            Set<Tag> tags = new HashSet<>(tagsToUpdate);
            for (Tag tag : tagsToUpdate) {
                Optional<Tag> existingTag = tagRepository.getTagByName(tag.getName());
                if (existingTag.isPresent()) {
                    tags.remove(tag);
                    Tag proxyTag = session.load(Tag.class, existingTag.get().getId());
                    tags.add(proxyTag);
                }
            }
            mergeTwoCertificates(existingGiftCertificate, changedGiftCertificate);
            existingGiftCertificate.setTags(tags);
            Optional<GiftCertificate> mergedGiftCertificate = Optional.of((GiftCertificate) session.merge(existingGiftCertificate));
            session.getTransaction().commit();
            return mergedGiftCertificate;
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Certificate with id [" + giftCertificateId + "] doesn't exist");
        }


    }

    @Override
    public Long create(GiftCertificate giftCertificate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Set<Tag> tags = new HashSet<>(giftCertificate.getTags());
            for (Tag tag : giftCertificate.getTags()) {
                Optional<Tag> existingTag = tagRepository.getTagByName(tag.getName());
                if (existingTag.isPresent()) {
                    tags.remove(tag);
                    Tag proxyTag = session.load(Tag.class, existingTag.get().getId());
                    tags.add(proxyTag);
                    proxyTag.addCertificate(giftCertificate);
                    session.merge(proxyTag);
                }
            }
            giftCertificate.setTags(tags);
            Long giftCertificateId = (Long) session.save(giftCertificate);
            session.getTransaction().commit();
            return giftCertificateId;

        } catch (ConstraintViolationException e) {

            throw new DuplicateKeyException("Certificate with  name [" + giftCertificate.getName() + "] already exists");

        }
    }

    private void mergeTwoCertificates(GiftCertificate existingGiftCertificate, GiftCertificate changedGiftCertificate) {

        existingGiftCertificate.setDescription(nonNull(changedGiftCertificate.getDescription()) ? changedGiftCertificate.getDescription() : existingGiftCertificate.getDescription());
        existingGiftCertificate.setPrice(nonNull(changedGiftCertificate.getPrice()) ? changedGiftCertificate.getPrice() : existingGiftCertificate.getPrice());
        existingGiftCertificate.setDuration(nonNull(changedGiftCertificate.getDuration()) ? changedGiftCertificate.getDuration() : existingGiftCertificate.getDuration());
        existingGiftCertificate.setCreateDate(nonNull(changedGiftCertificate.getCreateDate()) ? changedGiftCertificate.getCreateDate() : existingGiftCertificate.getCreateDate());
        existingGiftCertificate.setLastUpdateDate(changedGiftCertificate.getLastUpdateDate());
        existingGiftCertificate.setTags(!changedGiftCertificate.getTags().isEmpty() ? changedGiftCertificate.getTags() : existingGiftCertificate.getTags());

    }
}
