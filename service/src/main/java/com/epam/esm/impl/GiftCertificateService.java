package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;

@Transactional
@Service
public class GiftCertificateService implements CRUDService<GiftCertificate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private final GiftCertificateRepository giftCertificateRepository;

    private final TagService tagService;

    @Autowired
    public GiftCertificateService(GiftCertificateRepository giftCertificateRepository, TagService tagService) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagService = tagService;
    }


    @Transactional
    @Override
    public Optional<GiftCertificate> findById(Long id) {
        LOGGER.debug("Entering GiftCertificateService.getById()");

        Optional<GiftCertificate> foundGiftCertificate = giftCertificateRepository.findById(id);

        LOGGER.debug("Exiting GiftCertificateService.getById()");
        return foundGiftCertificate;
    }

    public Optional<GiftCertificate> findGiftCertificateByName(String giftCertificateName) {
        LOGGER.debug("Entering GiftCertificateService.getGiftCertificateByName()");

        Optional<GiftCertificate> foundGiftCertificate = giftCertificateRepository.findByName(giftCertificateName);

        LOGGER.debug("Exiting GiftCertificateService.getGiftCertificateByName()");
        return foundGiftCertificate;

    }

    public Page<GiftCertificate> getGiftCertificates(Set<String> tagNames, Sort sortingParams, int max, int offset) {
        Page<GiftCertificate> giftCertificates = isNull(tagNames)
                ? getAll(sortingParams, max, offset)
                : getCertificatesByTags(sortingParams, max, offset, tagNames);
        if (giftCertificates.getContent().isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in GiftCertificateController.getCertificates()\n" +
                    "No Satisfying Gift Certificates exists");
            throw new NoSuchElementException("No Satisfying Gift Certificates exist");
        }
        return giftCertificates;
    }


    @Override
    public Page<GiftCertificate> getAll(Sort sortParams, int max, int offset) {
        LOGGER.debug("Entering GiftCertificateService.getAll()");

        Page<GiftCertificate> foundGiftCertificates = giftCertificateRepository.findAll(PageRequest.of(offset, max, sortParams));

        LOGGER.debug("Exiting GiftCertificateService.getAll()");
        return foundGiftCertificates;
    }

    //TODO -- fix return. Return duplicates, 'and' condition is not met
    public Page<GiftCertificate> getCertificatesByTags(Sort sortParams, int max, int offset, Set<String> tagNames) {
        LOGGER.debug("Entering GiftCertificateService.getCertificatesByTags()");

        Set<Tag> tags = tagService.getTagsByNames(tagNames);
        Page<GiftCertificate> foundGiftCertificates = giftCertificateRepository.findByTagsIn(tags, PageRequest.of(offset, max, sortParams));

        LOGGER.debug("Exiting GiftCertificateService.getCertificatesByTags()");
        return foundGiftCertificates;
    }

    @Override
    public boolean delete(Long giftCertificateId) {
        LOGGER.debug("Entering GiftCertificateService.delete()");

        boolean giftCertificateIsDeleted;
        giftCertificateRepository.deleteById(giftCertificateId);

        giftCertificateIsDeleted = giftCertificateRepository.findById(giftCertificateId).isPresent();

        LOGGER.debug("Exiting GiftCertificateService.delete()");
        return giftCertificateIsDeleted;
    }


    @Override
    public void update(GiftCertificate changedGiftCertificate, Long giftCertificateId) throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.update()");

        GiftCertificate existingGiftCertificate = findById(giftCertificateId)
                .orElseThrow(() -> new NoSuchElementException("No Gift Certificate with id [" + giftCertificateId + "] exists"));
        changedGiftCertificate.setLastUpdateDate(LocalDateTime.now());
        Set<Tag> updatedTags = replaceExistingWithProxy(changedGiftCertificate.getTags());

        changedGiftCertificate.setTags(updatedTags);
        existingGiftCertificate.mergeTwoGiftCertificate(changedGiftCertificate);
        giftCertificateRepository.save(existingGiftCertificate);

        LOGGER.debug("Exiting GiftCertificateService.update()");
    }


    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {

        LOGGER.debug("Entering GiftCertificateService.create()");

        if (findGiftCertificateByName(giftCertificate.getName()).isPresent()) {
            throw new ConstraintViolationException
                    ("Gift Certificate with name [" + giftCertificate.getName() + "] already exists"
                            , new SQLException(), "gift certificate name");
        }
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());

        Set<Tag> updatedTags = replaceExistingWithProxy(giftCertificate.getTags());
        giftCertificate.setTags(updatedTags);
        GiftCertificate createdGiftCertificate = giftCertificateRepository.save(giftCertificate);

        LOGGER.debug("Exiting GiftCertificateService.create()");
        return createdGiftCertificate;
    }


    public Set<Tag> getCertificateTags(Long giftCertificateId) {

        Set<Tag> giftCertificateTag = tagService.getTagsForCertificate(giftCertificateId);

        return giftCertificateTag;
    }

    private Set<Tag> replaceExistingWithProxy(Set<Tag> tags) {
        Set<Tag> updatedTags = new HashSet<>();
        for (Tag tag : tags) {
            Optional<Tag> foundTag = tagService.findTagByName(tag.getName());
            if (foundTag.isPresent()) {
                updatedTags.remove(tag);
                Tag proxyTag = tagService.getById(foundTag.get().getId());
                updatedTags.add(proxyTag);
            }
        }
        return updatedTags;
    }

}
