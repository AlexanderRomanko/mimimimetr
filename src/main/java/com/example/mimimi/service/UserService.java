package com.example.mimimi.service;

import com.example.mimimi.dto.UserDto;
import com.example.mimimi.entity.User;
import com.example.mimimi.repos.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
