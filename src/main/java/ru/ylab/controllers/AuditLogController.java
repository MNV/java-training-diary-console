package ru.ylab.controllers;

import ru.ylab.dto.AuditLogDTO;
import ru.ylab.exceptions.AccessDeniedException;
import ru.ylab.services.AuditLogService;
import ru.ylab.utils.Console;

import java.util.List;


public class AuditLogController {
    private final AuditLogService auditLogService;

    public AuditLogController() {
        this.auditLogService = new AuditLogService();
    }

    public void viewAuditLog() {
        List<AuditLogDTO> auditLog;
        try {
            auditLog = auditLogService.getAuditLog();
        } catch (AccessDeniedException ex) {
            System.out.println(Console.warning(ex.getMessage()));
            return;
        }

        if (auditLog.isEmpty()) {
            System.out.println(Console.warning("There are no entries in the audit log."));
            return;
        }

        String[][] tableData = auditLog.stream()
            .map(log -> new String[]{
                String.valueOf(log.createdAt()),
                String.valueOf(log.username()),
                String.valueOf(log.action()),
            }).toArray(String[][]::new);

        System.out.println("Audit log: ");
        System.out.println(Console.createTable(
            new String[]{"Date", "User", "Action"}, tableData)
        );
    }
}
