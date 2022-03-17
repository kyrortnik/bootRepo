package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public Optional<GiftCertificate> getByName(String name) {
        return giftCertificateRepository.getCertificateByName(name);

    }


    @Override
    public List<GiftCertificate> getAll(String order, int max, int offset) {
        return giftCertificateRepository.getCertificates(order, max, offset);
    }



    public List<GiftCertificate> getCertificatesByTags(String order, int max, Set<String> tagNames, int offset) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames){
            Optional<Tag> tag = tagService.getTagByName(tagName);
            tag.ifPresent(tags::add);
        }
        return giftCertificateRepository.getCertificatesByTags(order, max, tags, offset);
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


    @Override
    public Optional<GiftCertificate> create(GiftCertificate giftCertificate) {
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());

        Long createdGiftCertificateId = giftCertificateRepository.create(giftCertificate);

        return getById(createdGiftCertificateId);
    }

}
