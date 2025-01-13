package com.ulises.javasemiseniorcommerce.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author ulide
 */
@Data
@Getter
public class ExceptionResponse {
    private final LocalDateTime date;
    private final HttpStatus status;
    private final String message;

    public ExceptionResponse(HttpStatus status, String message) {
        this.date = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }
}
