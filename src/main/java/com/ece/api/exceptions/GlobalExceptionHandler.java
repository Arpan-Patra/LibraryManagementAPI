package com.ece.api.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    //handle records which are not found (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return createResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    //handle duplicate ISBN or email (409)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
        return createResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    //handle database duplicate or relationship errors (409)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseError(DataIntegrityViolationException ex) {
        return createResponse("The record already exists or is being used by another record", HttpStatus.CONFLICT);
    }

    //handle no available copies (400)
    @ExceptionHandler(NoAvailableCopiesException.class)
    public ResponseEntity<Map<String, Object>> handleNoCopies(NoAvailableCopiesException ex) {
        return createResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //handle more than three active issues (400)
    @ExceptionHandler(MaximumActiveIssuesException.class)
    public ResponseEntity<Map<String, Object>> handleMaximumIssues(MaximumActiveIssuesException ex) {
        return createResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //handle book returned for a second time (400)
    @ExceptionHandler(BookAlreadyReturnedException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyReturned(BookAlreadyReturnedException ex) {
        return createResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //handle deletion of a student or book which has issue records (409)
    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<Map<String, Object>> handleResourceInUse(ResourceInUseException ex) {
        return createResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    //handle invalid field values (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Invalid request data");
        response.put("errors", fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //handle incorrect JSON or invalid data format (400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        return createResponse("Invalid request body. Please check the JSON data", HttpStatus.BAD_REQUEST);
    }

    //handle other incorrect values (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return createResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //prepare the common error response
    private ResponseEntity<Map<String, Object>> createResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
