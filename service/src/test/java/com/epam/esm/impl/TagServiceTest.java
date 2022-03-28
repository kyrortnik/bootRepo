package com.epam.esm.impl;

import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.persistence.NoResultException;
import java.util.*;

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

    private Tag firstTag;
    private Tag secondTag;
    private Tag thirdTag;

    private final String firstTagName = "first tag";
    private final String secondTagName = "second tag";
    private final String thirdTagName = "third tag";

    private List<Tag> tagList;

    private Set<String> tagNamesSet;

    private Set<Tag> tagsSet;

    private List<Tag> noTags;

    private HashMap<String, Boolean> sortParams;
    private final int max = 20;
    private final int offset = 0;

    @BeforeEach
    void setUp() {
        firstTag = new Tag(1L, "first tag");
        secondTag = new Tag(2L, "second tag");
        thirdTag = new Tag(3L, "third tag");

        tagList = Arrays.asList(
                firstTag,
                secondTag,
                thirdTag
        );

        tagNamesSet = new HashSet<>(Arrays.asList(
                firstTagName,
                secondTagName,
                thirdTagName
        ));

        tagsSet = new HashSet<>(Arrays.asList(
                new Tag(1L, "first tag"),
                new Tag(2L, "second tag"),
                new Tag(3L, "third tag")
        ));

        noTags = new ArrayList<>();
        sortParams = new HashMap<>();
        sortParams.put("name", true);
    }

    @AfterEach
    void tearDown() {
        firstTag = new Tag();
        secondTag = new Tag();
        thirdTag = new Tag();
        tagList = new ArrayList<>();
        tagNamesSet.clear();
        tagsSet.clear();
        sortParams.clear();
    }


    @Test
    void testGetTagById_idExists() {
        Tag tag = new Tag(tagId, tagName);

        when(tagRepository.getTagById(tagId)).thenReturn(Optional.of(tag));

        Optional<Tag> returnTag = tagService.getById(tagId);

        verify(tagRepository).getTagById(tagId);
        assertTrue(returnTag.isPresent());
        assertEquals(tag, returnTag.get());
    }

    @Test
    void testGetTagById_idDoesNotExist() {

        when(tagRepository.getTagById(tagId)).thenReturn(Optional.empty());

        Optional<Tag> returnTag = tagService.getById(tagId);

        verify(tagRepository).getTagById(tagId);
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
        when(tagRepository.getTags(sortParams, max, offset)).thenReturn(tagList);

        List<Tag> returnTags = tagService.getAll(sortParams, max, offset);

        verify(tagRepository).getTags(sortParams, max, offset);
        assertEquals(tagList, returnTags);

    }

    @Test
    void testGetAll_noTagsExist() {
        when(tagRepository.getTags(sortParams, max, offset)).thenReturn(noTags);

        List<Tag> returnTags = tagService.getAll(sortParams, max, offset);

        verify(tagRepository).getTags(sortParams, max, offset);
        assertEquals(noTags, returnTags);
    }

    @Test
    void testCreate_withName() {
        Tag tag = new Tag(tagName);
        Tag createdTag = new Tag(tagId, tagName);
        when(tagRepository.createTag(tag)).thenReturn(tagId);
        when(tagRepository.getTagById(tagId)).thenReturn(Optional.of(createdTag));

        Optional<Tag> returnTag = tagService.create(tag);

        verify(tagRepository).createTag(tag);
        verify(tagRepository).getTagById(tagId);
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
        verify(tagRepository, never()).getTagById(tagId);
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
    void testGetMostUsedTagForRichestUser_tagsExist() {
        Tag tag = new Tag(tagId, tagName);
        when(tagRepository.getMostUsedTagForRichestUser()).thenReturn(Optional.of(tag));
        Optional<Tag> mostUsedTagForRichestUser = tagService.getMostUsedTagForRichestUser();

        verify(tagRepository).getMostUsedTagForRichestUser();
        assertTrue(mostUsedTagForRichestUser.isPresent());
    }

    @Test
    void testGetMostUsedTagForRichestUser_noTagsExist() {
        when(tagRepository.getMostUsedTagForRichestUser()).thenThrow(NoResultException.class);

        Exception noResultException = assertThrows(NoResultException.class, tagService::getMostUsedTagForRichestUser);
        String expectedMessage = "No tags exist yet.";
        String actualMessage = noResultException.getMessage();

        verify(tagRepository).getMostUsedTagForRichestUser();
        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void testUpdate_notSupportedException() {
        Tag tag = new Tag(tagId, tagName);
        assertThrows(UnsupportedOperationException.class, () -> tagService.update(tag, tagId));
    }

    @Test
    void testGetTagsByName_notEmptySet() {
        when(tagRepository.getTagByName(firstTagName)).thenReturn(Optional.of(firstTag));
        when(tagRepository.getTagByName(secondTagName)).thenReturn(Optional.of(secondTag));
        when(tagRepository.getTagByName(thirdTagName)).thenReturn(Optional.of(thirdTag));

        Set<Tag> returnTags = tagService.getTagsByNames(tagNamesSet);

        assertTrue(returnTags.containsAll(tagsSet));

    }
}