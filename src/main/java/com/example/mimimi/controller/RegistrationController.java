package com.example.mimimi.controller;

import com.example.mimimi.entity.Role;
import com.example.mimimi.entity.User;
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
    public String addUser(User user, Model model) { //todo dont use Entity in outside, create DTO and DTO mapper
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) { //TODO we can use spring dto validation https://habr.com/ru/post/343960/
            model.addAttribute("message", "Please fill out all fields!");
            return "registration";
        }
        else {
            User userFromDb = userRepository.findByUsername(user.getUsername()); //todo try to create and catch exception
            if (userFromDb != null) {
                model.addAttribute("message", "User " + user.getUsername() + " already exists!");
                return "registration";
            }
            user.setActive(true);
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            return "redirect:login";
        }
    }

}
