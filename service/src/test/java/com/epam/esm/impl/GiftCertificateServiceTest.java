package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import com.epam.esm.mapper.RequestParamsMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GiftCertificateServiceTest {

    // mocks
    private final GiftCertificateRepository giftCertificateRepository = Mockito
            .mock(GiftCertificateRepository.class, withSettings().verboseLogging());

    private final TagService tagService = Mockito
            .mock(TagService.class, withSettings().verboseLogging());

    private final RequestParamsMapper requestParamsMapper = Mockito
            .mock(RequestParamsMapper.class, withSettings().verboseLogging());

    // class under test
    private final GiftCertificateService giftCertificateService =
            new GiftCertificateService(giftCertificateRepository, tagService, requestParamsMapper);

    //params

    private final long giftCertificateId = 1;
    private final long nonExistingGiftCertificateId = 11111;
    private final String giftCertificateName = "certificate name";
    private final String description = "certificate description";
    private final Long price = 100L;
    private final long duration = 120;
    private final LocalDateTime createDate = LocalDateTime.now();
    private final LocalDateTime lastUpdateDate = LocalDateTime.now();
    private final LocalDateTime dataChangedAfterUpdate = LocalDateTime.of(2022, 3, 25, 15, 55);

    private final int max = 20;
    private final int offset = 0;
    private final Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
    private final Sort sort = Sort.by(order);


    private final Tag firstTag = new Tag(1L, "first tag");
    private final Tag secondTag = new Tag(2L, "second tag");
    private final Tag thirdTag = new Tag(3L, "third tag");

    private final String firstTagName = "first tag";
    private final String secondTagName = "second tag";
    private final String thirdTagName = "third tag";
    private final String nonExistingGiftCertificateName = "non-existing name";

    private final Set<Tag> noTags = new HashSet<>();

    private final Set<Tag> tags = new HashSet<>(
            Arrays.asList(
                    firstTag,
                    secondTag,
                    thirdTag
            ));

    private final Set<String> noTagNames = new HashSet<>();

    private final Set<String> tagNames = new HashSet<>(
            Arrays.asList(
                    firstTagName,
                    secondTagName,
                    thirdTagName
            ));

    private GiftCertificate firstGiftCertificate;
    private GiftCertificate secondGiftCertificate;
    private GiftCertificate thirdGiftCertificate;
    private GiftCertificate firstGiftCertificateWithTags;
    private GiftCertificate secondGiftCertificateWithTags;
    private GiftCertificate thirdGiftCertificateWithTags;

    private GiftCertificate changedGiftCertificate;
    private GiftCertificate existingGiftCertificate;
    private GiftCertificate updatedGiftCertificate;
    private GiftCertificate nonExistingGiftCertificate;

    private final List<GiftCertificate> noGiftCertificates = new ArrayList<>();

    private final List<GiftCertificate> giftCertificates = Arrays.asList(
            firstGiftCertificate,
            secondGiftCertificate,
            thirdGiftCertificate
    );

    private final List<GiftCertificate> giftCertificatesWithTags = Arrays.asList(
            firstGiftCertificateWithTags,
            secondGiftCertificateWithTags,
            thirdGiftCertificateWithTags
    );

    Page<GiftCertificate> giftCertificatesPage = new PageImpl<>(giftCertificates);
    Page<GiftCertificate> giftCertificatesWithTagsPage = new PageImpl<>(giftCertificatesWithTags);
    Page<GiftCertificate> emptyGiftCertificatePage = new PageImpl<>(new ArrayList<>());
    private final List<String> sortParams = new ArrayList<>();


    @BeforeEach
    void setUp() {
        sortParams.add("id.asc");

        String changedName = "changedName";
        String changedDescription = "changedDescription";
        long changedPrice = 1500;
        long changedDuration = 300;


        firstGiftCertificate = new GiftCertificate.GiftCertificateBuilder("first certificate")
                .id(1L)
                .description("first description")
                .price(100L)
                .duration(120L)
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .build();

        secondGiftCertificate = new GiftCertificate.GiftCertificateBuilder("second certificate")
                .id(2L)
                .description("second description")
                .price(300L)
                .duration(30L)
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .build();

        thirdGiftCertificate = new GiftCertificate.GiftCertificateBuilder("third certificate")
                .id(3L)
                .description("third description")
                .price(500L)
                .duration(90L)
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .build();

        firstGiftCertificateWithTags = new GiftCertificate.GiftCertificateBuilder("first certificate")
                .id(1L)
                .description("first description")
                .price(100L)
                .duration(120L)
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .tags(tags)
                .build();

        secondGiftCertificateWithTags = new GiftCertificate.GiftCertificateBuilder("second certificate")
                .id(2L)
                .description("second description")
                .price(300L)
                .duration(30L)
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .tags(tags)
                .build();

        thirdGiftCertificateWithTags = new GiftCertificate.GiftCertificateBuilder("third certificate")
                .id(3L)
                .description("third description")
                .price(500L)
                .duration(90L)
                .createDate(LocalDateTime.now())
                .lastUpdateDate(LocalDateTime.now())
                .tags(tags)
                .build();

        changedGiftCertificate = new GiftCertificate.GiftCertificateBuilder(changedName)
                .description(changedDescription)
                .price(changedPrice)
                .duration(changedDuration)
                .tags(tags)
                .build();

        existingGiftCertificate = new GiftCertificate.GiftCertificateBuilder(giftCertificateName)
                .id(giftCertificateId)
                .description(description)
                .price(price)
                .duration(duration)
                .createDate(createDate)
                .lastUpdateDate(lastUpdateDate)
                .tags(tags)
                .build();

        updatedGiftCertificate = new GiftCertificate.GiftCertificateBuilder(changedName)
                .id(giftCertificateId)
                .description(changedDescription)
                .price(changedPrice)
                .duration(changedDuration)
                .createDate(createDate)
                .lastUpdateDate(dataChangedAfterUpdate)
                .tags(tags)
                .build();

        nonExistingGiftCertificate = new GiftCertificate.GiftCertificateBuilder(nonExistingGiftCertificateName)
                .id(nonExistingGiftCertificateId)
                .build();
    }

    @AfterEach
    void tearDown() {
        sortParams.clear();
        firstGiftCertificate = null;
        secondGiftCertificate = null;
        thirdGiftCertificate = null;
        firstGiftCertificateWithTags = null;
        secondGiftCertificateWithTags = null;
        thirdGiftCertificateWithTags = null;
        changedGiftCertificate = null;
        existingGiftCertificate = null;
        updatedGiftCertificate = null;
        nonExistingGiftCertificate = null;
    }

    @Test
    void testGetById_idExists() {
        when(giftCertificateRepository.findById(giftCertificateId)).thenReturn(Optional.of(existingGiftCertificate));

        GiftCertificate returnGiftCertificate = giftCertificateService.findById(giftCertificateId);

        verify(giftCertificateRepository).findById(giftCertificateId);
        assertTrue(nonNull(returnGiftCertificate));
        assertEquals(existingGiftCertificate, returnGiftCertificate);
    }

    @Test
    void testGetById_idDoesNotExist() {
        when(giftCertificateRepository.findById(nonExistingGiftCertificateId)).thenReturn(Optional.empty());

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> giftCertificateService
                .findById(nonExistingGiftCertificateId));
        String expectedMessage = String.format("Certificate with id [%s] not found", nonExistingGiftCertificateId);
        String actualMessage = noSuchElementException.getMessage();

        verify(giftCertificateRepository).findById(nonExistingGiftCertificateId);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void testGetGiftCertificateByName_nameExists() {
        when(giftCertificateRepository.findByName(giftCertificateName)).thenReturn(Optional.of(existingGiftCertificate));

        GiftCertificate returnGiftCertificate = giftCertificateService.findGiftCertificateByName(giftCertificateName);

        verify(giftCertificateRepository).findByName(giftCertificateName);
        assertTrue(nonNull(returnGiftCertificate));
        assertEquals(existingGiftCertificate, returnGiftCertificate);
    }

    @Test
    void testGetGiftCertificateByName_nameDoesNotExists() {
        when(giftCertificateRepository.findByName(nonExistingGiftCertificateName)).thenReturn(Optional.empty());

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> giftCertificateService
                .findGiftCertificateByName(nonExistingGiftCertificateName));
        String expectedMessage = String.format("Gift Certificate with name [%s] does not exist", nonExistingGiftCertificateName);
        String actualMessage = noSuchElementException.getMessage();

        verify(giftCertificateRepository).findByName(nonExistingGiftCertificateName);
        assertEquals(expectedMessage, actualMessage);

    }


    @Test
    void testGetAll_giftCertificatesExist() {
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(giftCertificateRepository.findAll(PageRequest.of(offset, max, sort))).thenReturn(giftCertificatesPage);

        Page<GiftCertificate> returnGiftCertificates = giftCertificateService.getAllGiftCertificates(sortParams, max, offset);

        verify(giftCertificateRepository).findAll(PageRequest.of(offset, max, sort));
        assertEquals(giftCertificatesPage, returnGiftCertificates);
    }

    @Test
    void testGetAll_noGiftCertificates() {
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(giftCertificateRepository.findAll(PageRequest.of(offset, max, sort))).thenReturn(emptyGiftCertificatePage);

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> giftCertificateService
                .getAllGiftCertificates(sortParams, max, offset));
        String expectedMessage = "No Satisfying Gift Certificates exist";
        String actualMessage = noSuchElementException.getMessage();

        verify(giftCertificateRepository).findAll(PageRequest.of(offset, max, sort));
        assertEquals(expectedMessage, actualMessage);

    }

    //TODO -- test manually
    @Test
    void testGetCertificatesByTags_giftCertificatesWithTagsExist() {
        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
        when(tagService.getTagsByNames(tagNames)).thenReturn(tags);
        when(giftCertificateRepository.findByTagsIn(tags, PageRequest.of(offset, max, sort)))
                .thenReturn(giftCertificatesWithTagsPage);

        Page<GiftCertificate> giftCertificateWithTags = giftCertificateService
                .getGiftCertificatesByTags(sortParams, max, offset, tagNames);

        verify(tagService).getTagsByNames(tagNames);
        verify(giftCertificateRepository).findByTagsIn(tags, PageRequest.of(offset, max, sort));
        assertEquals(giftCertificatesWithTagsPage, giftCertificateWithTags);
    }

    //TODO -- test manually
//    @Test
//    void testGetCertificatesByTags_noGiftCertificatesWithTags() {
//        when(requestParamsMapper.mapParams(sortParams)).thenReturn(sort);
//        when(tagService.getTagsByNames(tagNames)).thenReturn(tags);
//        when(giftCertificateRepository.findByTagsIn(tags,PageRequest.of(offset, max, sort)))
//                .thenReturn(emptyGiftCertificatePage);
//
//        Page<GiftCertificate> giftCertificatesWithoutTags = giftCertificateService
//                .getGiftCertificatesByTags(sortParams, max, offset, tagNames);
//
//        verify(tagService).getTagsByNames(noTagNames);
//        verify(giftCertificateRepository).findByTagsIn(tags,PageRequest.of(offset, max, sort));
//        assertEquals(emptyGiftCertificatePage, giftCertificatesWithoutTags);
//
//    }
//
    @Test
    void testDelete_idExists() {
        when(giftCertificateRepository.findById(giftCertificateId)).thenReturn(Optional.of(existingGiftCertificate));

        boolean result = giftCertificateService.delete(giftCertificateId);

        verify(giftCertificateRepository).findById(giftCertificateId);
        assertTrue(result);
    }

    @Test
    void testDelete_idDoesNotExist() {
        when(giftCertificateRepository.findById(giftCertificateId)).thenReturn(Optional.empty());

        boolean result = giftCertificateService.delete(giftCertificateId);

        verify(giftCertificateRepository).findById(giftCertificateId);
        assertFalse(result);

    }

    @Test
    void testCreateGiftCertificate_withName() {
        GiftCertificate inputGiftCertificate = new GiftCertificate.GiftCertificateBuilder(giftCertificateName)
                .description(description)
                .price(price)
                .duration(duration)
                .tags(tags)
                .build();

        GiftCertificate expectedGiftCertificate = new GiftCertificate.GiftCertificateBuilder(giftCertificateName)
                .id(giftCertificateId)
                .description(description)
                .price(price)
                .duration(duration)
                .createDate(createDate)
                .lastUpdateDate(lastUpdateDate)
                .tags(tags)
                .build();

        inputGiftCertificate.setCreateDate(createDate);
        inputGiftCertificate.setLastUpdateDate(lastUpdateDate);
        when(giftCertificateRepository.save(inputGiftCertificate)).thenReturn(expectedGiftCertificate);

        GiftCertificate createdGiftCertificate = giftCertificateService.create(inputGiftCertificate);

        verify(giftCertificateRepository).save(inputGiftCertificate);
        assertTrue(nonNull(createdGiftCertificate));
        assertEquals(expectedGiftCertificate, createdGiftCertificate);
    }

    @Test
    void testCreateGiftCertificate_nameAlreadyExists() {
        GiftCertificate giftCertificate = new GiftCertificate
                .GiftCertificateBuilder(nonExistingGiftCertificateName)
                .build();
        when(giftCertificateService.giftCertificateAlreadyExists(giftCertificate)).thenReturn(true);

        Exception constraintViolationException = assertThrows(DuplicateKeyException.class,
                () -> giftCertificateService.create(giftCertificate));
        String expectedMessage = String
                .format("Gift certificate with name %s already exists", giftCertificate.getName());
        String actualMessage = constraintViolationException.getMessage();

        verify(giftCertificateRepository, never()).save(giftCertificate);
        assertEquals(expectedMessage, actualMessage);
    }


    //TODO -- finish
//    @Test
//    void testUpdateGiftCertificate_giftCertificateExists() {
//        when(giftCertificateService.findById(giftCertificateId)).thenReturn(existingGiftCertificate);
//        changedGiftCertificate.setId(giftCertificateId);
//        when(giftCertificateRepository.save(changedGiftCertificate)).thenReturn(changedGiftCertificate);
//
//        GiftCertificate firstGiftCertificate = giftCertificateService.findById(giftCertificateId);
//        firstGiftCertificate.setLastUpdateDate(LocalDateTime.now());
//        giftCertificateService.update(changedGiftCertificate, giftCertificateId);
//        GiftCertificate secondGiftCertificate = giftCertificateService.findById(giftCertificateId);
//
//        verify(giftCertificateRepository).save(existingGiftCertificate);
//        assertEquals(secondGiftCertificate, changedGiftCertificate);
//
//    }
//
//    @Test
//    void testUpdateGiftCertificate_giftCertificateDoesNotExist() {
//        when(giftCertificateService.getById(nonExistingGiftCertificateId)).thenThrow(new NoSuchElementException("No Gift Certificate with id [" + nonExistingGiftCertificateId + "] exists"));
//
//        Exception NoSuchElementException = assertThrows(NoSuchElementException.class, () -> giftCertificateService.update(changedGiftCertificate, nonExistingGiftCertificateId));
//        String expectedMessage = "No Gift Certificate with id [" + nonExistingGiftCertificateId + "] exists";
//        String actualMessage = NoSuchElementException.getMessage();
//
//        verify(giftCertificateRepository, never()).updateGiftCertificate(changedGiftCertificate, nonExistingGiftCertificate);
//        assertEquals(expectedMessage, actualMessage);
//    }

}