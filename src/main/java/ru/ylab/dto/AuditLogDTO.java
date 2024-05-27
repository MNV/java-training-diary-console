package ru.ylab.dto;

import java.util.Date;

public record AuditLogDTO(
    Long userId, String username, String action, Date createdAt
) {}
