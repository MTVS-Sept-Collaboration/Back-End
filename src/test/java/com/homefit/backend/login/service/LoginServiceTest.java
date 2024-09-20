package com.homefit.backend.login.service;

import com.homefit.backend.login.config.provider.JwtTokenProvider;
import com.homefit.backend.login.dto.AdminDto;
import com.homefit.backend.login.dto.LoginRequestDto;
import com.homefit.backend.login.dto.LoginResponseDto;
import com.homefit.backend.login.dto.UserDto;
import com.homefit.backend.login.entity.RoleType;
import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.repository.UserRepository;
import com.homefit.backend.user.entity.UserInfo;
import com.homefit.backend.user.repository.UserInfoRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginServiceTest {

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(authenticationManager, jwtTokenProvider, userService);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    /**
     * ### 회원가입/로그인 테스트 ###
     */
    @DisplayName(value = "#01. 사용자 회원가입 성공 테스트")
    @Test
    @Order(1)
    void registerUser_Success() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUserName("testUser");
        userDto.setPassword("password");
        userDto.setRole(RoleType.USER);

        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(userInfoRepository.save(any(UserInfo.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        User result = userService.registerUser(userDto);

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUserName());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(RoleType.USER, result.getRole());
        verify(userInfoRepository).save(any(UserInfo.class));
    }

    @DisplayName(value = "#02. 로그인 성공 테스트")
    @Test
    @Order(2)
    void login_Success() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUserName("testUser");
        loginRequestDto.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        User user = new User(1L, "testUser", "encodedPassword", RoleType.USER);
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(user));

        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        LoginResponseDto result = authenticationService.login(loginRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("jwtToken", result.getJwtToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUserName(eq("testUser"));
        verify(jwtTokenProvider).generateToken(user);
    }

    @DisplayName(value = "#03. 중복 아이디로 회원가입 실패 테스트")
    @Test
    @Order(3)
    void registerUser_DuplicateUserName_Failure() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUserName("existingUser");
        userDto.setPassword("password");

        when(userRepository.existsByUserName(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.registerUser(userDto));
    }

    @DisplayName(value = "#04. 관리자 회원가입 성공 테스트")
    @Test
    @Order(4)
    void createAdminUser_Success() {
        // Arrange
        AdminDto adminDto = new AdminDto();
        adminDto.setUserName("adminUser");
        adminDto.setPassword("adminPassword");
        adminDto.setRole(RoleType.ADMIN);

        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedAdminPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        User result = userService.registerAdminUser(adminDto);

        // Assert
        assertNotNull(result);
        assertEquals("adminUser", result.getUserName());
        assertEquals("encodedAdminPassword", result.getPassword());
        assertEquals(RoleType.ADMIN, result.getRole());
    }
}