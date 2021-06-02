package com.example.mimimi.service;

import com.example.mimimi.dto.UserDto;
import com.example.mimimi.repos.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ConverterService converterService;

    public UserService(UserRepository userRepository, ConverterService converterService) {
        this.userRepository = userRepository;
        this.converterService = converterService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found"));
    }

    public boolean ifUserExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public void saveUser(UserDto userDto) {
        userRepository.save(converterService.convertToEntity(userDto));
    }
}
