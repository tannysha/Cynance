package com.coms309.Cynance.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle duplicate entry and other data integrity violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String rootCauseMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (rootCauseMessage.contains("Duplicate entry")) {
            return new ResponseEntity<>("Error: Email or Username already exists.", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Database error occurred: " + rootCauseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle specific SQL constraint violations
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> handleSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException ex) {
        return new ResponseEntity<>("SQL Constraint Violation: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Handle JSON parse errors (invalid request bodies)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>("Invalid JSON payload: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Handle general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle 404 errors (No handler found for the URL)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNotFound(NoHandlerFoundException ex) {
        return new ResponseEntity<>("No endpoint found for: " + ex.getRequestURL(), HttpStatus.NOT_FOUND);
    }
}
