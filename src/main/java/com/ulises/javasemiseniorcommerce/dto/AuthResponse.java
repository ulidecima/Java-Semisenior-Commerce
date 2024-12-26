package com.ulises.javasemiseniorcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * @author ulide
 */
@Builder
@Data
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String message;
}
