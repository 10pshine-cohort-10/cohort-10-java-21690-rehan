package com.rehan.contacts.dto;

import jakarta.validation.constraints.NotBlank;

public record ResendRequest(
        @NotBlank(message = "Enter your username, email or phone") String identifier
) {
}