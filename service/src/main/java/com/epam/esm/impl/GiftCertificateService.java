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
        LOGGER.debug("Entering GiftCertificateService.getById()");

        Optional<GiftCertificate> foundGiftCertificate = giftCertificateRepository.findById(id);

        LOGGER.debug("Exiting GiftCertificateService.getById()");
        return foundGiftCertificate;
    }

    public Optional<GiftCertificate> getGiftCertificateByName(String giftCertificateName) {
        LOGGER.debug("Entering GiftCertificateService.getGiftCertificateByName()");

        Optional<GiftCertificate> foundGiftCertificate = giftCertificateRepository.findByName(giftCertificateName);

        LOGGER.debug("Exiting GiftCertificateService.getGiftCertificateByName()");
        return foundGiftCertificate;

    }


//    @Override
//    public List<GiftCertificate> getAll(HashMap<String, Boolean> sortParams, int max, int offset) {
//        LOGGER.debug("Entering GiftCertificateService.getAll()");
//        List<GiftCertificate> foundGiftCertificates = giftCertificateRepository.getGiftCertificates(sortParams, max, offset);
//
//        LOGGER.debug("Exiting GiftCertificateService.getAll()");
//        return foundGiftCertificates;
//    }

    @Override
    public Page<GiftCertificate> getAll(Sort sortParams, int max, int offset) {
        LOGGER.debug("Entering GiftCertificateService.getAll()");
//        List<GiftCertificate> foundGiftCertificates = giftCertificateRepository.getGiftCertificates(sortParams, max, offset);

        Page<GiftCertificate> foundGiftCertificate = giftCertificateRepository.findAll(PageRequest.of(offset, max, sortParams));

        LOGGER.debug("Exiting GiftCertificateService.getAll()");
//        return foundGiftCertificates;
        return foundGiftCertificate;
    }


    public Page<GiftCertificate> getCertificatesByTags(Sort sortParams, int max, Set<String> tagNames, int offset) {
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

//        giftCertificateId = giftCertificateRepository.findById(giftCertificateId).isPresent();

        giftCertificateIsDeleted = giftCertificateRepository.findById(giftCertificateId).isPresent();

//        LOGGER.debug("Exiting GiftCertificateService.delete()");
        return giftCertificateIsDeleted;
    }


    //TODO -- test on update with existing/new tags
    @Override
    public void update(GiftCertificate changedGiftCertificate, Long giftCertificateId) throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.update()");

        GiftCertificate existingGiftCertificate = getById(giftCertificateId)
                .orElseThrow(() -> new NoSuchElementException("No Gift Certificate with id [" + giftCertificateId + "] exists"));

        changedGiftCertificate.setLastUpdateDate(LocalDateTime.now());
        existingGiftCertificate.mergeTwoGiftCertificate(changedGiftCertificate);
        giftCertificateRepository.save(existingGiftCertificate);

        LOGGER.debug("Exiting GiftCertificateService.update()");
    }

    //TODO -- test on create with existing/new tags
    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {
        try {
            LOGGER.debug("Entering GiftCertificateService.create()");
            giftCertificate.setCreateDate(LocalDateTime.now());
            giftCertificate.setLastUpdateDate(LocalDateTime.now());
            GiftCertificate createdGiftCertificate = giftCertificateRepository.save(giftCertificate);

            LOGGER.debug("Exiting GiftCertificateService.create()");
            return createdGiftCertificate;
        } catch (ConstraintViolationException e) {
            LOGGER.error("ConstraintViolationException in GiftCertificateService.create()\n" +
                    e.getMessage());
            throw new ConstraintViolationException("Gift Certificate with name [" + giftCertificate.getName() + "] already exists", new SQLException(), "gift certificate name");
        }

    }

}
