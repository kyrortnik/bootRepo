package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class GiftCertificateService implements CRUDService<GiftCertificate> {

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

        return giftCertificateRepository.getCertificateById(id);
    }

    public Optional<GiftCertificate> getGiftCertificateByName(String giftCertificateName) {

        return giftCertificateRepository.getGiftCertificateByName(giftCertificateName);

    }


    @Override
    public List<GiftCertificate> getAll(HashMap<String, Boolean> sortParams, int max, int offset) {
        return giftCertificateRepository.getGiftCertificates(sortParams, max, offset);
    }


    public List<GiftCertificate> getCertificatesByTags(HashMap<String, Boolean> sortParams, int max, Set<String> tagNames, int offset) {
        Set<Tag> tags = tagService.getTagsByNames(tagNames);
        return giftCertificateRepository.getGiftCertificatesByTags(sortParams, max, tags, offset);
    }

    @Override
    public boolean delete(Long id) {
        return giftCertificateRepository.deleteGiftCertificate(id);
    }

//    @Override
//    public boolean update(GiftCertificate giftCertificate, Long giftCertificateId) throws NoSuchElementException {
//        try {
//            giftCertificate.setLastUpdateDate(LocalDateTime.now());
//            Optional<GiftCertificate> updatedGiftCertificated = giftCertificateRepository.updateGiftCertificate(giftCertificate, giftCertificateId);
//
//            return updatedGiftCertificated.isPresent();
//        } catch (NoSuchElementException e) {
//            throw new NoSuchElementException("Certificate with id [" + giftCertificateId + "] doesn't exist");
//        }
//
//
//    }

    @Override
    public boolean update(GiftCertificate giftCertificate, Long giftCertificateId) throws NoSuchElementException {
        try {
            GiftCertificate existingGiftCertificate = getById(giftCertificateId)
                            .orElseThrow(() -> new NoSuchElementException("No Gift Certificate with id [" + giftCertificateId + "] exists"));
            giftCertificate.setLastUpdateDate(LocalDateTime.now());
            Optional<GiftCertificate> updatedGiftCertificated = giftCertificateRepository.updateGiftCertificate(giftCertificate, existingGiftCertificate);

            return updatedGiftCertificated.isPresent();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Certificate with id [" + giftCertificateId + "] doesn't exist");
        }


    }


    @Override
    public Optional<GiftCertificate> create(GiftCertificate giftCertificate) {
        try {
            giftCertificate.setCreateDate(LocalDateTime.now());
            giftCertificate.setLastUpdateDate(LocalDateTime.now());

            Long createdGiftCertificateId = giftCertificateRepository.createGiftCertificate(giftCertificate);

            return getById(createdGiftCertificateId);
        } catch (ConstraintViolationException e) {
            throw new ConstraintViolationException("Gift Certificate with name [" + giftCertificate.getName() + "] already exists", new SQLException(), "tag name");
        }

    }

}
