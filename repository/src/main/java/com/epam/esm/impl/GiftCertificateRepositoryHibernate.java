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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

        Session session = sessionFactory.getCurrentSession();
        List<GiftCertificate> resultSet = session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.orders LEFT JOIN FETCH c.tags WHERE c.id = :id", GiftCertificate.class)
                .setParameter("id", id).getResultList();

        return resultSet.isEmpty() ? Optional.empty() : Optional.of(resultSet.get(0));

    }

    @Override
    public Optional<GiftCertificate> getCertificateByName(String name) {

        Session session = sessionFactory.getCurrentSession();
        List<GiftCertificate> resultSet = session
                .createQuery("SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags WHERE c.name =:name ", GiftCertificate.class)
                .setParameter("name", name).getResultList();

        return resultSet.isEmpty() ? Optional.empty() : Optional.of(resultSet.get(0));
    }

    @Override
    public List<GiftCertificate> getCertificates(String order, int max, int offset) {

        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT c FROM GiftCertificate c LEFT JOIN FETCH c.tags ORDER BY c.name " + order;
        return session.createQuery(queryString, GiftCertificate.class)
                .setMaxResults(max)
                .setFirstResult(offset)
                .getResultList();

    }


    @Override
    public List<GiftCertificate> getCertificatesByTags(String order, int max, Set<Tag> tags, int offset) {

        List<String> tagNames = tags.stream().map(Tag::getName).collect(Collectors.toList());
        Session session = sessionFactory.getCurrentSession();
        String queryString = "SELECT DISTINCT c FROM Tag t LEFT JOIN t.certificates c WHERE t.name IN :tags ORDER BY c.name " + order;

        List<GiftCertificate> giftCertificates =
                session.createQuery(queryString, GiftCertificate.class)
                        .setParameterList("tags", tagNames)
                        .setMaxResults(max)
                        .setFirstResult(offset)
                        .getResultList();
        giftCertificates.removeIf(certificate -> !certificate.getTags().containsAll(tags));
        return giftCertificates;
    }


    @Override
    public boolean delete(Long id) {

        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("DELETE FROM GiftCertificate WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate() > 0;
    }


    @Override
    public Optional<GiftCertificate> update(GiftCertificate changedGiftCertificate, long id) {

        Session session = sessionFactory.getCurrentSession();
        GiftCertificate existingGiftCertificate = session.load(GiftCertificate.class, id);
        mergeTwoCertificates(existingGiftCertificate, changedGiftCertificate);
        return Optional.of((GiftCertificate) session.merge(existingGiftCertificate));

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
