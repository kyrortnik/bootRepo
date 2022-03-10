package com.epam.esm.impl;

import com.epam.esm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
public class CertificateService implements CRUDService<GiftCertificate> {

//    @Autowired
//    private final GiftCertificateRepository giftCertificateRepository;

    private final GiftCertificateRepository giftCertificateRepository;


    private final TagService tagService;

    @Autowired
    public CertificateService(GiftCertificateRepository giftCertificateRepository, TagService tagService) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagService = tagService;
    }


    @Transactional
    @Override
    public Optional<GiftCertificate> getById(Long id) {
        //        Set<Tag> tags = new HashSet<>(tagService.getTagsForCertificate(id));
//        giftCertificate.ifPresent(certificate -> certificate.setTags(tags));
        return giftCertificateRepository.getCertificateById(id);
    }

    public Optional<GiftCertificate> getByName(String name) {
        Optional<GiftCertificate> giftCertificate = giftCertificateRepository.getCertificateByName(name);
        return giftCertificate;
    }


    @Override
    public List<GiftCertificate> getAll(String order, int max,int offset) {
        return giftCertificateRepository.getCertificates(order, max);
    }


    public List<GiftCertificate> getEntitiesWithParams(String order, int max, String tag, String pattern) {
//       String processedPattern = fromParamToMatchingPattern(pattern);

        String wrappedPattern = fromParamToMatchingPattern(pattern);
        //        for (GiftCertificate certificate : giftCertificates) {
//            Set<Tag> tags = new HashSet<>(tagService.getTagsForCertificate(certificate.getId()));
//            certificate.setTags(tags);
//        }
        return giftCertificateRepository.getCertificatesWithParams(order, max, tag, wrappedPattern);
    }


    @Override
    public boolean delete(Long id) {
        return giftCertificateRepository.delete(id);
    }

    @Override
    public boolean update(GiftCertificate giftCertificate, Long id) {
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        Optional<GiftCertificate> updatedGiftCertificated = giftCertificateRepository.update(giftCertificate, id);
        return updatedGiftCertificated.isPresent();

    }

    @Transactional
    @Override
    public Optional<GiftCertificate> create(GiftCertificate giftCertificate) {
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        Long createdGiftCertificateId = giftCertificateRepository.create(giftCertificate);
        return getById(createdGiftCertificateId);
    }

    private String fromParamToMatchingPattern(String pattern) {
        if (!isNull(pattern)){
            pattern = '%' + pattern + '%';
        }
        return pattern;
    }

}
