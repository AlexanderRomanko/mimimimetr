package com.example.mimimi.service;

import com.example.mimimi.dto.UserDto;
import com.example.mimimi.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ConverterService {

    private final ModelMapper modelMapper;

    public ConverterService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

}
