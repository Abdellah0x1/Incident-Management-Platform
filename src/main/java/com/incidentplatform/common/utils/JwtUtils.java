package com.incidentplatform.common.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.cookieName}")
    private String jwtCookieName;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Value("${jwt.cookieSecure}")
    private boolean jwtCookieSecure;


    public ResponseCookie getJwtCookie(String email){
        String jwt = generateJwtFromEmail(email);

        ResponseCookie cookie = ResponseCookie.from(jwtCookieName,jwt).path("/api").maxAge(24*60*60).httpOnly(true).secure(jwtCookieSecure).build();

        log.info("Generated jwt for user {}", jwt);
        return cookie;
    }

    public String getJwtFromCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);

        if(cookie != null){
            return cookie.getValue();
        }

        return null;
    }

    public ResponseCookie getCleanJwtCookie(){
        return ResponseCookie.from(jwtCookieName,null).path("/api").maxAge(0).httpOnly(true).secure(jwtCookieSecure).build();
    }

    private String generateJwtFromEmail(String email){
        return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(new Date().getTime() + jwtExpiration)).signWith(key()).compact();
    }



    private Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64URL.decode(jwtSecret)
        );
    }

    public String getEmailFromJwtToken(String token){
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String token){
        try{
            log.debug("Validating jwt token for user {}", token);
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
            return true;
        }catch (MalformedJwtException e){
            log.error("Invalid JWT token {}", e.getMessage());
        }catch (ExpiredJwtException e){
            log.error("Expired JWT token {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("Unsupported JWT token {}", e.getMessage());
        }catch (IllegalArgumentException e){
            log.error("JWT claims string is empty {}", e.getMessage());
        }
        return false;
    }
}
