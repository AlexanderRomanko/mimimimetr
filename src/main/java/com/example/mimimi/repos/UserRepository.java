package com.example.mimimi.repos;

import com.example.mimimi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); //todo Optional
}
