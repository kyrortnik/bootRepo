package com.epam.esm.handler;

import io.jsonwebtoken.JwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationExceptionHandler.class);
    private static int errorCodeCounter = 0;
    private static final String ERROR_CODE = "errorCode";
    private static final String STATUS = "status";
    private static final String MESSAGE = "message: ";
    private static final String ERROR_CODE_MESSAGE = "\nerror code:";


    @Bean
    public ErrorAttributes errorAttributes() {

        return new DefaultErrorAttributes() {
            public Map<String, Object> getErrorAttributes(WebRequest requestAttributes, ErrorAttributeOptions includeStackTrace) {

                Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
                errorAttributes.put(ERROR_CODE, String.valueOf(errorAttributes.get(STATUS)) + errorCodeCounter++);

                return errorAttributes;
            }
        };
    }


    @ExceptionHandler(NoSuchElementException.class)
    public void noSuchElement(NoSuchElementException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.NOT_FOUND.value());
        LOGGER.error("NoSuchElementException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.NOT_FOUND.value() + errorCodeCounter);

    }


    @ExceptionHandler(DuplicateKeyException.class)
    public void duplicateKeyException(DuplicateKeyException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.BAD_REQUEST.value());
        LOGGER.error("DuplicateKeyException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.BAD_REQUEST + errorCodeCounter);

    }

    @ExceptionHandler(SQLException.class)
    public void constraintViolationException(ConstraintViolationException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.BAD_REQUEST.value());
        LOGGER.error("SQLException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.BAD_REQUEST + errorCodeCounter);

    }

    @ExceptionHandler(NullPointerException.class)
    public void nullPointerException(NullPointerException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.BAD_REQUEST.value());
        LOGGER.error("NullPointerException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.BAD_REQUEST + errorCodeCounter);

    }


    @ExceptionHandler(NoResultException.class)
    public void noResultException(NoResultException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.NOT_FOUND.value());
        LOGGER.error("NoResultException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.NOT_FOUND + errorCodeCounter);

    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public void incorrectSortByPattern(ArrayIndexOutOfBoundsException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.BAD_REQUEST.value());
        LOGGER.error("ArrayIndexOutOfBoundsException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.BAD_REQUEST + errorCodeCounter);

    }


    @ExceptionHandler(HttpClientErrorException.class)
    public void httpServerErrorException(HttpClientErrorException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.BAD_REQUEST.value());
        LOGGER.error("HttpClientErrorException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.BAD_REQUEST + errorCodeCounter);

    }

    @ExceptionHandler(HttpServerErrorException.class)
    public void httpServerErrorException(HttpServerErrorException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.NOT_FOUND.value());
        LOGGER.error("HttpServerErrorException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.NOT_FOUND + errorCodeCounter);

    }

    @ExceptionHandler(JwtException.class)
    public void jwtException(JwtException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.BAD_REQUEST.value());
        LOGGER.error("JwtException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.BAD_REQUEST + errorCodeCounter);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public void illegalArgumentException(IllegalArgumentException ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.BAD_REQUEST.value());
        LOGGER.error("IllegalArgumentException caught in ApplicationExceptionHandler\n" +
                MESSAGE + ex.getMessage() +
                ERROR_CODE_MESSAGE + HttpStatus.BAD_REQUEST + errorCodeCounter);

    }

}


