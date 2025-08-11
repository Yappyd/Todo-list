package com.yappyd.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "Health check endpoint")
@RestController
public class HealthController {

    @Operation(summary = "Check service health", description = "Returns status if the service is running")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is up and running"),
            @ApiResponse(responseCode = "503", description = "Service is down or unavailable")
    })
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is up");
    }
}