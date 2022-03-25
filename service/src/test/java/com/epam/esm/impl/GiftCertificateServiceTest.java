package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.withSettings;

import static org.mockito.Mockito.*;

class GiftCertificateServiceTest {

    // mocks
    private final GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class, withSettings().verboseLogging());

    private final TagService tagService = Mockito.mock(TagService.class, withSettings().verboseLogging());

    // class under test
    private final GiftCertificateService giftCertificateService = new GiftCertificateService(giftCertificateRepository, tagService);

    private final long giftCertificateId = 1L;
    private final String giftCertificateName = "certificate name";
    private final String description = "certificate description";
    private final Long price = 100L;
    private final long duration = 120;
    private final LocalDateTime createDate = LocalDateTime.now();
    private final LocalDateTime lastUpdateDate = LocalDateTime.now();

    private final String order = "ASC";
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


    @Test
    void testGetById_idExists() {
        GiftCertificate giftCertificate = new GiftCertificate(giftCertificateId, giftCertificateName, description, price, duration, createDate, lastUpdateDate);
        when(giftCertificateRepository.getCertificateById(giftCertificateId)).thenReturn(Optional.of(giftCertificate));

        Optional<GiftCertificate> returnGiftCertificate = giftCertificateService.getById(giftCertificateId);

        verify(giftCertificateRepository).getCertificateById(giftCertificateId);
        assertTrue(returnGiftCertificate.isPresent());
        assertEquals(giftCertificate, returnGiftCertificate.get());
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
        GiftCertificate giftCertificate = new GiftCertificate(giftCertificateId, giftCertificateName, description, price, duration, createDate, lastUpdateDate);
        when(giftCertificateRepository.getGiftCertificateByName(giftCertificateName)).thenReturn(Optional.of(giftCertificate));

        Optional<GiftCertificate> returnGiftCertificate = giftCertificateService.getGiftCertificateByName(giftCertificateName);

        verify(giftCertificateRepository).getGiftCertificateByName(giftCertificateName);
        assertTrue(returnGiftCertificate.isPresent());
        assertEquals(giftCertificate, returnGiftCertificate.get());
    }

    @Test
    void testGetGiftCertificateByName_nameDoesNotExists() {
        String nonExistingGiftCertificateName = "non-existing name";
        when(giftCertificateRepository.getGiftCertificateByName(nonExistingGiftCertificateName)).thenThrow(NoResultException.class);

        Exception noSuchElementException = assertThrows(NoSuchElementException.class, () -> giftCertificateService.getGiftCertificateByName(nonExistingGiftCertificateName));
        String expectedMessage = "Gift Certificate with name [" + nonExistingGiftCertificateName + "] does not exist";
        String actualMessage = noSuchElementException.getMessage();

        verify(giftCertificateRepository).getGiftCertificateByName(nonExistingGiftCertificateName);
        assertEquals(expectedMessage, actualMessage);
    }


//    @Test
//    void testGetAll_giftCertificatesExist() {
//        when(giftCertificateRepository.getGiftCertificates(order, max, offset)).thenReturn(giftCertificates);
//
//        List<GiftCertificate> returnGiftCertificates = giftCertificateService.getAll(order, max, offset);
//
//        verify(giftCertificateRepository).getGiftCertificates(order, max, offset);
//        assertEquals(giftCertificates, returnGiftCertificates);
//    }
//
//    @Test
//    void testGetAll_noGiftCertificates() {
//        when(giftCertificateRepository.getGiftCertificates(order, max, offset)).thenReturn(noGiftCertificates);
//
//        List<GiftCertificate> returnGiftCertificates = giftCertificateService.getAll(order, max, offset);
//
//        verify(giftCertificateRepository).getGiftCertificates(order, max, offset);
//        assertEquals(noGiftCertificates, returnGiftCertificates);
//
//    }

//    @Test
//    void testGetCertificatesByTags_giftCertificatesWithTagsExist() {
//        when(tagService.getTagsByNames(tagNames)).thenReturn(tags);
//        when(giftCertificateRepository.getGiftCertificatesByTags(order, max, tags, offset)).thenReturn(giftCertificatesWithTags);
//
//        List<GiftCertificate> returnGiftCertificates = giftCertificateService.getCertificatesByTags(order, max, tagNames, offset);
//
//        verify(tagService).getTagsByNames(tagNames);
//        verify(giftCertificateRepository).getGiftCertificatesByTags(order, max, tags, offset);
//        assertEquals(giftCertificatesWithTags, returnGiftCertificates);
//    }
//
//    @Test
//    void testGetCertificatesByTags_noGiftCertificatesWithTags() {
//        when(tagService.getTagsByNames(noTagNames)).thenReturn(noTags);
//        when(giftCertificateRepository.getGiftCertificatesByTags(order, max, noTags, offset)).thenReturn(noGiftCertificates);
//
//        List<GiftCertificate> returnGiftCertificates = giftCertificateService.getCertificatesByTags(order, max, noTagNames, offset);
//
//        verify(tagService).getTagsByNames(noTagNames);
//        verify(giftCertificateRepository).getGiftCertificatesByTags(order, max, noTags, offset);
//        assertEquals(noGiftCertificates, returnGiftCertificates);
//
//    }

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

//    @Test
//    void testUpdateGiftCertificate_giftCertificateExists() {
//        GiftCertificate changedGiftCertificate = new GiftCertificate(giftCertificateName, description, price, duration, createDate, lastUpdateDate, tags);
//        GiftCertificate updatedGiftCertificate = new GiftCertificate(giftCertificateId, giftCertificateName, description, price, duration, createDate, lastUpdateDate, tags);
//
//        when(giftCertificateRepository.updateGiftCertificate(changedGiftCertificate, giftCertificateId)).thenReturn(Optional.of(updatedGiftCertificate));
//
//        boolean giftCertificateIsUpdated = giftCertificateService.update(changedGiftCertificate, giftCertificateId);
//
//        verify(giftCertificateRepository).updateGiftCertificate(changedGiftCertificate, giftCertificateId);
//        assertTrue(giftCertificateIsUpdated);
//
//    }
//
//    @Test
//    void testUpdateGiftCertificate_giftCertificateDoesNotExist() {
//
//        GiftCertificate changedGiftCertificate = new GiftCertificate(giftCertificateName, description, price, duration, createDate, lastUpdateDate, tags);
//        long notExistingGiftCertificateID = 9999;
//        when(giftCertificateRepository.updateGiftCertificate(changedGiftCertificate, notExistingGiftCertificateID)).thenThrow(NoSuchElementException.class);
//
//        Exception NoSuchElementException = assertThrows(NoSuchElementException.class, () -> giftCertificateService.update(changedGiftCertificate, notExistingGiftCertificateID));
//        String expectedMessage = "Certificate with id [" + notExistingGiftCertificateID + "] doesn't exist";
//        String actualMessage = NoSuchElementException.getMessage();
//
//        verify(giftCertificateRepository).updateGiftCertificate(changedGiftCertificate, notExistingGiftCertificateID);
//        assertEquals(expectedMessage, actualMessage);
//    }

}