package com.epam.esm.controller;

import com.epam.esm.GiftCertificate;
import com.epam.esm.Tag;
import com.epam.esm.exception.ControllerExceptionEntity;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.CertificateService;
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
    private static long errorCodeCounter = 0;

    private final CertificateService service;

    @Autowired
    public GiftCertificateController(CertificateService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public GiftCertificate getCertificate(@PathVariable Long id) {
        GiftCertificate giftCertificate = service.getById(id).orElseThrow(() -> new NoSuchElementException("Certificate with id [" + id + "] not found"));

        giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                .getCertificate(id))
                .withSelfRel());

        return giftCertificate;
    }


    @GetMapping("/")
    public List<GiftCertificate> getCertificates(
            @RequestParam(value = "order", defaultValue = DEFAULT_ORDER) String order,
            @RequestParam(value = "max", defaultValue = MAX_CERTIFICATES_IN_REQUEST) int max,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "pattern", required = false) String pattern) {
        List<GiftCertificate> giftCertificates = service.getEntitiesWithParams(order, max, tag, pattern);
        if (giftCertificates.isEmpty()) {
            throw new NoEntitiesFoundException();
        }

        giftCertificates.forEach(giftCertificate -> {
                    giftCertificate.add(linkTo(methodOn(GiftCertificateController.class)
                            .getCertificate(giftCertificate.getId()))
                            .withSelfRel());

                    giftCertificate.add(linkTo(methodOn(TagController.class)
                            .getTags(DEFAULT_ORDER, Integer.parseInt(MAX_CERTIFICATES_IN_REQUEST)))
                            .withRel("tags"));

                }
        );
        return giftCertificates;
    }

    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    GiftCertificate create(@RequestBody GiftCertificate giftCertificate) {
        Optional<GiftCertificate> createdGiftCertificate = service.create(giftCertificate);

        return createdGiftCertificate.orElseThrow(RuntimeException::new);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGiftCertificate(@PathVariable Long id) {
        ResponseEntity<String> response;
        if (service.delete(id)) {
            response = new ResponseEntity<>(HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("No certificate with id [" + id + "] was found", HttpStatus.OK);
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
            ControllerExceptionEntity error = new ControllerExceptionEntity(getErrorCode(400), "Error while updating");
            responseEntity = new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }
        return responseEntity;
    }


    @GetMapping("/{giftCertificateId}/tags")
    public Set<Tag> getGiftCertificateTags(@RequestParam(value = "giftCertificateId") long userId) {

        return new HashSet<>();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ControllerExceptionEntity noSuchElement(NoSuchElementException e) {
        return new ControllerExceptionEntity(getErrorCode(404), e.getMessage());
    }


    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ControllerExceptionEntity certificateNotFound(EntityNotFoundException e) {
        long certificateId = e.getEntityId();
        return new ControllerExceptionEntity(getErrorCode(404), "Gift Certificate [" + certificateId + "] not found");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ControllerExceptionEntity duplicateKeyValues(DuplicateKeyException e) {
        return new ControllerExceptionEntity(getErrorCode(500), e.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(NoEntitiesFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ControllerExceptionEntity certificatesNotFound(NoEntitiesFoundException e) {
        return new ControllerExceptionEntity(getErrorCode(404), "No certificates are found");
    }


    private static int getErrorCode(int errorCode) {
        errorCodeCounter++;
        return Integer.parseInt(errorCode + String.valueOf(errorCodeCounter));
    }

}