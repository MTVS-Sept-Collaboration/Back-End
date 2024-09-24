package com.homefit.backend.login.service;

import com.homefit.backend.login.dto.LoginRequestDto;
import com.homefit.backend.login.config.provider.JwtTokenProvider;
import com.homefit.backend.login.dto.LoginResponseDto;
import com.homefit.backend.login.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUserName(), loginRequestDto.getPassword())
        );

        User user = userService.findByUserName(loginRequestDto.getUserName());
        String token = jwtTokenProvider.generateToken(user);

        return new LoginResponseDto(user.getId(), token);
    }
}
