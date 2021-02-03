package com.example.mimimi.controller;

import com.example.mimimi.domain.Role;
import com.example.mimimi.domain.User;
import com.example.mimimi.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("registration")
    public String addUser(User user, Model model) {
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            model.addAttribute("message", "Пожалуйста, заполните все поля");
            return "registration";
        }
        else {
            User userFromDb = userRepository.findByUsername(user.getUsername());
            if (userFromDb != null) {
                model.addAttribute("message", "Пользователь " + user.getUsername() + " уже существует!");
                return "registration";
            }
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            return "redirect:login";
        }
    }

}
