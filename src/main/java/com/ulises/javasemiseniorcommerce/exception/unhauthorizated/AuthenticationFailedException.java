package com.ulises.javasemiseniorcommerce.exception.unhauthorizated;

/**
 * @author ulide
 */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}