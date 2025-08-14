package com.yappyd.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @NotBlank
    @Size(min = 6, max = 255)
    @ToString.Exclude
    private String password;
}
