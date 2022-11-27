package com.shreeharibi.userservice.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.shreeharibi.userservice.model.DAO.AppUser;
import com.shreeharibi.userservice.repository.AppUserRepository;
import com.shreeharibi.userservice.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
@Slf4j
public class Common {

    private final AppUserRepository appUserRepository;

    @GetMapping("health")
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/token/refresh")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("JSADLJASLK".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();

                Optional<AppUser> byEmail = appUserRepository.findByEmail(username);
                if (byEmail.isPresent()) {
                    String accessToken = JWT.create()
                            .withSubject(byEmail.get().getUsername()) // can be any string which is unique
                            .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) //10 min expiry time
                            .withIssuer(request.getRequestURL().toString()) //company name or author
                            .withClaim("roles", Collections.singletonList(byEmail.get().getAppUserRole().toString()))
                            .sign(algorithm);
                    response.setHeader("access_token", accessToken);
                    response.setHeader("refresh_token", refreshToken);
                }
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                response.setHeader("error", e.getMessage());
                response.sendError(HttpStatus.FORBIDDEN.value());
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
