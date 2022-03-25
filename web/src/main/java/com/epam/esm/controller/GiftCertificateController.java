package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Tag;
import com.epam.esm.exception.ExceptionEntity;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.GiftCertificateService;
import com.epam.esm.mapper.RequestMapper;
import com.epam.esm.util.GetMethodProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class GiftCertificateController {

    private final GiftCertificateService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateController.class);


    @Autowired
    public GiftCertificateController(GiftCertificateService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public GiftCertificate getCertificateById(@PathVariable Long id) {
        GiftCertificate giftCertificate = service.getById(id).orElseThrow(() -> new NoSuchElementException("Certificate with id [" + id + "] not found"));

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getCertificateById(id))
                .withSelfRel());

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .update(giftCertificate, id))
                .withRel("update"));

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .deleteGiftCertificate(id))
                .withRel("delete"));

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getGiftCertificateTags(id))
                .withRel("tags"));

        return giftCertificate;
    }


    @GetMapping("/")
    public List<GiftCertificate> getCertificates(
            @RequestParam(value = "sort_by", defaultValue = GetMethodProperty.DEFAULT_SORT_BY) Set<String> sortBy,
            @RequestParam(value = "max", defaultValue = GetMethodProperty.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "offset", defaultValue = GetMethodProperty.DEFAULT_OFFSET) int offset,
            @RequestParam(value = "tag", required = false) Set<String> tags) {
        HashMap<String, Boolean> sortingParams = RequestMapper.mapSortingParams(sortBy);
        List<GiftCertificate> giftCertificates = Objects.isNull(tags)
                ? service.getAll(sortingParams, max, offset)
                : service.getCertificatesByTags(sortingParams, max, tags, offset);

        if (giftCertificates.isEmpty()) {
            LOGGER.error("No Gift Certificates exists");
            throw new NoEntitiesFoundException("No Gift Certificates exist");
        }
        giftCertificates.forEach(giftCertificate -> {

            giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                    .getCertificateById(giftCertificate.getId()))
                    .withSelfRel());

            giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                    .update(giftCertificate, giftCertificate.getId()))
                    .withRel("update"));

            giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                    .deleteGiftCertificate(giftCertificate.getId()))
                    .withRel("delete"));

            giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                    .getGiftCertificateTags(giftCertificate.getId()))
                    .withRel("tags"));

        });
        return giftCertificates;
    }

    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    GiftCertificate createGiftCertificate(@RequestBody GiftCertificate giftCertificate) {

        GiftCertificate createdGiftCertificate = service.create(giftCertificate).orElseThrow(() -> new DuplicateKeyException("Gift Certificate with name [" + giftCertificate.getName() + "] already exists"));

        createdGiftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getCertificateById(createdGiftCertificate.getId()))
                .withSelfRel());

        createdGiftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .update(giftCertificate, createdGiftCertificate.getId()))
                .withRel("update"));

        createdGiftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .deleteGiftCertificate(createdGiftCertificate.getId()))
                .withRel("delete"));

        createdGiftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getGiftCertificateTags(createdGiftCertificate.getId()))
                .withRel("tags"));


        return createdGiftCertificate;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGiftCertificate(@PathVariable Long id) {
        ResponseEntity<String> response;
        if (service.delete(id)) {
            response = new ResponseEntity<>(HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("No Gift Certificate with id [" + id + "] exists", HttpStatus.OK);
        }
        return response;
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody GiftCertificate giftCertificate, @PathVariable Long id) {
        ResponseEntity<?> responseEntity;
        if (service.update(giftCertificate, id)) {
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            ExceptionEntity error = new ExceptionEntity(0, "Error while updating");
            responseEntity = new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }
        return responseEntity;
    }

    @GetMapping("/{giftCertificateId}/tags")
    public Set<Tag> getGiftCertificateTags(@PathVariable long giftCertificateId) {
        GiftCertificate giftCertificate = service.getById(giftCertificateId).orElseThrow(() -> new NoSuchElementException("no such Gift Certificate exists"));
        Set<Tag> giftCertificateTags = giftCertificate.getTags();

        giftCertificateTags.forEach(tag -> tag.add(linkTo(methodOn(TagController.class)
                .getTag(tag.getId()))
                .withSelfRel())
        );
        return giftCertificateTags;
    }
}