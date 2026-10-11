package com.incidentplatform.identity.service;


import com.incidentplatform.common.dto.MessageResponse;
import com.incidentplatform.common.utils.JwtUtils;
import com.incidentplatform.identity.dto.requests.LoginRequest;
import com.incidentplatform.identity.dto.response.AuthResponse;
import com.incidentplatform.identity.entity.User;
import com.incidentplatform.identity.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;


    public AuthResponse login(LoginRequest request, HttpServletResponse response){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new BadCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid email or password");
        }

        ResponseCookie  cookie=  jwtUtils.getJwtCookie(request.getEmail());

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new AuthResponse(user.getId(),user.getUsername(),user.getEmail(),user.getRole().name());
    }


    public MessageResponse Logout(HttpServletResponse response){
        ResponseCookie cleanCookie = jwtUtils.getCleanJwtCookie();
        response.setHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());

        return new MessageResponse("Logout successful");
    }
}
