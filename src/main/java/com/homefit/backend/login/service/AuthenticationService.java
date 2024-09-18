package com.homefit.backend.login.service;

import com.homefit.backend.login.dto.LoginDto;
import com.homefit.backend.login.config.provider.JwtTokenProvider;
import com.homefit.backend.login.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword())
        );

        User user = userService.findByUserName(loginDto.getUserName());
        return jwtTokenProvider.generateToken(user);
    }
}
