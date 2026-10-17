package com._wellness.wellness.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuditAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditLogService auditLogService;

    public AuditAccessDeniedHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth == null) ? "anonymous" : auth.getName();
        String uri = request.getRequestURI().replaceAll("[\\r\\n]", "_");

        auditLogService.logEvent("RBAC_DENIED " + uri, username, "DENIED_403");
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
    }
}
