package com.homefit.backend.login.config.security;

import com.homefit.backend.login.config.properties.CorsProperties;
import com.homefit.backend.login.entity.RoleType;
import com.homefit.backend.login.config.filter.JwtAuthenticationFilter;
import com.homefit.backend.login.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())  // CORS 설정 적용
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (RestAPI 이므로)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
                .authorizeHttpRequests(auth -> auth
                                // Swagger 엔드포인트
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                                // 로그인 및 회원가입 전용 엔드포인트
                                .requestMatchers("/api/login/**", "/api/register/**").permitAll()

                                // 비밀번호 변경 전용 엔드포인트
                                .requestMatchers("/api/change-password").authenticated()

                                // 관리자 전용 엔드포인트
                                .requestMatchers("/api/admin/**", "/api/logs/**").hasAuthority(RoleType.ADMIN.getCode())

                                // 사용자 정보 전용 엔드포인트
                                .requestMatchers("/api/user/**").authenticated()

                                // 아이템카테고리 전용 엔드포인트
                                .requestMatchers("/api/itemCategory/**").authenticated()

                                // 운동카테고리 전용 엔드포인트
                                .requestMatchers("/api/ExerciseCategory/**").authenticated()

                                // 운동기록 전용 엔드포인트
                                .requestMatchers("/api/exerciseLogs/**").authenticated()

                                // 운동 전용 엔드포인트
                                .requestMatchers("/api/exercises/**").authenticated()

                                // 그 외 모든 요청은 인증 필요
                                .anyRequest().authenticated()
//                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // 토큰 인증 필터 추가
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    /*
     * 비밀번호 암호화 설정
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * 인증 관련 설정
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
     * Cors 설정
     * */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        if (corsProperties.getAllowedHeaders() != null) {
            config.setAllowedHeaders(Collections.singletonList(corsProperties.getAllowedHeaders()));
        } else {
            config.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
        }
        config.setAllowCredentials(true);
        config.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
