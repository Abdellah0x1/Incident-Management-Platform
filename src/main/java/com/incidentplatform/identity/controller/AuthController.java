package com.incidentplatform.identity.controller;


import com.incidentplatform.common.dto.MessageResponse;
import com.incidentplatform.identity.dto.requests.LoginRequest;
import com.incidentplatform.identity.dto.response.AuthResponse;
import com.incidentplatform.identity.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response){
        AuthResponse authResponse = authService.login(request,response);

        return ResponseEntity.ok(authResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletResponse response){
        return ResponseEntity.ok(authService.Logout(response));
    }
}
