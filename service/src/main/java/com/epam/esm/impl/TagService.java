package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TagService implements CRUDService<Tag> {


    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Optional<Tag> getById(Long id) {
        return tagRepository.getTag(id);
    }

    @Override
    public List<Tag> getAll(String order, int max) {

        return tagRepository.getTags(order, max);
    }

    @Transactional
    @Override
    public Optional<Tag> create(Tag element) {
        Long createdTagId = tagRepository.create(element);
        return getById(createdTagId);
    }

    @Override
    public boolean delete(Long id) {
        return tagRepository.delete(id);
    }

    @Override
    public boolean update(Tag element, Long id) {
        throw new UnsupportedOperationException();
    }


//    public List<Tag> getTagsForCertificate(Long id) {
//        return tagRepository.getTagsForCertificate(id);
//    }

    public Optional<Tag> getMostUsedTag(){
        return tagRepository.getMostUsedTag();
    }

}
