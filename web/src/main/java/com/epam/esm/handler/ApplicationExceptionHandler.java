package com.epam.esm.handler;

import com.epam.esm.exception.ExceptionEntity;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.NoEntitiesFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.NoResultException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ApplicationExceptionHandler {

    private static int errorCodeCounter = 0;

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ExceptionEntity noSuchElement(NoSuchElementException e) {
        return new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());
    }


    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ExceptionEntity tagNotFound(EntityNotFoundException e) {
        long tagId = e.getEntityId();
        return new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());
    }

    @ExceptionHandler(NoEntitiesFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ExceptionEntity tagsNotFound(NoEntitiesFoundException e) {
        return new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionEntity duplicateKeyException(DuplicateKeyException e) {
        return new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionEntity constraintViolationException(SQLException e) {
        return new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionEntity nullPointerException(NullPointerException e){
        return new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());
    }

    @ExceptionHandler(NoResultException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionEntity noResultException(NoResultException e){
        return new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());

    }
}


