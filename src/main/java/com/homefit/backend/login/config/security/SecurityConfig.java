package com.homefit.backend.login.config.security;

import com.homefit.backend.login.config.properties.CorsProperties;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.exception.RestAuthenticationEntryPoint;
import com.homefit.backend.login.oauth.filter.TokenAuthenticationFilter;
import com.homefit.backend.login.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.homefit.backend.login.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.homefit.backend.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.homefit.backend.login.oauth.service.CustomOAuth2UserService;
import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())  // CORS 설정 적용
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화 (RestAPI 이므로)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (!response.isCommitted()) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write("{\"error\": \"Access Denied\"}");
                            }
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Swagger 엔드포인트
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()

                        // 카카오 로그인 전용 엔드포인트
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()

                        // 관리자 전용 엔드포인트
                        .requestMatchers("/api/admin/**").hasAuthority(RoleType.ADMIN.getCode())

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().permitAll()
                )
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // 토큰 인증 필터 추가
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization") // OAuth2 로그인 시작 URI
                                .authorizationRequestRepository(authorizationRequestRepository) // 쿠키 기반 인증 요청 저장소
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*") // OAuth2 로그인 후 리디렉션 URI
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService) // OAuth2 사용자 정보 처리 서비스
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler) // OAuth2 로그인 성공 핸들러
                        .failureHandler(oAuth2AuthenticationFailureHandler) // OAuth2 로그인 실패 핸들러
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
     * Cors 설정
     * */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        if (corsProperties.getAllowedOrigins() != null && !corsProperties.getAllowedOrigins().isEmpty()) {
            for (String allowedOrigin : corsProperties.getAllowedOrigins()) {
                config.addAllowedOriginPattern(allowedOrigin);
            }
        }

        config.setAllowedMethods(corsProperties.getAllowedMethods());
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        config.setAllowCredentials(true);
        config.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /*
     * 토큰 필터 설정
     * */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    /*
     * Rest Template 지정
     * */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
