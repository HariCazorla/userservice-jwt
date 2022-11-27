package com.shreeharibi.userservice.controller;

import com.shreeharibi.userservice.model.DTO.UserCreationRequest;
import com.shreeharibi.userservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody UserCreationRequest userCreationRequest) {
        return ResponseEntity.ok(registrationService.register(userCreationRequest));
    }

    @GetMapping("confirm")
    public void confirm(@RequestParam String token) {
        registrationService.confirmToken(token);
        return;
    }
}
