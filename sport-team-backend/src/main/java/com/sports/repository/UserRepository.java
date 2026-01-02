package com.sports.repository;

import com.sports.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByStuId(String stuId);
    Optional<User> findByPhoneNum(String phoneNum);
}
