package com.homefit.backend.user.repository;

import com.homefit.backend.login.entity.User;
import com.homefit.backend.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUser(User user);
}
