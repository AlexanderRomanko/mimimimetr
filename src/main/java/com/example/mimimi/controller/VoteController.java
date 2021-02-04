package com.example.mimimi.controller;

import com.example.mimimi.domain.Cat;
import com.example.mimimi.repos.CatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class VoteController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private CatRepository catRepository;

    @GetMapping
    public String collection(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
        Iterable<Cat> cats;
        if (filter != null && !filter.isEmpty()) cats = catRepository.findByTag(filter);
        else cats = catRepository.findAll();
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("cats", cats);
        model.addAttribute("filter", filter);
        return "collection";
    }

//    @PostMapping
//    public String addCollection(@RequestParam String tag, @RequestParam String name, @RequestParam("file") MultipartFile file,
//                                Model model) throws IOException {
//        if (tag.isEmpty() || name.isEmpty() || file == null || file.getOriginalFilename().isEmpty()) {
//            model.addAttribute("error", "Please, fill out all the fields and choose a file!");
//        } else {
//            Cat cat = new Cat(name, tag);
//            File uploadDir = new File(uploadPath);
//            if (!uploadDir.exists()) uploadDir.mkdir();
//            String uuidFile = UUID.randomUUID().toString();
//            String resultFileName = uuidFile + "." + file.getOriginalFilename();
//            file.transferTo(new File(uploadPath + "/" + resultFileName));
//            cat.setFilename(resultFileName);
//            catRepository.save(cat);
//        }
//        Iterable<Cat> cats = catRepository.findAll();
//        model.addAttribute("cats", cats);
//        return "collection";
//    }

}
