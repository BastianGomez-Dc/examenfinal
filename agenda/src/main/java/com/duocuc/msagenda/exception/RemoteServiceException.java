package com.duocuc.msagenda.exception;

// Se lanza cuando falla la comunicacion con un microservicio remoto (timeout, caido, error inesperado).
public class RemoteServiceException extends RuntimeException {

    public RemoteServiceException(String message) {
        super(message);
    }
}
