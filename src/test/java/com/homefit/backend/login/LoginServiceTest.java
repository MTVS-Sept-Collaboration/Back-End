package com.homefit.backend.login;

import com.homefit.backend.login.config.properties.AppProperties;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.oauth.entity.RoleType;
import com.homefit.backend.login.oauth.repository.UserRepository;
import com.homefit.backend.login.oauth.token.AuthToken;
import com.homefit.backend.login.oauth.token.AuthTokenProvider;
import com.homefit.backend.login.service.KakaoAuthService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LoginServiceTest {

    @InjectMocks
    private KakaoAuthService kakaoAuthService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthTokenProvider tokenProvider;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        AppProperties.Auth auth = new AppProperties.Auth();
        auth.setTokenExpiry(3600000L); // 1 hour
        when(appProperties.getAuth()).thenReturn(auth);
    }

    /**
     * ### Kakao Login Tests ###
     */
    @DisplayName(value = "#01. 신규 사용자 카카오 로그인 테스트")
    @Test
    @Order(1)
    void loginWithKakaoToken_NewUser_Success() {
        // Arrange
        String kakaoAccessToken = "fake_kakao_token";
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "12345");
        userInfo.put("properties", Map.of("nickname", "TestUser"));
        userInfo.put("kakao_account", Map.of("profile", Map.of("profile_image_url", "http://example.com/image.jpg")));

        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(userInfo, HttpStatus.OK));
        when(userRepository.findByKakaoId(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User savedUser = (User) i.getArguments()[0];
            return User.builder()
                    .id(1L) // Set an ID for the new user
                    .kakaoId(savedUser.getKakaoId())
                    .nickName(savedUser.getNickName())
                    .profileImage(savedUser.getProfileImage())
                    .role(savedUser.getRole())
                    .userStatus(savedUser.getUserStatus())
                    .createdAt(savedUser.getCreatedAt())
                    .updatedAt(savedUser.getUpdatedAt())
                    .build();
        });
        when(tokenProvider.createAuthToken(anyString(), anyString(), any())).thenReturn(new AuthToken("jwt_token", null));

        // Act
        String result = kakaoAuthService.loginWithKakaoToken(kakaoAccessToken);

        // Assert
        assertNotNull(result);
        assertEquals("jwt_token", result);
        verify(userRepository).save(any(User.class));
    }

    @DisplayName(value = "#02. 기존 사용자 카카오 로그인 테스트")
    @Test
    @Order(2)
    void loginWithKakaoToken_ExistingUser_Success() {
        // Arrange
        String kakaoAccessToken = "fake_kakao_token";
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "12345");
        userInfo.put("properties", Map.of("nickname", "TestUser"));
        userInfo.put("kakao_account", Map.of("profile", Map.of("profile_image_url", "http://example.com/image.jpg")));

        User existingUser = User.builder()
                .id(1L)
                .kakaoId("12345")
                .nickName("ExistingUser")
                .role(RoleType.USER)
                .build();

        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenReturn(new org.springframework.http.ResponseEntity<>(userInfo, org.springframework.http.HttpStatus.OK));
        when(userRepository.findByKakaoId(anyString()))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);
        when(tokenProvider.createAuthToken(anyString(), anyString(), any()))
                .thenReturn(new AuthToken("jwt_token", null));

        // Act
        String result = kakaoAuthService.loginWithKakaoToken(kakaoAccessToken);

        // Assert
        assertNotNull(result);
        assertEquals("jwt_token", result);
        verify(userRepository).save(any(User.class));
    }

    @DisplayName(value = "#03. 유효하지 않은 토큰 입력 시 예외 발생 테스트")
    @Test
    @Order(3)
    void loginWithKakaoToken_InvalidToken_ThrowsException() {
        // Arrange
        String invalidKakaoToken = "invalid_token";
        when(restTemplate.exchange(anyString(), any(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> kakaoAuthService.loginWithKakaoToken(invalidKakaoToken));
    }
}