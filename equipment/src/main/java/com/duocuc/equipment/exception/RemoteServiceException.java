package com.duocuc.equipment.exception;

// Thrown when communication with a remote microservice fails (timeout, down, unexpected error).
public class RemoteServiceException extends RuntimeException {

    public RemoteServiceException(String message) {
        super(message);
    }
}
