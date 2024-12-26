package com.ulises.javasemiseniorcommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author ulide
 */
@Builder
@Data
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private boolean success;
}
