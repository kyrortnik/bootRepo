package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Tag;
import com.epam.esm.impl.GiftCertificateService;
import com.epam.esm.util.DefaultValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class GiftCertificateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftCertificateController.class);

    private final GiftCertificateService giftCertificateService;


    @Autowired
    public GiftCertificateController(GiftCertificateService giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_USER','ROLE_ADMIN')")
    public GiftCertificate getCertificateById(@PathVariable Long id) {
        LOGGER.debug("Entering GiftCertificateController.getCertificatedById()");

        GiftCertificate giftCertificate = giftCertificateService.findGiftCertificateById(id);

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


        LOGGER.debug("Exiting GiftCertificateController.getCertificatedById()");
        return giftCertificate;
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_USER','ROLE_ADMIN')")
    public Page<GiftCertificate> getCertificates(
            @RequestParam(value = "sort_by", defaultValue = DefaultValue.DEFAULT_SORT_BY) List<String> sortBy,
            @RequestParam(value = "max", defaultValue = DefaultValue.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "page", defaultValue = DefaultValue.DEFAULT_PAGE) int page,
            @RequestParam(value = "tag", required = false) Set<String> tags) {
        LOGGER.debug("Entering GiftCertificateController.getCertificates()");

        Page<GiftCertificate> giftCertificates =
                giftCertificateService.getGiftCertificates(tags, sortBy, max, page);

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
        LOGGER.debug("Exiting GiftCertificateController.getCertificates()");
        return giftCertificates;
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    GiftCertificate createGiftCertificate(@RequestBody GiftCertificate giftCertificate) {
        LOGGER.debug("Entering GiftCertificateController.createGiftCertificate()");

        GiftCertificate createdGiftCertificate = giftCertificateService.create(giftCertificate);

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

        LOGGER.debug("Exiting GiftCertificateController.createGiftCertificate()");
        return createdGiftCertificate;
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteGiftCertificate(@PathVariable Long id) {
        LOGGER.debug("Entering GiftCertificateController.deleteGiftCertificate()");

        ResponseEntity<String> response = giftCertificateService.delete(id)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(String.format("No Gift Certificate with id [%s] exists", id), HttpStatus.OK);

        LOGGER.debug("Exiting GiftCertificateController.deleteGiftCertificate()");
        return response;
    }


    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> update(@RequestBody GiftCertificate giftCertificate, @PathVariable Long id) {
        LOGGER.debug("Entering GiftCertificateController.update()");

        giftCertificateService.update(giftCertificate, id);

        LOGGER.debug("Exiting GiftCertificateController.update()");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{giftCertificateId}/tags")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Set<Tag> getGiftCertificateTags(@PathVariable long giftCertificateId) {
        LOGGER.debug("Entering GiftCertificateController.getGiftCertificateTags()");

        Set<Tag> giftCertificateTags = giftCertificateService.getCertificateTags(giftCertificateId);

        giftCertificateTags.forEach(tag -> tag.add(linkTo(methodOn(TagController.class)
                .getTagById(tag.getId()))
                .withSelfRel())
        );
        LOGGER.debug("Exiting GiftCertificateController.getGiftCertificateTags()");
        return giftCertificateTags;
    }
}