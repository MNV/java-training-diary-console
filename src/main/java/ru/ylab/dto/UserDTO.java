package ru.ylab.dto;

public record UserDTO(
    Long id, String username, String password, boolean isAdmin
) {}
