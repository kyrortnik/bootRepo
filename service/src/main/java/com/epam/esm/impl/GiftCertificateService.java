package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import com.epam.esm.mapper.RequestParamsMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;

@Transactional
@Service
public class GiftCertificateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private final GiftCertificateRepository giftCertificateRepository;

    private final TagService tagService;

    private final RequestParamsMapper requestParamsMapper;

    @Autowired
    public GiftCertificateService(GiftCertificateRepository giftCertificateRepository, TagService tagService
            , RequestParamsMapper requestParamsMapper) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagService = tagService;
        this.requestParamsMapper = requestParamsMapper;
    }


    @Transactional
    public GiftCertificate findById(Long id) {
        LOGGER.debug("Entering GiftCertificateService.getById()");

        GiftCertificate foundGiftCertificate = giftCertificateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Certificate with id [%s] not found", id)));

        LOGGER.debug("Exiting GiftCertificateService.getById()");
        return foundGiftCertificate;
    }

    public GiftCertificate findGiftCertificateByName(String giftCertificateName) {
        LOGGER.debug("Entering GiftCertificateService.getGiftCertificateByName()");

        GiftCertificate foundGiftCertificate = giftCertificateRepository.findByName(giftCertificateName)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("Gift Certificate with name [%s] does not exist", giftCertificateName)));

        LOGGER.debug("Exiting GiftCertificateService.getGiftCertificateByName()");
        return foundGiftCertificate;

    }

    public Page<GiftCertificate> getGiftCertificates(Set<String> tagNames, List<String> sortBy, int max, int offset) {
        LOGGER.debug("Entering GiftCertificateService.getGiftCertificates");

        Sort sortingParams = requestParamsMapper.mapParams(sortBy);
        Page<GiftCertificate> giftCertificates = isNull(tagNames)
                ? getAll(sortingParams, max, offset)
                : getCertificatesByTags(sortingParams, max, offset, tagNames);
        if (giftCertificates.getContent().isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in GiftCertificateController.getCertificates()\n" +
                    "No Satisfying Gift Certificates exists");
            throw new NoSuchElementException("No Satisfying Gift Certificates exist");
        }
        LOGGER.debug("Exiting GiftCertificateService.getGiftCertificates");
        return giftCertificates;
    }


    public Page<GiftCertificate> getAll(Sort sortParams, int max, int offset) {
        LOGGER.debug("Entering GiftCertificateService.getAll");

        Page<GiftCertificate> foundGiftCertificates = giftCertificateRepository.findAll(PageRequest.of(offset, max, sortParams));

        LOGGER.debug("Exiting GiftCertificateService.getAll");
        return foundGiftCertificates;
    }

    //TODO -- fix return. Return duplicates, 'and' condition is not met
    public Page<GiftCertificate> getCertificatesByTags(Sort sortParams, int max, int offset, Set<String> tagNames) {
        LOGGER.debug("Entering GiftCertificateService.getCertificatesByTags()");

        Set<Tag> tags = tagService.getTagsByNames(tagNames);
        Page<GiftCertificate> foundGiftCertificates = giftCertificateRepository
                .findByTagsIn(tags, PageRequest.of(offset, max, sortParams));

        LOGGER.debug("Exiting GiftCertificateService.getCertificatesByTags()");
        return foundGiftCertificates;
    }

    public boolean delete(Long giftCertificateId) {
        LOGGER.debug("Entering GiftCertificateService.delete()");

        boolean giftCertificateIsDeleted;
        giftCertificateRepository.deleteById(giftCertificateId);
        giftCertificateIsDeleted = giftCertificateRepository.findById(giftCertificateId).isPresent();

        LOGGER.debug("Exiting GiftCertificateService.delete()");
        return giftCertificateIsDeleted;
    }


    public void update(GiftCertificate changedGiftCertificate, Long giftCertificateId) throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.update()");

        GiftCertificate existingGiftCertificate = findById(giftCertificateId);
        changedGiftCertificate.setLastUpdateDate(LocalDateTime.now());
        Set<Tag> updatedTags = replaceExistingTagWithProxy(changedGiftCertificate.getTags());

        changedGiftCertificate.setTags(updatedTags);
        existingGiftCertificate.mergeTwoGiftCertificate(changedGiftCertificate);
        giftCertificateRepository.save(existingGiftCertificate);

        LOGGER.debug("Exiting GiftCertificateService.update()");
    }


    //TODO -- no new tag created
    public GiftCertificate create(GiftCertificate giftCertificate) {
        LOGGER.debug("Entering GiftCertificateService.create()");
        String giftCertificateName = giftCertificate.getName();

        if (findGiftCertificateByName(giftCertificateName).getName().equals(giftCertificate.getName())) {
            throw new ConstraintViolationException(String
                    .format("Gift Certificate with name [%s] already exists", giftCertificateName),
                    new SQLException(), "gift certificate name");
        }
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        Set<Tag> updatedTags = replaceExistingTagWithProxy(giftCertificate.getTags());
        giftCertificate.setTags(updatedTags);
        GiftCertificate createdGiftCertificate = giftCertificateRepository.save(giftCertificate);

        LOGGER.debug("Exiting GiftCertificateService.create()");
        return createdGiftCertificate;
    }


    public Set<Tag> getCertificateTags(Long giftCertificateId) {
        LOGGER.debug("Entering GiftCertificateService.getCertificateTags");

        Set<Tag> giftCertificateTags = tagService.getTagsForCertificate(giftCertificateId);

        if (giftCertificateTags.isEmpty()){
            LOGGER.error("NoEntitiesFoundException in GiftCertificateService.getCertificateTags\n" +
                    "No order tags for this gift certificate");
            throw new NoSuchElementException("No order tags for this gift certificate");
        }

        LOGGER.debug("Exiting GiftCertificateService.getCertificateTags");
        return giftCertificateTags;
    }

    private Set<Tag> replaceExistingTagWithProxy(Set<Tag> tags) {
        LOGGER.debug("Entering GiftCertificateService.replaceExistingTagWithProxy");
        Set<Tag> updatedTags = new HashSet<>();

        for (Tag tag : tags) {
            String tagName = tag.getName();
            if (tagService.tagExists(tagName)) {
                Tag foundTag = tagService.findTagByName(tagName);
                updatedTags.remove(tag);
                Tag proxyTag = tagService.getById(foundTag.getId());
                updatedTags.add(proxyTag);
            }
        }

        LOGGER.debug("Exiting GiftCertificateService.replaceExistingTagWithProxy");
        return updatedTags;
    }

}
