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

    private final UserRepository userRepository;

    public RegistrationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("registration")
    public String addUser(User user, Model model) {
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            model.addAttribute("message", "Please fill out all fields!");
            return "registration";
        }
        else {
            User userFromDb = userRepository.findByUsername(user.getUsername());
            if (userFromDb != null) {
                model.addAttribute("message", "User " + user.getUsername() + " already exists!");
                return "registration";
            }
            user.setRoles(Collections.singleton(Role.USER));
//            user.setRoles(Collections.singleton(Role.ADMIN));
            userRepository.save(user);
            return "redirect:login";
        }
    }

}
