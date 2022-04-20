package com.epam.esm.impl;

import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import com.epam.esm.mapper.RequestParamsMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    //mock
    private final TagRepository tagRepository = Mockito.mock(TagRepository.class, withSettings().verboseLogging());

    private final RequestParamsMapper requestParamsMapper = Mockito.mock(RequestParamsMapper.class, withSettings().verboseLogging());

    // class under test
    private final TagService tagService = new TagService(tagRepository, requestParamsMapper);


    //params
    private final long tagId = 1L;
    private final String tagName = "tag name";

    Tag tag = new Tag(tagId, tagName);

    private final Tag firstTag = new Tag(1L, "first tag");
    private final Tag secondTag = new Tag(2L, "second tag");
    private final Tag thirdTag = new Tag(3L, "third tag");


    private final String firstTagName = "first tag";
    private final String secondTagName = "second tag";
    private final String thirdTagName = "third tag";

    private List<Tag> tagList = Arrays.asList(
            firstTag,
            secondTag,
            thirdTag
    );

    private Set<String> tagNamesSet;

    private final Set<Tag> tags = new HashSet<>(Arrays.asList(
            firstTag,
            secondTag,
            thirdTag));

    private List<Tag> noTags;


    private final int max = 20;
    private final int offset = 0;
    private final Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
    private final Sort sort = Sort.by(order);

    private final List<String> sortParams = new ArrayList<>();

    Page<Tag> tagsPage = new PageImpl<>(tagList);
    Page<Tag> noTagsPage = new PageImpl<>(new ArrayList<>());

    @BeforeEach
    void setUp() {
        sortParams.add("id.asc");

        tagNamesSet = new HashSet<>(Arrays.asList(
                firstTagName,
                secondTagName,
                thirdTagName
        ));


        noTags = new ArrayList<>();

    }

    @AfterEach
    void tearDown() {
        tagList = new ArrayList<>();
        tagNamesSet.clear();
        tags.clear();
        sortParams.clear();
    }


    @Test
    void testGetTagById_idExists() {
        Tag tag = new Tag(tagId, tagName);
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        Tag returnTag = tagService.findById(tagId);

        verify(tagRepository).findById(tagId);
        assertEquals(tag, returnTag);
    }

    @Test
    void testGetTagById_idDoesNotExist() {
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> tagService.findById(tagId));
        String expectedMessage = String.format("Tag with tagId [%s] not found", tagId);
        String actualMessage = noSuchElementException.getMessage();

        verify(tagRepository).findById(tagId);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetTagByName_nameExists() {
        Tag tag = new Tag(tagName);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));

        Tag returnTag = tagService.findTagByName(tagName);

        verify(tagRepository).findByName(tagName);
        assertEquals(tag, returnTag);
    }

    @Test
    void testGetTagByName_nameDoesNotExists() {
        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());

        Exception noSuchElementException = assertThrows(NoSuchElementException.class,
                () -> tagService.findTagByName(tagName));
        String expectedMessage = String.format("Tag with name [%s] does not exist", tagName);
        String actualMessage = noSuchElementException.getMessage();

        verify(tagRepository).findByName(tagName);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetAll_tagsExist() {
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(tagRepository.findAll(PageRequest.of(offset, max, sort))).thenReturn(tagsPage);

        Page<Tag> returnTags = tagService.findTags(sortParams, max, offset);

        verify(requestParamsMapper).mapParams(sortParams);
        verify(tagRepository).findAll(PageRequest.of(offset, max, sort));
        assertEquals(tagsPage, returnTags);
    }

    @Test
    void testGetAll_noTagsExist() {
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(tagRepository.findAll(PageRequest.of(offset, max, sort))).thenReturn(noTagsPage);

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> tagService.findTags(sortParams, max, offset));
        String expectedMessage = "No Tags exist";
        String actualMessage = noSuchElementException.getMessage();

        verify(requestParamsMapper).mapParams(sortParams);
        verify(tagRepository).findAll(PageRequest.of(offset, max, sort));
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testCreate_withName() {
        Tag tag = new Tag(tagName);
        Tag createdTag = new Tag(tagId, tagName);
        when(tagRepository.save(tag)).thenReturn(createdTag);

        Tag returnTag = tagService.create(tag);

        verify(tagRepository).save(tag);
        assertEquals(createdTag, returnTag);
    }

    @Test
    void testCreate_nameAlreadyExists() {
        Tag tag = new Tag(tagName);
        when(tagRepository.existsByName(tagName)).thenReturn(true);

        Exception constraintViolationException = assertThrows(ConstraintViolationException.class,
                () -> tagService.create(tag));
        String expectedMessage = String.format("Tag with name [%s] already exists", tagName);
        String actualMessage = constraintViolationException.getMessage();

        verify(tagRepository).existsByName(tagName);
        verify(tagRepository, never()).save(tag);
        assertEquals(expectedMessage, actualMessage);

    }

    @Test
    void testDelete_idExists() {
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        boolean tagWasPresentAndIsDeleted = tagService.delete(tagId);

        verify(tagRepository).findById(tagId);
        verify(tagRepository).delete(tag);
        assertTrue(tagWasPresentAndIsDeleted);
    }

    @Test
    void testDelete_idDoesNotExist() {
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        boolean tagWasPresentAndIsDeleted = tagService.delete(tagId);

        verify(tagRepository).findById(tagId);
        assertFalse(tagWasPresentAndIsDeleted);
    }


    @Test
    void testGetMostUsedTagForRichestUser_tagsExist() {
        long richestUserId = 1;
        Tag tag = new Tag(tagId, tagName);
        when(tagRepository.getRichestUserId()).thenReturn(richestUserId);
        when(tagRepository.getMostUsedTagForRichestUser(richestUserId)).thenReturn(Optional.of(tag));

        Tag mostUsedTagForRichestUser = tagService.getMostUsedTagForRichestUser();

        verify(tagRepository).getRichestUserId();
        verify(tagRepository).getMostUsedTagForRichestUser(richestUserId);
        assertTrue(nonNull(mostUsedTagForRichestUser));
    }

    @Test
    void testGetMostUsedTagForRichestUser_noTagsExist() {
        long richestUserId = 1;
        when(tagRepository.getRichestUserId()).thenReturn(richestUserId);
        when(tagRepository.getMostUsedTagForRichestUser(richestUserId)).thenReturn(Optional.empty());

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, tagService::getMostUsedTagForRichestUser);
        String expectedMessage = "No certificates with tags exist in orders";
        String actualMessage = noSuchElementException.getMessage();

        verify(tagRepository).getRichestUserId();
        verify(tagRepository).getMostUsedTagForRichestUser(richestUserId);
        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    void testGetTagsByName_notEmptySet() {
        when(tagRepository.findByName(firstTagName)).thenReturn(Optional.of(firstTag));
        when(tagRepository.findByName(secondTagName)).thenReturn(Optional.of(secondTag));
        when(tagRepository.findByName(thirdTagName)).thenReturn(Optional.of(thirdTag));

        Set<Tag> returnTags = tagService.getTagsByNames(tagNamesSet);

        assertTrue(returnTags.containsAll(tags));

    }
}
