package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GiftCertificateServiceTest {

    // mocks
    private final GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class, withSettings().verboseLogging());

    private final TagService tagService = Mockito.mock(TagService.class, withSettings().verboseLogging());

    // class under test
    private final GiftCertificateService giftCertificateService = new GiftCertificateService(giftCertificateRepository, tagService);

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

    private final Tag firstTag = new Tag(1L, "first tag");
    private final Tag secondTag = new Tag(2L, "second tag");
    private final Tag thirdTag = new Tag(3L, "third tag");

    private final String firstTagName = "first tag";
    private final String secondTagName = "second tag";
    private final String thirdTagName = "third tag";

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

    private GiftCertificate changedGiftCertificate;
    private GiftCertificate existingGiftCertificate;
    private GiftCertificate updatedGiftCertificate;
    private GiftCertificate nonExistingGiftCertificate;

    private final List<GiftCertificate> noGiftCertificates = new ArrayList<>();

    private final List<GiftCertificate> giftCertificates = Arrays.asList(
            new GiftCertificate(1L, "first certificate", "first description", 100L, 120L, LocalDateTime.now(), LocalDateTime.now()),
            new GiftCertificate(2L, "second certificate", "second description", 300L, 30L, LocalDateTime.now(), LocalDateTime.now()),
            new GiftCertificate(3L, "third certificate", "third description", 500L, 90L, LocalDateTime.now(), LocalDateTime.now())
    );

    private final List<GiftCertificate> giftCertificatesWithTags = Arrays.asList(
            new GiftCertificate(1L, "first certificate", "first description", 100L, 120L, LocalDateTime.now(), LocalDateTime.now(), tags),
            new GiftCertificate(2L, "second certificate", "second description", 300L, 30L, LocalDateTime.now(), LocalDateTime.now(), tags),
            new GiftCertificate(3L, "third certificate", "third description", 500L, 90L, LocalDateTime.now(), LocalDateTime.now(), tags)
    );

    private final HashMap<String, Boolean> sortParams = new HashMap<>();

    @BeforeEach
    void setUp() {
        sortParams.put("name", true);

        String changedName = "changedName";
        String changedDescription = "changedDescription";
        long changedPrice = 1500;
        long changedDuration = 300;

        changedGiftCertificate = new GiftCertificate();
        changedGiftCertificate.setName(changedName);
        changedGiftCertificate.setDescription(changedDescription);
        changedGiftCertificate.setPrice(changedPrice);
        changedGiftCertificate.setDuration(changedDuration);
        changedGiftCertificate.setTags(tags);

        existingGiftCertificate = new GiftCertificate();
        existingGiftCertificate.setId(giftCertificateId);
        existingGiftCertificate.setName(giftCertificateName);
        existingGiftCertificate.setDescription(description);
        existingGiftCertificate.setPrice(price);
        existingGiftCertificate.setDuration(duration);
        existingGiftCertificate.setCreateDate(createDate);
        existingGiftCertificate.setLastUpdateDate(lastUpdateDate);
        existingGiftCertificate.setTags(tags);

        updatedGiftCertificate = new GiftCertificate();
        updatedGiftCertificate.setId(giftCertificateId);
        updatedGiftCertificate.setName(changedName);
        updatedGiftCertificate.setDescription(changedDescription);
        updatedGiftCertificate.setPrice(changedPrice);
        updatedGiftCertificate.setDuration(changedDuration);
        updatedGiftCertificate.setCreateDate(createDate);
        updatedGiftCertificate.setLastUpdateDate(dataChangedAfterUpdate);
        updatedGiftCertificate.setTags(tags);

        nonExistingGiftCertificate = new GiftCertificate();
        nonExistingGiftCertificate.setId(nonExistingGiftCertificateId);


    }

    @AfterEach
    void tearDown() {
        sortParams.clear();
        changedGiftCertificate = new GiftCertificate();
        existingGiftCertificate = new GiftCertificate();
        updatedGiftCertificate = new GiftCertificate();
        nonExistingGiftCertificate = new GiftCertificate();
    }

    @Test
    void testGetById_idExists() {
        when(giftCertificateRepository.getCertificateById(giftCertificateId)).thenReturn(Optional.of(existingGiftCertificate));

        Optional<GiftCertificate> returnGiftCertificate = giftCertificateService.getById(giftCertificateId);

        verify(giftCertificateRepository).getCertificateById(giftCertificateId);
        assertTrue(returnGiftCertificate.isPresent());
        assertEquals(existingGiftCertificate, returnGiftCertificate.get());
    }

    @Test
    void testGetById_idDoesNotExist() {
        when(giftCertificateRepository.getCertificateById(giftCertificateId)).thenReturn(Optional.empty());

        Optional<GiftCertificate> returnGiftCertificate = giftCertificateService.getById(giftCertificateId);

        verify(giftCertificateRepository).getCertificateById(giftCertificateId);
        assertFalse(returnGiftCertificate.isPresent());
        assertEquals(Optional.empty(), returnGiftCertificate);
    }

    @Test
    void testGetGiftCertificateByName_nameExists() {
        when(giftCertificateRepository.getGiftCertificateByName(giftCertificateName)).thenReturn(Optional.of(existingGiftCertificate));

        Optional<GiftCertificate> returnGiftCertificate = giftCertificateService.getGiftCertificateByName(giftCertificateName);

        verify(giftCertificateRepository).getGiftCertificateByName(giftCertificateName);
        assertTrue(returnGiftCertificate.isPresent());
        assertEquals(existingGiftCertificate, returnGiftCertificate.get());
    }

    @Test
    void testGetGiftCertificateByName_nameDoesNotExists() {
        String nonExistingGiftCertificateName = "non-existing name";
        when(giftCertificateRepository.getGiftCertificateByName(nonExistingGiftCertificateName)).thenReturn(Optional.empty());

        Optional<GiftCertificate> returnGiftCertificate = giftCertificateService.getGiftCertificateByName(nonExistingGiftCertificateName);

        verify(giftCertificateRepository).getGiftCertificateByName(nonExistingGiftCertificateName);
        assertEquals(Optional.empty(), returnGiftCertificate);
    }


    @Test
    void testGetAll_giftCertificatesExist() {
        when(giftCertificateRepository.getGiftCertificates(sortParams, max, offset)).thenReturn(giftCertificates);

        List<GiftCertificate> returnGiftCertificates = giftCertificateService.getAll(sortParams, max, offset);

        verify(giftCertificateRepository).getGiftCertificates(sortParams, max, offset);
        assertEquals(giftCertificates, returnGiftCertificates);
    }

    @Test
    void testGetAll_noGiftCertificates() {
        when(giftCertificateRepository.getGiftCertificates(sortParams, max, offset)).thenReturn(noGiftCertificates);

        List<GiftCertificate> returnGiftCertificates = giftCertificateService.getAll(sortParams, max, offset);

        verify(giftCertificateRepository).getGiftCertificates(sortParams, max, offset);
        assertEquals(noGiftCertificates, returnGiftCertificates);

    }

    @Test
    void testGetCertificatesByTags_giftCertificatesWithTagsExist() {
        when(tagService.getTagsByNames(tagNames)).thenReturn(tags);
        when(giftCertificateRepository.getGiftCertificatesByTags(sortParams, max, tags, offset)).thenReturn(giftCertificatesWithTags);

        List<GiftCertificate> returnGiftCertificates = giftCertificateService.getCertificatesByTags(sortParams, max, tagNames, offset);

        verify(tagService).getTagsByNames(tagNames);
        verify(giftCertificateRepository).getGiftCertificatesByTags(sortParams, max, tags, offset);
        assertEquals(giftCertificatesWithTags, returnGiftCertificates);
    }

    @Test
    void testGetCertificatesByTags_noGiftCertificatesWithTags() {
        when(tagService.getTagsByNames(noTagNames)).thenReturn(noTags);
        when(giftCertificateRepository.getGiftCertificatesByTags(sortParams, max, noTags, offset)).thenReturn(noGiftCertificates);

        List<GiftCertificate> returnGiftCertificates = giftCertificateService.getCertificatesByTags(sortParams, max, noTagNames, offset);

        verify(tagService).getTagsByNames(noTagNames);
        verify(giftCertificateRepository).getGiftCertificatesByTags(sortParams, max, noTags, offset);
        assertEquals(noGiftCertificates, returnGiftCertificates);

    }

    @Test
    void testDelete_idExists() {
        when(giftCertificateRepository.deleteGiftCertificate(giftCertificateId)).thenReturn(true);

        boolean result = giftCertificateService.delete(giftCertificateId);

        assertTrue(result);
    }

    @Test
    void testDelete_idDoesNotExist() {
        when(giftCertificateRepository.deleteGiftCertificate(giftCertificateId)).thenReturn(false);

        boolean result = giftCertificateService.delete(giftCertificateId);

        assertFalse(result);

    }

    @Test
    void testCreateGiftCertificate_withName() {

        GiftCertificate inputGiftCertificate = new GiftCertificate(giftCertificateName, description, price, duration, null, null, tags);
        GiftCertificate expectedGiftCertificate = new GiftCertificate(giftCertificateId, giftCertificateName, description, price, duration, createDate, lastUpdateDate, tags);

        inputGiftCertificate.setCreateDate(createDate);
        inputGiftCertificate.setLastUpdateDate(lastUpdateDate);
        when(giftCertificateRepository.createGiftCertificate(inputGiftCertificate)).thenReturn(giftCertificateId);
        when(giftCertificateRepository.getCertificateById(giftCertificateId)).thenReturn(Optional.of(expectedGiftCertificate));

        Optional<GiftCertificate> createdGiftCertificate = giftCertificateService.create(inputGiftCertificate);

        verify(giftCertificateRepository).createGiftCertificate(inputGiftCertificate);
        verify(giftCertificateRepository).getCertificateById(giftCertificateId);
        assertTrue(createdGiftCertificate.isPresent());
        assertEquals(expectedGiftCertificate, createdGiftCertificate.get());
    }

    @Test
    void testCreateGiftCertificate_nameAlreadyExists() {
        GiftCertificate giftCertificate = new GiftCertificate();
        when(giftCertificateRepository.createGiftCertificate(giftCertificate)).thenThrow(ConstraintViolationException.class);

        Exception constraintViolationException = assertThrows(ConstraintViolationException.class, () -> giftCertificateService.create(giftCertificate));
        String expectedMessage = "Gift Certificate with name [" + giftCertificate.getName() + "] already exists";
        String actualMessage = constraintViolationException.getMessage();

        verify(giftCertificateRepository).createGiftCertificate(giftCertificate);
        verify(giftCertificateRepository, never()).getCertificateById(giftCertificateId);
        assertEquals(expectedMessage, actualMessage);
    }


    @Test
    void testUpdateGiftCertificate_giftCertificateExists() {

        when(giftCertificateService.getById(giftCertificateId)).thenReturn(Optional.of(existingGiftCertificate));
        when(giftCertificateRepository.updateGiftCertificate(changedGiftCertificate, existingGiftCertificate)).thenReturn(Optional.of(updatedGiftCertificate));

        Optional<GiftCertificate> resultGiftCertificate = giftCertificateService.getById(giftCertificateId);
        resultGiftCertificate.ifPresent(giftCertificate -> giftCertificate.setLastUpdateDate(LocalDateTime.now()));
        boolean giftCertificateIsUpdated = giftCertificateService.update(changedGiftCertificate, giftCertificateId);

        verify(giftCertificateRepository).updateGiftCertificate(changedGiftCertificate, existingGiftCertificate);
        assertEquals(resultGiftCertificate, Optional.of(existingGiftCertificate));
        assertTrue(giftCertificateIsUpdated);

    }

    @Test
    void testUpdateGiftCertificate_giftCertificateDoesNotExist() {
        when(giftCertificateService.getById(nonExistingGiftCertificateId)).thenThrow(new NoSuchElementException("No Gift Certificate with id [" + nonExistingGiftCertificateId + "] exists"));

        Exception NoSuchElementException = assertThrows(NoSuchElementException.class, () -> giftCertificateService.update(changedGiftCertificate, nonExistingGiftCertificateId));
        String expectedMessage = "No Gift Certificate with id [" + nonExistingGiftCertificateId + "] exists";
        String actualMessage = NoSuchElementException.getMessage();

        verify(giftCertificateRepository, never()).updateGiftCertificate(changedGiftCertificate, nonExistingGiftCertificate);
        assertEquals(expectedMessage, actualMessage);
    }

}