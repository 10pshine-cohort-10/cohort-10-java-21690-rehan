package com.rehan.contacts.dto;

public record UserResponse(
        Long id,
        String username,
        String email,
        String phone,
        String firstName,
        String lastName
) {
}