package com.shreeharibi.userservice.service;

import com.shreeharibi.userservice.model.DAO.AppUser;
import com.shreeharibi.userservice.model.DAO.AppUserRole;
import com.shreeharibi.userservice.model.DAO.Token;
import com.shreeharibi.userservice.model.DTO.UserCreationRequest;
import com.shreeharibi.userservice.repository.AppUserRepository;
import com.shreeharibi.userservice.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    private final TokenRepository tokenRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoderl;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).orElseThrow();
    }

    @Transactional
    public String signup(UserCreationRequest request) {
        AppUser appUser = new AppUser(request.getFirstname(),
                request.getLastname(),
                request.getEmail(),
                bCryptPasswordEncoderl.encode(request.getPassword()),
                AppUserRole.USER
        );
        appUserRepository.save(appUser);
        String uuid_token = UUID.randomUUID().toString();
        Token token = new Token(uuid_token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60),
                appUser
                );
        tokenRepository.save(token);
        return uuid_token;
    }

    @Transactional
    public void enableUser(String token) {
        Optional<Token> byToken = tokenRepository.findByToken(token);
        if (byToken.isPresent()) {
            Token tokenDao = byToken.get();
            LocalDateTime expiresAt = tokenDao.getExpiresAt();

            if (expiresAt.isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("token expired");
            }
            tokenDao.setConfirmedAt(LocalDateTime.now());
            tokenRepository.save(tokenDao);

            appUserRepository.enableAppUser(tokenDao.getAppUser().getEmail());
        }
    }
}
