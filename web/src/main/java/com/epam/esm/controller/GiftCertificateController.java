package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Tag;
import com.epam.esm.exception.ExceptionEntity;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.GiftCertificateService;
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

    private static final String MAX_CERTIFICATES_IN_REQUEST = "20";
    private static final String DEFAULT_ORDER = "ASC";
    private static final String DEFAULT_OFFSET = "0";

    private final GiftCertificateService service;


    @Autowired
    public GiftCertificateController(GiftCertificateService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public GiftCertificate getCertificate(@PathVariable Long id) {
        GiftCertificate giftCertificate = service.getById(id).orElseThrow(() -> new NoSuchElementException("Certificate with id [" + id + "] not found"));

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getCertificate(id))
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
            @RequestParam(value = "order", defaultValue = DEFAULT_ORDER) String order,
            @RequestParam(value = "max", defaultValue = MAX_CERTIFICATES_IN_REQUEST) int max,
            @RequestParam(value = "tag", required = false) Set<String> tags,
            @RequestParam(value = "offset", defaultValue = DEFAULT_OFFSET) int offset) {
        List<GiftCertificate> giftCertificates = Objects.isNull(tags)
                ? service.getAll(order, max, offset)
                : service.getCertificatesByTags(order, max, tags, offset);

        if (giftCertificates.isEmpty()) {
            throw new NoEntitiesFoundException();
        }
        giftCertificates.forEach(giftCertificate -> {

            giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                    .getCertificate(giftCertificate.getId()))
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
                .getCertificate(createdGiftCertificate.getId()))
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

//        return createdGiftCertificate.orElseThrow(() -> new DuplicateKeyException("Gift Certificate with name [" + giftCertificate.getName() + "] already exists"));

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