package ru.ylab.app;

import ru.ylab.dto.UserDTO;
import ru.ylab.models.AuditLog;
import ru.ylab.services.AuditLogService;


public class Logger {

    private final AuditLogService auditLogService;

    public Logger() {
        this.auditLogService = new AuditLogService();
    }

    public void log(String action) {
        UserDTO loggedInUser = Application.getUserSession().getLoggedInUser();
        AuditLog auditLog = new AuditLog(loggedInUser.id(), action);
        auditLogService.createAuditLog(auditLog);
    }
}
