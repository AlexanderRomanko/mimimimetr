package com.example.mimimi.repos;

import com.example.mimimi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Optional<User> findUserByUsername(String username);
}
