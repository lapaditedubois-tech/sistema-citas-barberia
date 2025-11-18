package com.Neita.sistemacitasbarberia.exception;

public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String mensaje) {
        super(mensaje);
    }
}
