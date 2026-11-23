package com.ece.api.exceptions;

public class NoAvailableCopiesException extends RuntimeException {

    public NoAvailableCopiesException(String message) {
        super(message);
    }
}
