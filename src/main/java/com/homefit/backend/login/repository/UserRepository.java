package com.homefit.backend.login.repository;

import com.homefit.backend.login.entity.RoleType;
import com.homefit.backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByRole(RoleType role);
}