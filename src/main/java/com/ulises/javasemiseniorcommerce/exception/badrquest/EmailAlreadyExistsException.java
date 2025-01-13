package com.ulises.javasemiseniorcommerce.exception.badrquest;

/**
 * @author ulide
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
