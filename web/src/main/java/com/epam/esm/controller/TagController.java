package com.epam.esm.controller;

import com.epam.esm.Tag;
import com.epam.esm.impl.TagService;
import com.epam.esm.util.GetMethodProperty;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "api/v1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

    private final TagService tagService;


    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/{tagId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Tag getTagById(@PathVariable Long tagId) {
        LOGGER.debug("Entering TagController.getTag()");

        Tag tag = tagService.findById(tagId);

        tag.add(linkTo(methodOn(TagController.class)
                .getTagById(tagId))
                .withSelfRel());

        tag.add(linkTo(methodOn(TagController.class)
                .deleteTag(tagId))
                .withRel("delete"));

        LOGGER.debug("Exiting TagController.getTag()");
        return tag;

    }


    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Page<Tag> getTags(
            @RequestParam(value = "sort_by", defaultValue = GetMethodProperty.DEFAULT_SORT_BY) List<String> sortBy,
            @RequestParam(value = "max", defaultValue = GetMethodProperty.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "offset", defaultValue = GetMethodProperty.DEFAULT_OFFSET) int offset) {
        LOGGER.debug("Entering TagController.getTags()");

        Page<Tag> tags = tagService.findTags(sortBy, max, offset);

        tags.forEach(tag -> {
                    tag.add(linkTo(methodOn(TagController.class)
                            .getTagById(tag.getId()))
                            .withSelfRel());
                    tag.add(linkTo(methodOn(TagController.class)
                            .deleteTag(tag.getId()))
                            .withRel("delete"));
                }
        );

        LOGGER.debug("Exiting TagController.getTags()");
        return tags;
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Tag create(@RequestBody Tag tag) {
        LOGGER.debug("Entering TagController.create()");

        Tag createdTag = tagService.create(tag);

        createdTag.add(linkTo(methodOn(TagController.class)
                .getTagById(createdTag.getId()))
                .withSelfRel());

        createdTag.add(linkTo(methodOn(TagController.class)
                .deleteTag(createdTag.getId()))
                .withRel("delete"));

        LOGGER.debug("Exiting TagController.create()");
        return createdTag;


    }


    @DeleteMapping("/{tagId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteTag(@PathVariable Long tagId) {
        LOGGER.debug("Entering TagController.delete()");

        ResponseEntity<String> response = tagService.delete(tagId)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(String.format("No tag with tagId [%s] was found", tagId), HttpStatus.OK);

        LOGGER.debug("Exiting TagController.delete()");
        return response;
    }

    @GetMapping("/mostUsedTagForRichestUser")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public Tag getMostUsedTagForRichestUser() {
        LOGGER.debug("Entering TagController.getMostUsedTagForRichestUser()");

        Tag tag = tagService.getMostUsedTagForRichestUser();

        LOGGER.debug("Exiting TagController.getMostUsedTagForRichestUser()");
        return tag;
    }


}
