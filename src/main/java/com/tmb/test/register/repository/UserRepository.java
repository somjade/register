package com.tmb.test.register.repository;

import com.tmb.test.register.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findUsersByEmailAndPassword(String email, String hashPassword);
    Optional<UserEntity> findUsersByUserIdAndRefCode(Integer userId, String refCode);
}
