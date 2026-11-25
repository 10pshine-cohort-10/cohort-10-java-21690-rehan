package com.rehan.contacts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyRequest(
        @NotBlank String identifier,
        @NotBlank @Size(min = 5, max = 5) String code
) {
}