package com.ulises.javasemiseniorcommerce.exception;

import jakarta.validation.ConstraintViolationException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author ulide
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Bad Request Exceptions
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> emailAlreadyExistsExceptionHandler(EmailAlreadyExistsException e) {
        LOGGER.warn("EmailAlreadyExistsExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.CONFLICT, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(PedidoSinProductosException.class)
    public ResponseEntity<ExceptionResponse> pedidoSinProductosExceptionHandler(PedidoSinProductosException e) {
        LOGGER.warn("PedidoSinProductosExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ExceptionResponse> stockInsuficienteExceptionHandler(StockInsuficienteException e) {
        LOGGER.warn("StockInsuficienteExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        LOGGER.warn("MethodArgumentTypeMismatchException - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        LOGGER.warn("IllegalArgumentException - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> constraintViolationExceptionHandler(ConstraintViolationException e) {
        LOGGER.warn("ConstraintViolationException - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
        LOGGER.warn("HttpMessageNotReadableException - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> validationExceptionHandler(MethodArgumentNotValidException e) {
        LOGGER.warn("ValidationExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    // Not Found Exceptions
    @ExceptionHandler(PedidoNotFoundException.class)
    public ResponseEntity<ExceptionResponse> pedidoNotFoundExceptionHandler(PedidoNotFoundException e) {
        LOGGER.warn("PedidoNotFoundExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(ProductoNotFoundException.class)
    public ResponseEntity<ExceptionResponse> productoNotFoundExceptionHandler(ProductoNotFoundException e) {
        LOGGER.warn("ProductoNotFoundExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> userNotFoundExceptionHandler(UserNotFoundException e) {
        LOGGER.warn("UserNotFoundExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> noResourceFoundExceptionHandler(NoResourceFoundException e) {
        LOGGER.warn("NoResourceFoundException - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.NOT_FOUND, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    //Unauthorized Exceptions

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> authenticationExceptionHandler(AuthenticationException e) {
        LOGGER.warn("AuthenticationExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ExceptionResponse> authenticationFailedExceptionHandler(AuthenticationFailedException e) {
        LOGGER.warn("AuthenticationFailedExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> badCredentialsExceptionHandler(BadCredentialsException e) {
        LOGGER.warn("BadCredentialsExceptionHandler - Message: {}", e.getMessage());
        ExceptionResponse response = new ExceptionResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
}
