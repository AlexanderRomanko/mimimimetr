package com.example.mimimi.controller;

import com.example.mimimi.dto.UserDto;
import com.example.mimimi.entity.Role;
import com.example.mimimi.repos.UserRepository;
import com.example.mimimi.service.ConverterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Collections;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserRepository userRepository;
    private final ConverterService converterService;

    public RegistrationController(UserRepository userRepository, ConverterService converterService) {
        this.userRepository = userRepository;
        this.converterService = converterService;
    }

    @GetMapping
    public String registration() {
        return "registration";
    }

    @PostMapping
    public String addUser(@Valid UserDto userDto, BindingResult bindingResult, Model model) {
        String message;
        if (bindingResult.hasErrors()) {
            message = "Please fill out all fields!";
            model.addAttribute("message", message);
            return "registration";
        }
        if (userRepository.findUserByUsername(userDto.getUsername()).isPresent()) {
            message = "User " + userDto.getUsername() + " already exists!";
            model.addAttribute("message", message);
            return "registration";
        }
        userDto.setRoles(Collections.singleton(Role.USER));
        userRepository.save(converterService.convertToEntity(userDto));
        return "redirect:login";
    }
}
