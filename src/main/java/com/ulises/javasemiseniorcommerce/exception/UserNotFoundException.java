package com.ulises.javasemiseniorcommerce.exception;

/**
 * @author ulide
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
