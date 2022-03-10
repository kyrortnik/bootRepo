package com.epam.esm.handler;

import com.epam.esm.exception.ControllerExceptionEntity;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.NoEntitiesFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ControllerExceptionEntity noSuchElement(NoSuchElementException e){
        return new ControllerExceptionEntity(getErrorCode(404),e.getMessage());
    }


    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ControllerExceptionEntity tagNotFound(EntityNotFoundException e) {
        long tagId = e.getEntityId();
        return new ControllerExceptionEntity(getErrorCode(404), "Tag [" + tagId + "] not found");
    }

    @ExceptionHandler(NoEntitiesFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ControllerExceptionEntity tagsNotFound(NoEntitiesFoundException e) {
        return new ControllerExceptionEntity(getErrorCode(404), e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ControllerExceptionEntity duplicateKeyException(DuplicateKeyException e) {
        return new ControllerExceptionEntity(getErrorCode(400), "Tag with such name already exists");
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ControllerExceptionEntity constraintViolationException(SQLException e) {
        return new ControllerExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + e.getErrorCode()), e.getMessage());
    }

    private static int getErrorCode(int errorCode) {
        long counter = 0;
        counter++;
        return Integer.parseInt(errorCode + String.valueOf(counter));
    }

}
