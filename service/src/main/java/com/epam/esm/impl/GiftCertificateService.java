package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import com.epam.esm.mapper.RequestParamsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.util.Objects.isNull;

@Transactional
@Service
public class GiftCertificateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private static final String NAME_PROPERTY = "name";

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


    public GiftCertificate findGiftCertificateById(Long id) throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.getById()");

        GiftCertificate foundGiftCertificate = giftCertificateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Gift Certificate with id [%s] not found", id)));

        LOGGER.debug("Exiting GiftCertificateService.getById()");
        return foundGiftCertificate;
    }


    public GiftCertificate findGiftCertificateByName(String giftCertificateName) throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.getGiftCertificateByName()");

        GiftCertificate foundGiftCertificate = giftCertificateRepository.findByName(giftCertificateName)
                .orElseThrow(() -> new NoSuchElementException(String
                        .format("Gift Certificate with name [%s] does not exist", giftCertificateName)));

        LOGGER.debug("Exiting GiftCertificateService.getGiftCertificateByName()");
        return foundGiftCertificate;

    }

    public Page<GiftCertificate> getGiftCertificates(Set<String> tagNames, List<String> sortBy, int max, int page)
            throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.getGiftCertificates");

        Page<GiftCertificate> giftCertificates = isNull(tagNames)
                ? getAllGiftCertificates(sortBy, max, page)
                : getGiftCertificatesByTags(sortBy, max, page, tagNames);
        if (giftCertificates.getContent().isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in GiftCertificateController.getCertificates()\n" +
                    "No Satisfying Gift Certificates exists");
            throw new NoSuchElementException("No Satisfying Gift Certificates exist");
        }
        LOGGER.debug("Exiting GiftCertificateService.getGiftCertificates");
        return giftCertificates;
    }


    public Page<GiftCertificate> getAllGiftCertificates(List<String> sortBy, int max, int page)
            throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.getAll");

        Sort sortingParams = requestParamsMapper.mapParams(sortBy);
        Page<GiftCertificate> giftCertificates = giftCertificateRepository
                .findAll(PageRequest.of(page, max, sortingParams));

        if (giftCertificates.getContent().isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in GiftCertificateController.getCertificates()\n" +
                    "No Satisfying Gift Certificates exists");
            throw new NoSuchElementException("No Satisfying Gift Certificates exist");
        }

        LOGGER.debug("Exiting GiftCertificateService.getAll");
        return giftCertificates;
    }


    public Page<GiftCertificate>
    getGiftCertificatesByTags(List<String> sortBy, int max, int page, Set<String> tagNames) {
        LOGGER.debug("Entering GiftCertificateService.getCertificatesByTags()");

        Sort sortingParams = requestParamsMapper.mapParams(sortBy);
        Set<Tag> tags = tagService.getTagsByNames(tagNames);
        Page<GiftCertificate> foundGiftCertificates = giftCertificateRepository
                .findByTagsIn(tags, PageRequest.of(page, max, sortingParams));

        LOGGER.debug("Exiting GiftCertificateService.getCertificatesByTags()");
        return foundGiftCertificates;
    }

    public boolean delete(Long giftCertificateId) {
        LOGGER.debug("Entering GiftCertificateService.delete()");
        boolean giftCertificateIsDeleted;

        if (!giftCertificateRepository.findById(giftCertificateId).isPresent()) {
            giftCertificateIsDeleted = false;
        } else {
            giftCertificateRepository.deleteById(giftCertificateId);
            giftCertificateIsDeleted = true;
        }

        LOGGER.debug("Exiting GiftCertificateService.delete()");
        return giftCertificateIsDeleted;
    }


    public void update(GiftCertificate changedGiftCertificate, Long giftCertificateId) throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.update()");

        GiftCertificate existingGiftCertificate = findGiftCertificateById(giftCertificateId);
        changedGiftCertificate.setLastUpdateDate(LocalDateTime.now());
        Set<Tag> updatedTags = replaceExistingTagWithProxy(changedGiftCertificate.getTags());

        changedGiftCertificate.setTags(updatedTags);
        existingGiftCertificate.mergeTwoGiftCertificate(changedGiftCertificate);
        giftCertificateRepository.save(existingGiftCertificate);

        LOGGER.debug("Exiting GiftCertificateService.update()");
    }


    public GiftCertificate create(GiftCertificate giftCertificate) throws DuplicateKeyException {
        LOGGER.debug("Entering GiftCertificateService.create()");
        GiftCertificate createdGiftCertificate;

        if (!giftCertificateAlreadyExists(giftCertificate)) {
            giftCertificate.setCreateDate(LocalDateTime.now());
            giftCertificate.setLastUpdateDate(LocalDateTime.now());
            Set<Tag> updatedTags = replaceExistingTagWithProxy(giftCertificate.getTags());
            giftCertificate.setTags(updatedTags);
            createdGiftCertificate = giftCertificateRepository.save(giftCertificate);
        } else {
            throw new DuplicateKeyException(String
                    .format("Gift certificate with name %s already exists", giftCertificate.getName()));
        }

        LOGGER.debug("Exiting GiftCertificateService.create()");
        return createdGiftCertificate;
    }


    public Set<Tag> getCertificateTags(Long giftCertificateId) throws NoSuchElementException {
        LOGGER.debug("Entering GiftCertificateService.getCertificateTags");

        Set<Tag> giftCertificateTags = tagService.getTagsForCertificate(giftCertificateId);

        if (giftCertificateTags.isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in GiftCertificateService.getCertificateTags\n" +
                    "No order tags for this gift certificate");
            throw new NoSuchElementException("No tags in this gift certificate");
        }

        LOGGER.debug("Exiting GiftCertificateService.getCertificateTags");
        return giftCertificateTags;
    }


    public boolean giftCertificateAlreadyExists(GiftCertificate giftCertificate) {
        LOGGER.debug("Entering GiftCertificateService.orderAlreadyExists");

        ExampleMatcher customExampleMatcher = ExampleMatcher.matchingAll()
                .withMatcher(NAME_PROPERTY, ExampleMatcher.GenericPropertyMatchers.exact());


        Example<GiftCertificate> orderExample = Example.of(giftCertificate, customExampleMatcher);
        boolean orderAlreadyExists = giftCertificateRepository.exists(orderExample);

        LOGGER.debug("Exiting OrderService.orderAlreadyExists()");
        return orderAlreadyExists;
    }


    private Set<Tag> replaceExistingTagWithProxy(Set<Tag> tags) {
        LOGGER.debug("Entering GiftCertificateService.replaceExistingTagWithProxy");
        Set<Tag> updatedTags = new HashSet<>(tags);

        for (Tag tag : tags) {
            String tagName = tag.getName();
            if (tagService.tagAlreadyExists(tagName)) {
                updatedTags.remove(tag);
                Tag foundTag = tagService.findTagByName(tagName);
                Tag proxyTag = tagService.getById(foundTag.getId());
                updatedTags.add(proxyTag);
            }
        }

        LOGGER.debug("Exiting GiftCertificateService.replaceExistingTagWithProxy");
        return updatedTags;
    }

}
