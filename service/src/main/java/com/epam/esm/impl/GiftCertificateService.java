package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;


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
    public Optional<GiftCertificate> getById(Long id) {
        LOGGER.info("Entering GiftCertificateService.getById()");

        Optional<GiftCertificate> foundGiftCertificate = giftCertificateRepository.getCertificateById(id);

        LOGGER.info("Exiting GiftCertificateService.getById()");
        return foundGiftCertificate;
    }

    public Optional<GiftCertificate> getGiftCertificateByName(String giftCertificateName) {
        LOGGER.info("Entering GiftCertificateService.getGiftCertificateByName()");

        Optional<GiftCertificate> foundGiftCertificate = giftCertificateRepository.getGiftCertificateByName(giftCertificateName);

        LOGGER.info("Exiting GiftCertificateService.getGiftCertificateByName()");
        return foundGiftCertificate;

    }


    @Override
    public List<GiftCertificate> getAll(HashMap<String, Boolean> sortParams, int max, int offset) {
        LOGGER.info("Entering GiftCertificateService.getAll()");
        List<GiftCertificate> foundGiftCertificates = giftCertificateRepository.getGiftCertificates(sortParams, max, offset);

        LOGGER.info("Exiting GiftCertificateService.getAll()");
        return foundGiftCertificates;
    }


    public List<GiftCertificate> getCertificatesByTags(HashMap<String, Boolean> sortParams, int max, Set<String> tagNames, int offset) {
        LOGGER.info("Entering GiftCertificateService.getCertificatesByTags()");

        Set<Tag> tags = tagService.getTagsByNames(tagNames);
        List<GiftCertificate> foundGiftCertificates = giftCertificateRepository.getGiftCertificatesByTags(sortParams, max, tags, offset);

        LOGGER.info("Exiting GiftCertificateService.getCertificatesByTags()");
        return foundGiftCertificates;
    }

    @Override
    public boolean delete(Long id) {
        LOGGER.info("Entering GiftCertificateService.delete()");

        boolean giftCertificateIsDeleted = giftCertificateRepository.deleteGiftCertificate(id);

        LOGGER.info("Exiting GiftCertificateService.delete()");
        return giftCertificateIsDeleted;
    }


    @Override
    public boolean update(GiftCertificate giftCertificate, Long giftCertificateId) throws NoSuchElementException {
        LOGGER.info("Entering GiftCertificateService.update()");
        boolean giftCertificateIsUpdate;

        GiftCertificate existingGiftCertificate = getById(giftCertificateId)
                .orElseThrow(() -> new NoSuchElementException("No Gift Certificate with id [" + giftCertificateId + "] exists"));
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        Optional<GiftCertificate> updatedGiftCertificated = giftCertificateRepository.updateGiftCertificate(giftCertificate, existingGiftCertificate);
        giftCertificateIsUpdate = updatedGiftCertificated.isPresent();

        LOGGER.info("Exiting GiftCertificateService.update()");
        return giftCertificateIsUpdate;
    }


    @Override
    public Optional<GiftCertificate> create(GiftCertificate giftCertificate) {
        try {
            LOGGER.info("Entering GiftCertificateService.create()");
            giftCertificate.setCreateDate(LocalDateTime.now());
            giftCertificate.setLastUpdateDate(LocalDateTime.now());
            Long createdGiftCertificateId = giftCertificateRepository.createGiftCertificate(giftCertificate);

            LOGGER.info("Exiting GiftCertificateService.create()");
            return getById(createdGiftCertificateId);
        } catch (ConstraintViolationException e) {
            LOGGER.error("ConstraintViolationException in GiftCertificateService.create()\n" +
                    e.getMessage());
            throw new ConstraintViolationException("Gift Certificate with name [" + giftCertificate.getName() + "] already exists", new SQLException(), "gift certificate name");
        }

    }

}
