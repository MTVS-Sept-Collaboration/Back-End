package com.homefit.backend.user.service;

import com.homefit.backend.login.entity.User;
import com.homefit.backend.login.repository.UserRepository;
import com.homefit.backend.user.dto.UserInfoDto;
import com.homefit.backend.user.dto.UserPhysicalInfoDto;
import com.homefit.backend.user.entity.UserInfo;
import com.homefit.backend.user.repository.UserInfoRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserInfoServiceTest {

    @InjectMocks
    private UserInfoService userInfoService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userInfoService.setTestMode(true); // 테스트 모드 활성화(이미 인증이 되어 있다고 가정)
    }

    /**
     * ### User Info 조회/수정 테스트 ###
     */
    @DisplayName(value = "#01. 사용자 정보 조회 성공 테스트")
    @Test
    @Order(1)
    void getUserInfo_Success() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        UserInfo userInfo = new UserInfo(1L, user, "NickName", LocalDate.of(1990, 1, 1), 180.0, 75.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userInfoRepository.findByUser(user)).thenReturn(Optional.of(userInfo));

        UserInfoDto result = userInfoService.getUserInfo(userId);

        assertNotNull(result);
        assertEquals("NickName", result.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getBirthday());
        assertEquals(180.0, result.getHeight());
        assertEquals(75.0, result.getWeight());
    }

    @DisplayName(value = "#02. 사용자 정보 조회 실패 테스트")
    @Test
    @Order(2)
    void getUserInfo_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userInfoService.getUserInfo(userId));
    }

    @DisplayName(value = "#03. 사용자 생년월일 변경 성공 테스트")
    @Test
    @Order(3)
    void updateBirthday_Success() {
        Long userId = 1L;
        LocalDate newBirthday = LocalDate.of(1990, 1, 1);
        User user = User.builder().id(userId).build();
        UserInfo userInfo = new UserInfo(1L, user, "Nickname", LocalDate.of(1989, 1, 1), 180.0, 75.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userInfoRepository.findByUser(user)).thenReturn(Optional.of(userInfo));

        userInfoService.updateBirthday(userId, newBirthday);

        verify(userInfoRepository).save(any(UserInfo.class));
        assertEquals(newBirthday, userInfo.getBirthday());
    }

    @DisplayName(value = "#04. 사용자 신체정보 변경 성공 테스트")
    @Test
    @Order(4)
    void updateUserPhysicalInfo_Success() {
        Long userId = 1L;
        UserPhysicalInfoDto dto = new UserPhysicalInfoDto(180.0, 75.0);
        User user = User.builder().id(userId).build();
        UserInfo userInfo = new UserInfo(1L, user, "Nickname", LocalDate.of(1990, 1, 1), 175.0, 70.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userInfoRepository.findByUser(user)).thenReturn(Optional.of(userInfo));

        userInfoService.updateUserPhysicalInfo(userId, dto);

        verify(userInfoRepository).save(any(UserInfo.class));
        assertEquals(180.0, userInfo.getHeight());
        assertEquals(75.0, userInfo.getWeight());
    }

    @DisplayName(value = "#05. 사용자 정보 일괄 수정 성공 테스트")
    @Test
    @Order(5)
    void updateUserInfo_Success() {
        // Arrange
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        UserInfo originalUserInfo = new UserInfo(1L, user, "OldNickName", LocalDate.of(1990, 1, 1), 175.0, 70.0);

        UserInfoDto updateDto = UserInfoDto.builder()
                .nickName("NewNickName")
                .birthday(LocalDate.of(1991, 2, 2))
                .height(180.0)
                .weight(75.0)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userInfoRepository.findByUser(user)).thenReturn(Optional.of(originalUserInfo));
        when(userInfoRepository.save(any(UserInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userInfoService.updateUserInfo(userId, updateDto);

        // Assert
        verify(userInfoRepository).save(any(UserInfo.class));

        assertEquals("NewNickName", originalUserInfo.getNickName());
        assertEquals(LocalDate.of(1991, 2, 2), originalUserInfo.getBirthday());
        assertEquals(180.0, originalUserInfo.getHeight());
        assertEquals(75.0, originalUserInfo.getWeight());

        // BMI 계산 확인
        double expectedBmi = 75.0 / Math.pow(1.80, 2);
        assertEquals(expectedBmi, originalUserInfo.getBmi(), 0.01);  // 0.01은 허용 오차
    }

    @DisplayName(value = "#06. 사용자 정보 일괄 수정 - 부분 업데이트 테스트")
    @Test
    @Order(6)
    void updateUserInfo_PartialUpdate_Success() {
        // Arrange
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        UserInfo originalUserInfo = new UserInfo(1L, user, "OldNickName", LocalDate.of(1990, 1, 1), 175.0, 70.0);

        UserInfoDto updateDto = UserInfoDto.builder()
                .nickName("NewNickName")
                .height(180.0)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userInfoRepository.findByUser(user)).thenReturn(Optional.of(originalUserInfo));
        when(userInfoRepository.save(any(UserInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        userInfoService.updateUserInfo(userId, updateDto);

        // Assert
        verify(userInfoRepository).save(any(UserInfo.class));

        assertEquals("NewNickName", originalUserInfo.getNickName());
        assertEquals(LocalDate.of(1990, 1, 1), originalUserInfo.getBirthday());  // 변경되지 않아야 함
        assertEquals(180.0, originalUserInfo.getHeight());
        assertEquals(70.0, originalUserInfo.getWeight());  // 변경되지 않아야 함

        // BMI 계산 확인
        double expectedBmi = 70.0 / Math.pow(1.80, 2);
        assertEquals(expectedBmi, originalUserInfo.getBmi(), 0.01);  // 0.01은 허용 오차
    }
}