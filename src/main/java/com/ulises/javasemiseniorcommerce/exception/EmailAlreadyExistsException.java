package com.ulises.javasemiseniorcommerce.exception;

/**
 * @author ulide
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
