package com.example.mimimi.service;

import com.example.mimimi.dto.CollDto;
import com.example.mimimi.dto.ComparableElementDto;
import com.example.mimimi.dto.UserDto;
import com.example.mimimi.entity.Coll;
import com.example.mimimi.entity.ComparableElement;
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

    public CollDto convertToDto(Coll coll) {
        return modelMapper.map(coll, CollDto.class);
    }

    public Coll convertToEntity(CollDto collDto) {
        return modelMapper.map(collDto, Coll.class);
    }

    public ComparableElementDto convertToDto(ComparableElement comparableElement) {
        return modelMapper.map(comparableElement, ComparableElementDto.class);
    }

    public ComparableElement convertToEntity(ComparableElementDto comparableElementDto) {
        return modelMapper.map(comparableElementDto, ComparableElement.class);
    }
}
