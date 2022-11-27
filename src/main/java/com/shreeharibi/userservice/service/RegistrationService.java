package com.shreeharibi.userservice.service;

import com.shreeharibi.userservice.model.DAO.AppUser;
import com.shreeharibi.userservice.model.DTO.UserCreationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;

    public String register(UserCreationRequest request) {
        return appUserService.signup(request);
    }

    public void confirmToken(String token) {
        appUserService.enableUser(token);
        return;
    }
}
