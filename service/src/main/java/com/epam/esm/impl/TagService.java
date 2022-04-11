package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.GiftCertificate;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
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
public class TagService implements CRUDService<Tag> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateService.class);

    //    private final TagRepository tagRepository;
    private final TagRepository tagRepository;

//    @Autowired
//    public TagService(TagRepository tagRepository) {
//        this.tagRepository = tagRepository;
//    }

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Optional<Tag> findById(Long id) {
        LOGGER.debug("Entering tagService.getById()");

        Optional<Tag> foundTag = tagRepository.findById(id);

        LOGGER.debug("Exiting tagService.getById()");
        return foundTag;
    }


//    @Override
//    public List<Tag> getAll(HashMap<String, Boolean> sortParams, int max, int offset) {
//        LOGGER.debug("Entering tagService.getAll()");
//
//        PageRequest pageRequest = PageRequest.of(offset,max, Sort.by().and(Sort.by("id").ascending()));
//
//        List<Tag> foundTags = tagRepository.findAll(sortParams, max, offset);
//
//        LOGGER.debug("Exiting tagService.getAll()");
//        return foundTags;
//    }

    @Override
    public Page<Tag> getAll(Sort sortParams, int max, int offset) {
        LOGGER.debug("Entering tagService.getAll()");


        Page<Tag> foundTags = tagRepository.findAll(PageRequest.of(offset, max, sortParams));
        if (foundTags.isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in TagController.getTags()\n" +
                    "No Tags exist");
            throw new NoSuchElementException("No Tags exist");
        }

        LOGGER.debug("Exiting tagService.getAll()");
        return foundTags;
    }

    @Transactional
    @Override
    public Tag create(Tag tag) {
        try {
            LOGGER.debug("Entering TagService.create()");

            if ((tag.getName().isEmpty())) {
                LOGGER.error("NullPointerException in TagController.create()\n" +
                        "Tag name can not be empty");
                throw new NullPointerException("Tag name can not be empty");
            }
            Tag createdTag;


            createdTag = tagRepository.save(tag);


            LOGGER.debug("Exiting TagService.create()");
            return createdTag;
        } catch (ConstraintViolationException e) {
            LOGGER.error("ConstraintViolationException in TagService.create()\n" +
                    e.getMessage());
            throw new ConstraintViolationException("Tag with name [" + tag.getName() + "] already exists", new SQLException(), "tag name");
        }
    }

    @Override
    public boolean delete(Long id) {
        LOGGER.debug("Entering TagService.delete()");
        try {

            Optional<Tag> tagToDelete = tagRepository.findById(id);
            tagToDelete.ifPresent(tagRepository::delete);
            for (GiftCertificate giftCertificate : tagToDelete.get().getCertificates()) {
                giftCertificate.getTags().remove(tagToDelete.get());
            }

            LOGGER.debug("Exiting TagService.delete()");
            return !findById(id).isPresent();
        } catch (EmptyResultDataAccessException e) {
            return false;
        }


    }

    @Override
    public void update(Tag element, Long id) {
        LOGGER.debug("Entering TagService.update()");
        LOGGER.error("UnsupportedOperationException in TagService.update()");
        throw new UnsupportedOperationException();
    }

    public Optional<Tag> findTagByName(String tagName) {
        LOGGER.debug("Entering TagService.getTagByName()");

        Optional<Tag> foundTag = tagRepository.findByName(tagName);

        LOGGER.debug("Exiting TagService.getTagByName()");
        return foundTag;
    }

    //TODO -- fix
    public Optional<Tag> getMostUsedTagForRichestUser() {
        try {
            LOGGER.debug("Entering TagService.getMostUsedTagForRichestUser()");

            long richestUserId = tagRepository.getRichestUserId();

            Optional<Tag> tag = tagRepository.getMostUsedTagForRichestUser(richestUserId);

            LOGGER.debug("Exiting TagService.getMostUsedTagForRichestUser()");
            return tag;
        } catch (NoResultException e) {
            LOGGER.error("NoResultException in TagService.getMostUsedTagForRichestUser()\n"
                    + e.getMessage());
            throw new NoResultException("No tags exist yet.");
        }

    }


    public Set<Tag> getTagsByNames(Set<String> tagNames) {
        LOGGER.debug("Entering TagService.getTagsByNames()");
        Set<Tag> tags = new HashSet<>();
        if (!tagNames.isEmpty()) {
            tagNames.forEach(tagName -> findTagByName(tagName).ifPresent(tags::add));
        }
        LOGGER.debug("Exiting TagService.getTagsByNames()");
        return tags;
    }

    public Set<Tag> getTagsForCertificate(Long giftCertificateId) {

        return tagRepository.findByCertificatesId(giftCertificateId);
    }

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
}
