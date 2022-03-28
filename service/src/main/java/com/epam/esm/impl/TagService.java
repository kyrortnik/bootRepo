package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.SQLException;
import java.util.*;

@Service
public class TagService implements CRUDService<Tag> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Optional<Tag> getById(Long id) {
        LOGGER.info("Entering tagService.getById()");

        Optional<Tag> foundTag = tagRepository.getTagById(id);

        LOGGER.info("Exiting tagService.getById()");
        return foundTag;
    }


    @Override
    public List<Tag> getAll(HashMap<String, Boolean> sortParams, int max, int offset) {
        LOGGER.info("Entering tagService.getAll()");

        List<Tag> foundTags = tagRepository.getTags(sortParams, max, offset);

        LOGGER.info("Exiting tagService.getAll()");
        return foundTags;
    }

    @Transactional
    @Override
    public Optional<Tag> create(Tag tag) {
        try {
            LOGGER.info("Entering TagService.create()");
            Optional<Tag> createdTag;

            Long createdTagId = tagRepository.createTag(tag);

            createdTag = getById(createdTagId);
            LOGGER.info("Exiting TagService.create()");
            return createdTag;
        } catch (ConstraintViolationException e) {
            LOGGER.error("ConstraintViolationException in TagService.create()\n" +
                    e.getMessage());
            throw new ConstraintViolationException("Tag with name [" + tag.getName() + "] already exists", new SQLException(), "tag name");
        }
    }

    @Override
    public boolean delete(Long id) {
        LOGGER.info("Entering TagService.delete()");

        boolean tagIsDeleted = tagRepository.delete(id);

        LOGGER.info("Exiting TagService.delete()");
        return tagIsDeleted;
    }

    @Override
    public boolean update(Tag element, Long id) {
        LOGGER.info("Entering TagService.update()");
        LOGGER.error("UnsupportedOperationException in TagService.update()");
        throw new UnsupportedOperationException();
    }

    public Optional<Tag> getTagByName(String tagName) {
        LOGGER.info("Entering TagService.getTagByName()");

        Optional<Tag> foundTag = tagRepository.getTagByName(tagName);

        LOGGER.info("Exiting TagService.getTagByName()");
        return foundTag;
    }

    public Optional<Tag> getMostUsedTagForRichestUser() {
        try {
            LOGGER.info("Entering TagService.getMostUsedTagForRichestUser()");

            Optional<Tag> tag = tagRepository.getMostUsedTagForRichestUser();

            LOGGER.info("Exiting TagService.getMostUsedTagForRichestUser()");
            return tag;
        } catch (NoResultException e) {
            LOGGER.error("NoResultException in TagService.getMostUsedTagForRichestUser()\n"
                    + e.getMessage());
            throw new NoResultException("No tags exist yet.");
        }

    }


    public Set<Tag> getTagsByNames(Set<String> tagNames) {
        LOGGER.info("Entering TagService.getTagsByNames()");
        Set<Tag> tags = new HashSet<>();
        if (!tagNames.isEmpty()) {
            tagNames.forEach(tagName -> getTagByName(tagName).ifPresent(tags::add));
        }
        LOGGER.info("Exiting TagService.getTagsByNames()");
        return tags;
    }
}
