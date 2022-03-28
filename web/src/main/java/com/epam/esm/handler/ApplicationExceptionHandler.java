package com.epam.esm.handler;

import com.epam.esm.exception.ExceptionEntity;
import com.epam.esm.exception.NoEntitiesFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationExceptionHandler.class);
    private static int errorCodeCounter = 0;

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ExceptionEntity noSuchElement(NoSuchElementException e) {
        ExceptionEntity exceptionEntity = new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.NOT_FOUND.value()) + errorCodeCounter++), e.getMessage());
        LOGGER.error("NoSuchElementException caught in ApplicationExceptionHandler\n" +
                "message: " + exceptionEntity.getMessage() +
                "\nerror code:" + exceptionEntity.getCode());

        return exceptionEntity;
    }

    @ExceptionHandler(NoEntitiesFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ExceptionEntity entitiesNotFound(NoEntitiesFoundException e) {
        ExceptionEntity exceptionEntity = new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.NOT_FOUND.value()) + errorCodeCounter++), e.getMessage());
        LOGGER.error("NoEntitiesFoundException caught in ApplicationExceptionHandler\n" +
                "message: " + exceptionEntity.getMessage() +
                "\nerror code:" + exceptionEntity.getCode());

        return exceptionEntity;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionEntity duplicateKeyException(DuplicateKeyException e) {
        ExceptionEntity exceptionEntity = new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());
        LOGGER.error("DuplicateKeyException caught in ApplicationExceptionHandler\n" +
                "message: " + exceptionEntity.getMessage() +
                "\nerror code:" + exceptionEntity.getCode());

        return exceptionEntity;
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionEntity constraintViolationException(SQLException e) {
        ExceptionEntity exceptionEntity = new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage().split(":")[0]);
        LOGGER.error("SQLException caught in ApplicationExceptionHandler\n" +
                "message: " + exceptionEntity.getMessage() +
                "\nerror code:" + exceptionEntity.getCode());

        return exceptionEntity;
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ExceptionEntity nullPointerException(NullPointerException e) {
        ExceptionEntity exceptionEntity = new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.BAD_REQUEST.value()) + errorCodeCounter++), e.getMessage());
        LOGGER.error("NullPointerException caught in ApplicationExceptionHandler\n" +
                "message: " + exceptionEntity.getMessage() +
                "\nerror code:" + exceptionEntity.getCode());

        return exceptionEntity;
    }

    @ExceptionHandler(NoResultException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    ExceptionEntity noResultException(NoResultException e) {
        ExceptionEntity exceptionEntity = new ExceptionEntity(Integer.parseInt(String.valueOf(HttpStatus.NOT_FOUND.value()) + errorCodeCounter++), e.getMessage());
        LOGGER.error("NoResultException caught in ApplicationExceptionHandler\n" +
                "message: " + exceptionEntity.getMessage() +
                "\nerror code:" + exceptionEntity.getCode());

        return exceptionEntity;
    }

}


