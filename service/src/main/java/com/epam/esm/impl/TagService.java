package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.SQLException;
import java.util.*;

@Service
public class TagService implements CRUDService<Tag> {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Optional<Tag> getById(Long id) {
        return tagRepository.getTagById(id);
    }


    @Override
    public List<Tag> getAll(HashMap<String, Boolean> sortParams, int max, int offset) {
        return tagRepository.getTags(sortParams, max, offset);
    }

    @Transactional
    @Override
    public Optional<Tag> create(Tag tag) {
        try {
            Long createdTagId = tagRepository.createTag(tag);
            return getById(createdTagId);
        } catch (ConstraintViolationException e) {
            throw new ConstraintViolationException("Tag with name [" + tag.getName() + "] already exists", new SQLException(), "tag name");
        }
    }

    @Override
    public boolean delete(Long id) {
        return tagRepository.delete(id);
    }

    @Override
    public boolean update(Tag element, Long id) {
        throw new UnsupportedOperationException();
    }

    public Optional<Tag> getTagByName(String tagName) {
        return tagRepository.getTagByName(tagName);
    }

    public Optional<Tag> getMostUsedTagForRichestUser() {
        try {
            return tagRepository.getMostUsedTagForRichestUser();
        } catch (NoResultException e) {
            throw new NoResultException("No tags exist yet.");
        }

    }


    public Set<Tag> getTagsByNames(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (!tagNames.isEmpty()) {
            tagNames.forEach(tagName -> getTagByName(tagName).ifPresent(tags::add));
        }
        return tags;
    }
}
