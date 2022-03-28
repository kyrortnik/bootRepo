package com.epam.esm.controller;

import com.epam.esm.Tag;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.TagService;
import com.epam.esm.mapper.RequestParamsMapper;
import com.epam.esm.util.GetMethodProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

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
    public Tag getTagById(@PathVariable Long tagId) {
        LOGGER.info("Entering TagController.getTag()");

        Tag tag = tagService.getById(tagId).orElseThrow(() -> new NoSuchElementException("Tag with tagId [" + tagId + "] not found"));

        tag.add(linkTo(methodOn(TagController.class)
                .getTagById(tagId))
                .withSelfRel());

        tag.add(linkTo(methodOn(TagController.class)
                .deleteTag(tagId))
                .withRel("delete"));

        LOGGER.info("Exiting TagController.getTag()");
        return tag;

    }


    @GetMapping("/")
    public List<Tag> getTags(
            @RequestParam(value = "sort_by", defaultValue = GetMethodProperty.DEFAULT_SORT_BY) List<String> sortBy,
            @RequestParam(value = "max", defaultValue = GetMethodProperty.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "offset", defaultValue = GetMethodProperty.DEFAULT_OFFSET) int offset) {
        LOGGER.info("Entering TagController.getTags()");

        LinkedHashMap<String, Boolean> sortingParams = RequestParamsMapper.mapSortingParams(sortBy);
        List<Tag> tags = tagService.getAll(sortingParams, max, offset);
        if (tags.isEmpty()) {
            LOGGER.error("NoEntitiesFoundException in TagController.getTags()\n" +
                    "No Tags exist");
            throw new NoEntitiesFoundException("No Tags exist");
        }
        tags.forEach(tag -> {
                    tag.add(linkTo(methodOn(TagController.class)
                            .getTagById(tag.getId()))
                            .withSelfRel());
                    tag.add(linkTo(methodOn(TagController.class)
                            .deleteTag(tag.getId()))
                            .withRel("delete"));
                }
        );

        LOGGER.info("Exiting TagController.getTags()");
        return tags;
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Tag create(@RequestBody Tag tag) {
        LOGGER.info("Entering TagController.create()");

        if ((tag.getName().isEmpty())) {
            LOGGER.error("NullPointerException in TagController.create()\n" +
                    "Tag name can not be empty");
            throw new NullPointerException("Tag name can not be empty");
        }
        Tag createdTag = tagService.create(tag).orElseThrow(() -> new DuplicateKeyException("Tag with name [" + tag.getName() + "] already exists"));

        createdTag.add(linkTo(methodOn(TagController.class)
                .getTagById(createdTag.getId()))
                .withSelfRel());

        createdTag.add(linkTo(methodOn(TagController.class)
                .deleteTag(createdTag.getId()))
                .withRel("delete"));

        LOGGER.info("Exiting TagController.create()");
        return createdTag;


    }


    @DeleteMapping("/{tagId}")
    public ResponseEntity<String> deleteTag(@PathVariable Long tagId) {
        LOGGER.info("Entering TagController.delete()");

        ResponseEntity<String> response = tagService.delete(tagId)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>("No tag with tagId [" + tagId + "] was found", HttpStatus.OK);

        LOGGER.info("Exiting TagController.delete()");
        return response;
    }

    @GetMapping("/mostUsedTagForRichestUser")
    public Tag getMostUsedTagForRichestUser() {
        LOGGER.info("Entering TagController.getMostUsedTagForRichestUser()");
        Tag tag = tagService.getMostUsedTagForRichestUser()
                .orElseThrow(() -> new NoEntitiesFoundException("No certificates with tags exist in orders"));

        LOGGER.info("Exiting TagController.getMostUsedTagForRichestUser()");
        return tag;
    }


}
