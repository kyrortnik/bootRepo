package com.epam.esm.controller;

import com.epam.esm.Tag;
import com.epam.esm.exception.ControllerExceptionEntity;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.handler.ApplicationExceptionHandler;
import com.epam.esm.impl.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController {

    private static final String DEFAULT_ORDER = "ASC";
    private static final String DEFAULT_MAX_VALUE = "20";


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

    //TODO -- add offset for pagination
    @GetMapping("/")
    public List<Tag> getTags(
            @RequestParam(value = "order", defaultValue = DEFAULT_ORDER) String order,
            @RequestParam(value = "max", defaultValue = DEFAULT_MAX_VALUE) int max) {
        List<Tag> tags = tagService.getAll(order, max);
        if (tags.isEmpty()) {
            throw new NoEntitiesFoundException();
        }
        return tags;
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Tag create(@RequestBody Tag tag) {
        Optional<Tag> createdTag = tagService.create(tag);
        return createdTag.orElseThrow(() -> new DuplicateKeyException("Tag with name [" + tag.getName() +"] already exists"));

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

//    @ExceptionHandler(NoSuchElementException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ControllerExceptionEntity noSuchElement(NoSuchElementException e){
//        return new ControllerExceptionEntity(getErrorCode(404),e.getMessage());
//    }
//
//    private static int getErrorCode(int errorCode) {
//        long counter = 0;
//        counter++;
//        return Integer.parseInt(errorCode + String.valueOf(counter));
//    }

//    @GetMapping("/mostUsedTagOfRichestUser")
//    public Tag mostUsedTagForUser(){
//
//        return tagService.getMostUsedTag().orElseThrow(() -> new NoSuchElementException("No orders for this user"));
//    }

}
