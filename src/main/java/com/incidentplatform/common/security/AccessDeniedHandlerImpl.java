package com.incidentplatform.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpServletResponse.SC_FORBIDDEN);
        body.put("message", "You don't have permission to access this resource");
        body.put("error", "Access Denied");
        body.put("path", request.getServletPath());

        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(response.getOutputStream(), body);
    }
}
