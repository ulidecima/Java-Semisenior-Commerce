package com.ulises.javasemiseniorcommerce.exception;

/**
 * @author ulide
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}