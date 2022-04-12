package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.GiftCertificate;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import com.epam.esm.mapper.RequestParamsMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.SQLException;
import java.util.*;

@Transactional
@Service
public class TagService /*implements CRUDService<Tag>*/ {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private final TagRepository tagRepository;

    private final RequestParamsMapper requestParamsMapper;


    @Autowired
    public TagService(TagRepository tagRepository, RequestParamsMapper requestParamsMapper) {
        this.tagRepository = tagRepository;
        this.requestParamsMapper = requestParamsMapper;
    }

    public Tag findById(Long tagId) {
        LOGGER.debug("Entering tagService.getById");

        Tag foundTag = tagRepository.findById(tagId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Tag with tagId [%s] not found", tagId)));

        LOGGER.debug("Exiting tagService.getById");
        return foundTag;
    }


    public Page<Tag> getAll(List<String> sortBy, int max, int offset) {
        LOGGER.debug("Entering tagService.getAll");

        Sort sortParams = requestParamsMapper.mapParams(sortBy);
        Page<Tag> foundTags = tagRepository.findAll(PageRequest.of(offset, max, sortParams));
        if (foundTags.isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in TagController.getTags()\n" +
                    "No Tags exist");
            throw new NoSuchElementException("No Tags exist");
        }

        LOGGER.debug("Exiting tagService.getAll");
        return foundTags;
    }

    public Tag create(Tag tag) {
        LOGGER.debug("Entering TagService.create");

        String tagName = tag.getName();
        if (findTagByName(tagName).getName().equals(tag.getName())) {
            throw new ConstraintViolationException(String.format("Tag with name [%s] already exists", tagName),
                    new SQLException(), "gift certificate name");
        }
        Tag createdTag = tagRepository.save(tag);

        LOGGER.debug("Exiting TagService.create");
        return createdTag;
    }

    //TODO -- check that works without try/catch
    public boolean delete(Long id) {
        LOGGER.debug("Entering TagService.delete()");
        boolean tagIsDeleted;
//        try {
        Optional<Tag> tagToDelete = tagRepository.findById(id);
        if (tagToDelete.isPresent()) {
            tagRepository.delete(tagToDelete.get());
            for (GiftCertificate giftCertificate : tagToDelete.get().getCertificates()) {
                giftCertificate.getTags().remove(tagToDelete.get());
            }
            tagIsDeleted = true;
        } else {
            tagIsDeleted = false;
        }
        LOGGER.debug("Exiting TagService.delete()");
        return tagIsDeleted;
//        } catch (EmptyResultDataAccessException e) {
//            return false;
//        }

    }

    public void update(Tag tag, Long id) {
        LOGGER.debug("Entering TagService.update()");
        LOGGER.error("UnsupportedOperationException in TagService.update()");
        throw new UnsupportedOperationException();
    }

    public Tag findTagByName(String tagName) {
        LOGGER.debug("Entering TagService.getTagByName()");

        Tag foundTag = tagRepository.findByName(tagName).orElseThrow(() -> new NoSuchElementException(String
                .format("Tag with name [%s] does not exist", tagName)));

        LOGGER.debug("Exiting TagService.getTagByName()");
        return foundTag;
    }

    //TODO -- fix
    public Tag getMostUsedTagForRichestUser() {
        try {
            LOGGER.debug("Entering TagService.getMostUsedTagForRichestUser()");

            long richestUserId = tagRepository.getRichestUserId();
            Tag tag = tagRepository.getMostUsedTagForRichestUser(richestUserId)
                    .orElseThrow(() -> new NoSuchElementException("No certificates with tags exist in orders"));

            LOGGER.debug("Exiting TagService.getMostUsedTagForRichestUser()");
            return tag;
        } catch (NoResultException e) {
            LOGGER.error("NoResultException in TagService.getMostUsedTagForRichestUser()\n"
                    + e.getMessage());
            throw new NoResultException("No tags exist yet.");
        }

    }


    public Set<Tag> getTagsByNames(Set<String> tagNames) {
        LOGGER.debug("Entering TagService.getTagsByNames");
        Set<Tag> tags = new HashSet<>();
        if (!tagNames.isEmpty()) {
            tagNames.forEach(tagName -> tags.add(findTagByName(tagName)));
        }
        LOGGER.debug("Exiting TagService.getTagsByNames");
        return tags;
    }

    public Set<Tag> getTagsForCertificate(Long giftCertificateId) {
        LOGGER.debug("Entering TagService.getTagsForCertificate");

        Set<Tag> tagsForGiftCertificate = tagRepository.findByCertificatesId(giftCertificateId);

        LOGGER.debug("Exiting TagService.getTagsForCertificate");
        return tagsForGiftCertificate;
    }

    //TODO -- do I need this?
    public boolean tagExists(Tag tag) {
        LOGGER.debug("start ");

        boolean tagExists;
        Example<Tag> tagExample = Example.of(tag);

        tagExists = tagRepository.exists(tagExample);

        LOGGER.debug("exit");
        return tagExists;


    }

    public Tag getById(Long tagId) {
        LOGGER.debug("start");

        Tag proxyTag = tagRepository.getById(tagId);
        LOGGER.debug("end");
        return proxyTag;
    }

    //TODO -- tests that works
    public boolean tagExists(String tagName) {
        LOGGER.debug("start ");

        boolean tagExists = tagRepository.existsByName(tagName);

        LOGGER.debug("finish ");
        return tagExists;
    }
}
