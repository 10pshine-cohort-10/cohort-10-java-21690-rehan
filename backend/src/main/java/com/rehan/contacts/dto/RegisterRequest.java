package com.rehan.contacts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be 3 to 30 characters")
        String username,

        @Email(message = "Enter a valid email")
        String email,

        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters")
        String password,

        @Size(max = 255, message = "First name must be at most 255 characters")
        String firstName,

        @Size(max = 255, message = "Last name must be at most 255 characters")
        String lastName
) {
}
