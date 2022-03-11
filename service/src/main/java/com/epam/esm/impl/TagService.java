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

//    private final UserService userService;



    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Optional<Tag> getById(Long id) {
        return tagRepository.getTag(id);
    }

    @Override
    public List<Tag> getAll(String order, int max, int offset) {

        return tagRepository.getTags(order, max, offset);
    }

    @Transactional
    @Override
    public Optional<Tag> create(Tag tag) {
        Optional<Tag> createdTag = Optional.empty();
        Optional<Long> createdTagId = Optional.ofNullable(tagRepository.createTag(tag));
        if (createdTagId.isPresent()) {
            createdTag = getById(createdTagId.get());
        }
        return createdTag;
    }

    @Override
    public boolean delete(Long id) {
        return tagRepository.delete(id);
    }

    @Override
    public boolean update(Tag element, Long id) {
        throw new UnsupportedOperationException();
    }


    public void updateTag(Tag tag) {
         tagRepository.update(tag);
    }

    public void createTagGiftCertificateRelation(long giftCertificateId, long tagId) {
        tagRepository.createTagGiftCertificateRelation(giftCertificateId,tagId);
    }


//    public List<Tag> getTagsForCertificate(Long id) {
//        return tagRepository.getTagsForCertificate(id);
//    }

//    public Optional<Tag> getMostUsedTag(){
//        User user = userService.getUserWithBiggest
//        return tagRepository.getMostUsedTag();
//    }

}
