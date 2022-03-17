package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.withSettings;

import static org.mockito.Mockito.*;

class GiftCertificateServiceTest {

    // mock
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
    private final String tag = "tagName";
    private final String pattern = "pattern";

    private final List<GiftCertificate> giftCertificates = Arrays.asList(
            new GiftCertificate(1L, "first certificate", "first description", 100L, 120L, LocalDateTime.now(), LocalDateTime.now()),
            new GiftCertificate(2L, "second certificate", "second description", 300L, 30L, LocalDateTime.now(), LocalDateTime.now()),
            new GiftCertificate(3L, "third certificate", "third description", 500L, 90L, LocalDateTime.now(), LocalDateTime.now())

    );

    private final List<Tag> tags = Arrays.asList(
            new Tag(1L, "first tag"),
            new Tag(2L, "second tag"),
            new Tag(3L, "third tag")
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
        when(giftCertificateRepository.getGiftCertificateByName(giftCertificateName)).thenReturn(Optional.empty());

        Optional<GiftCertificate> returnGiftCertificate = giftCertificateService.getGiftCertificateByName(giftCertificateName);

        verify(giftCertificateRepository).getGiftCertificateByName(giftCertificateName);
        assertFalse(returnGiftCertificate.isPresent());
        assertEquals(Optional.empty(), returnGiftCertificate);
    }


    @Test
    void getAll() {
    }

    @Test
    void getCertificatesByTags() {
    }

    @Test
    void delete() {
    }

    @Test
    void update() {
    }

    @Test
    void create() {
    }
}