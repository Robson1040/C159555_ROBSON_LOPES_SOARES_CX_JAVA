package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @JsonProperty("username")
    @NotBlank(message = "Username é obrigatório")
    String username,
    
    @JsonProperty("password")
    @NotBlank(message = "Password é obrigatório") 
    String password
) {}