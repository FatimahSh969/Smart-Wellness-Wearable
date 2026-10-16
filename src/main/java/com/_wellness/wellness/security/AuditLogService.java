package com._wellness.wellness.security;

import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private static final String AUDIT_FILE = "system_audit.log";

    public synchronized void logEvent(String eventType, String username, String status) {
       
        String logEntry = String.format("[%s] EVENT: %s | USER: %s | STATUS: %s%n",
                LocalDateTime.now(), eventType, username, status);

        try (FileWriter fw = new FileWriter(AUDIT_FILE, true)) {
            fw.write(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to audit log: " + e.getMessage());
        }
    }
}