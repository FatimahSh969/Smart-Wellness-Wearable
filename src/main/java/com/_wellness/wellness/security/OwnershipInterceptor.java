package com._wellness.wellness.security;

import com._wellness.wellness.model.Role;
import com._wellness.wellness.model.User;
import com._wellness.wellness.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class OwnershipInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public OwnershipInterceptor(UserRepository userRepository,
                                AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Map<String, String> vars = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (vars == null || !vars.containsKey("userId")) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return deny(response, request, "anonymous", 401);
        }

        String email = auth.getName();
        User current = userRepository.findByEmail(email).orElse(null);
        if (current == null || current.isDeleted()) {
            return deny(response, request, email, 401);
        }

        Long requestedId;
        try {
            requestedId = Long.valueOf(vars.get("userId"));
        } catch (NumberFormatException e) {
            return deny(response, request, email, 400);
        }

        boolean isAdmin = current.getRole() == Role.ADMIN;
        boolean isOwner = current.getId().equals(requestedId);

        if (!isAdmin && !isOwner) {
            return deny(response, request, email, 403);
        }

        if (isAdmin && !isOwner) {
            auditLogService.logEvent(
                    "ADMIN_ACCESS_OTHER_USER " + safe(request.getRequestURI()),
                    email, "ALLOWED");
        }
        return true;
    }

    private boolean deny(HttpServletResponse response, HttpServletRequest request,
                         String username, int status) throws Exception {
        auditLogService.logEvent(
                "BOLA_DENIED " + safe(request.getRequestURI()),
                username, "DENIED_" + status);
        response.sendError(status);  
        return false;
    }

    private String safe(String s) {
        return s == null ? "" : s.replaceAll("[\\r\\n]", "_");
    }
}
