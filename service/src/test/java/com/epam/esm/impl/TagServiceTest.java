package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.*;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

//TODO tests with giftCertificates not empty
class TagServiceTest {

    //mock
    private final TagRepository tagRepository = Mockito.mock(TagRepository.class, withSettings().verboseLogging());

    // class under test
    private final TagService tagService = new TagService(tagRepository);


    //params
    private final long tagId = 1L;
    private final String tagName = "tag name";

    private final String order = "ASC";
    private final int max = 20;
    private final int offset = 0;

    private final long giftCertificateId = 1L;

    private final Set<GiftCertificate> giftCertificates = new HashSet<GiftCertificate>(Arrays.asList(
            new GiftCertificate(),
            new GiftCertificate()
    ));

    private final List<Tag> tags = Arrays.asList(
            new Tag(1L, "first tag"),
            new Tag(2L, "second tag"),
            new Tag(3L, "third tag")
    );

    private final List<Tag> noTags = new ArrayList<>();


    @Test
    void testGetById_idExists() {
        Tag tag = new Tag(tagId, tagName);

        when(tagRepository.getTag(tagId)).thenReturn(Optional.of(tag));

        Optional<Tag> returnTag = tagService.getById(tagId);

        verify(tagRepository).getTag(tagId);
        assertTrue(returnTag.isPresent());
        assertEquals(tag, returnTag.get());
    }

    @Test
    void testFetById_idDoesNotExist() {

        when(tagRepository.getTag(tagId)).thenReturn(Optional.empty());

        Optional<Tag> returnTag = tagService.getById(tagId);

        verify(tagRepository).getTag(tagId);
        assertFalse(returnTag.isPresent());
        assertEquals(Optional.empty(), returnTag);

    }

    @Test
    void testGetAll_tagsExist() {
        when(tagRepository.getTags(order, max, offset)).thenReturn(tags);

        List<Tag> returnTags = tagService.getAll(order, max, offset);

        verify(tagRepository).getTags(order, max, offset);
        assertEquals(tags, returnTags);

    }

    @Test
    void testGetAll_noTagsExist() {
        when(tagRepository.getTags(order, max, offset)).thenReturn(noTags);

        List<Tag> returnTags = tagService.getAll(order, max, offset);

        verify(tagRepository).getTags(order, max, offset);
        assertEquals(noTags, returnTags);
    }

    @Test
    void testCreate_withName() {
        Tag tag = new Tag(tagName);
        Tag createdTag = new Tag(tagId, tagName);
        when(tagRepository.createTag(tag)).thenReturn(tagId);
        when(tagRepository.getTag(tagId)).thenReturn(Optional.of(createdTag));

        Optional<Tag> returnTag = tagService.create(tag);

        verify(tagRepository).createTag(tag);
        verify(tagRepository).getTag(tagId);
        assertTrue(returnTag.isPresent());
        assertEquals(createdTag, returnTag.get());
    }

    @Test
    void testCreate_withoutName() {

    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }

    @Test
    void updateTag() {
    }

    @Test
    void getTagByName() {
    }

    @Test
    void getMostUsedTagForRichestUser() {
    }
}