package com.ulises.javasemiseniorcommerce.exception;

/**
 * @author ulide
 */
public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(String message) {
        super(message);
    }
}
