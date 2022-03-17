package com.epam.esm.impl;

import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testTagById_idDoesNotExist() {

        when(tagRepository.getTag(tagId)).thenReturn(Optional.empty());

        Optional<Tag> returnTag = tagService.getById(tagId);

        verify(tagRepository).getTag(tagId);
        assertFalse(returnTag.isPresent());
        assertEquals(Optional.empty(), returnTag);

    }

    @Test
    void testGetTagByName_nameExists() {
        Tag tag = new Tag(tagName);
        when(tagRepository.getTagByName(tagName)).thenReturn(Optional.of(tag));

        Optional<Tag> returnTag = tagService.getTagByName(tagName);

        verify(tagRepository).getTagByName(tagName);
        assertTrue(returnTag.isPresent());
        assertEquals(tag, returnTag.get());
    }

    @Test
    void testGetTagByName_nameDoesNotExists() {
        when(tagRepository.getTagByName(tagName)).thenReturn(Optional.empty());

        Optional<Tag> returnTag = tagService.getTagByName(tagName);

        verify(tagRepository).getTagByName(tagName);
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
    void testCreate_nameAlreadyExists() {
        Tag tag = new Tag();
        when(tagRepository.createTag(tag)).thenThrow(ConstraintViolationException.class);

        Exception constraintViolationException = assertThrows(ConstraintViolationException.class, () -> tagService.create(tag));
        String expectedMessage = "Tag with name [" + tag.getName() + "] already exists";
        String actualMessage = constraintViolationException.getMessage();

        verify(tagRepository).createTag(tag);
        verify(tagRepository, never()).getTag(tagId);
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void testDelete_idExists() {
        when(tagRepository.delete(tagId)).thenReturn(true);

        boolean result = tagService.delete(tagId);

        verify(tagRepository).delete(tagId);
        assertTrue(result);
    }

    @Test
    void testDelete_idDoesNotExist() {
        when(tagRepository.delete(tagId)).thenReturn(false);

        boolean result = tagService.delete(tagId);

        verify(tagRepository).delete(tagId);
        assertFalse(result);
    }


    @Test
    void testGetMostUsedTagForRichestUser() {
        Tag tag = new Tag(tagId, tagName);
        when(tagRepository.getMostUsedTagForRichestUser()).thenReturn(Optional.of(tag));
        Optional<Tag> mostUsedTagForRichestUser = tagService.getMostUsedTagForRichestUser();

        verify(tagRepository).getMostUsedTagForRichestUser();
        assertTrue(mostUsedTagForRichestUser.isPresent());
    }

    @Test
    void testUpdate_notSupportedException() {
        Tag tag = new Tag(tagId, tagName);
        assertThrows(UnsupportedOperationException.class, () -> tagService.update(tag, tagId));
    }
}