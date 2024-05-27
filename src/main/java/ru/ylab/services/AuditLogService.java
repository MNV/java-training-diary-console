package ru.ylab.services;

import ru.ylab.app.Application;
import ru.ylab.dto.AuditLogDTO;
import ru.ylab.dto.UserDTO;
import ru.ylab.exceptions.AccessDeniedException;
import ru.ylab.models.AuditLog;
import ru.ylab.repositories.AuditLogRepository;

import java.util.Collections;
import java.util.List;

public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService() {
        this.auditLogRepository = new AuditLogRepository(Application.dataSource);
    }

    public void createAuditLog(AuditLog auditLog) {
        auditLogRepository.create(auditLog);
    }

    /**
     * Get the audit log (admin only).
     */
    public List<AuditLogDTO> getAuditLog() throws AccessDeniedException {
        UserDTO loggedInUser = Application.getUserSession().getLoggedInUser();
        if (!loggedInUser.isAdmin()) {
            Application.getLogger().log("An attempt to view audit log.");
            throw new AccessDeniedException("Access denied.");
        }

        Application.getLogger().log("Viewed audit log.");

        return Collections.unmodifiableList(auditLogRepository.findAll());
    }
}
