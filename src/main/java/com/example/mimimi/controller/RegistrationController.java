package com.example.mimimi.controller;

import com.example.mimimi.dto.UserDto;
import com.example.mimimi.entity.Role;
import com.example.mimimi.entity.User;
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
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Please fill out all fields!");
            return "registration";
        }
//        if (userDto.getUsername().isEmpty() || userDto.getPassword().isEmpty()) {
//            model.addAttribute("message", "Please fill out all fields!");
//            return "registration";
//        }
        else {
            User userFromDb = userRepository.findByUsername(userDto.getUsername());
            if (userFromDb != null) {
                model.addAttribute("usernameError", "User " + userDto.getUsername() + " already exists!");
                return "registration";
            }
            userDto.setRoles(Collections.singleton(Role.USER));
            userRepository.save(converterService.convertToEntity(userDto));
            return "redirect:login";
        }
    }

}
