package com.homefit.backend.login.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homefit.backend.login.config.properties.AppProperties;
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
import org.springframework.security.oauth2.client.*;
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
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
            OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler
    ) throws Exception {
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
                        .requestMatchers("/", "/login/**", "/oauth2/**", "/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()

                        // 관리자 전용 엔드포인트
                        .requestMatchers("/api/admin/**").hasAuthority(RoleType.ADMIN.getCode())

                        // 사용자 정보 전용 엔드포인트
//                        .requestMatchers("/api/user/**").permitAll()
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
//                        .anyRequest().permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new TokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class) // 토큰 인증 필터 추가
//                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // 토큰 인증 필터 추가
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

        config.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
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

    /*
     * OAuth2 인증 성공 핸들러 지정
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(
            AuthTokenProvider tokenProvider,
            AppProperties appProperties,
            ObjectMapper objectMapper,
            OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        return new OAuth2AuthenticationSuccessHandler(
                tokenProvider, appProperties, objectMapper,
                authorizationRequestRepository, authorizedClientService
        );
    }

    /*
     * OAuth2 인증 실패 핸들러 지정
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(authorizationRequestRepository);
    }
}
