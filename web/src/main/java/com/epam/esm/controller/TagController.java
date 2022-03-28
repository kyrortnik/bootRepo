package com.epam.esm.controller;

import com.epam.esm.Tag;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.TagService;
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

import static java.util.Objects.isNull;
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

    @GetMapping("/{id}")
    public Tag getTag(@PathVariable Long id) {
        Optional<Tag> tag = tagService.getById(id);
        return tag.orElseThrow(() -> new NoSuchElementException("Tag with id [" + id + "] not found"));

    }


    @GetMapping("/")
    public List<Tag> getTags(
            @RequestParam(value = "sort_by", defaultValue = GetMethodProperty.DEFAULT_SORT_BY) List<String> sortBy,
            @RequestParam(value = "max", defaultValue = GetMethodProperty.DEFAULT_MAX_VALUE) int max,
            @RequestParam(value = "offset", defaultValue = GetMethodProperty.DEFAULT_OFFSET) int offset) {
        LinkedHashMap<String, Boolean> sortingParams = RequestMapper.mapSortingParams(sortBy);
        List<Tag> tags = tagService.getAll(sortingParams, max, offset);
        if (tags.isEmpty()) {
            throw new NoEntitiesFoundException("No tags are found");
        }
        tags.forEach(tag -> tag.add(linkTo(methodOn(TagController.class)
                .getTag(tag.getId()))
                .withSelfRel())
        );
        return tags;
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Tag create(@RequestBody Tag tag) {
        if (isNull(tag.getName())) {
            throw new NullPointerException("Tag name can not be empty");
        }
        return tagService.create(tag).orElseThrow(() -> new DuplicateKeyException("Tag with name [" + tag.getName() + "] already exists"));


    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        ResponseEntity<String> response;
        if (tagService.delete(id)) {
            response = new ResponseEntity<>(HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("No tag with such id was found", HttpStatus.OK);
        }
        return response;
    }

    @GetMapping("/mostUsedTagForRichestUser")
    public Tag getMostUsedTagForRichestUser() {
        return tagService.getMostUsedTagForRichestUser()
                .orElseThrow(() -> new NoEntitiesFoundException("No certificates with tags exist in orders"));
    }


}
