package com.example.mimimi.controller;

import com.example.mimimi.domain.Cat;
import com.example.mimimi.repos.CatRepository;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
//@RequestMapping("collection")
public class CollectionController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private CatRepository catRepository;

    @GetMapping("collection")
    public String index(@RequestParam(required = false, defaultValue = "") String tag, Model model) {
        if (tag.isEmpty()) return "collection";
        List<Cat> cats = catRepository.findByTag(tag);
        if (!cats.isEmpty()) {
            model.addAttribute("error", "Такая коллекция уже существует, выберите другое название.");
            return "collection";
        }
        model.addAttribute("tag", tag);
        model.addAttribute("cats", cats);
        return "collectionEdit";
    }

    @PostMapping("collection")
    public String create(@RequestParam String tag, Model model) {
        List<Cat> cats = catRepository.findByTag(tag);
        model.addAttribute("cats", cats);
        model.addAttribute("tag", tag);
        System.out.println(tag);
        return "redirect:/collection/" + tag;

    }

    @GetMapping("collection/{tag}")
    public String createC(@PathVariable String tag, Model model) {
        List<Cat> cats = catRepository.findByTag(tag);
        model.addAttribute("cats", cats);
        model.addAttribute("tag", tag);
        return "collectionEdit";
    }

    @PostMapping("collection/{tag}")
    public String edit(@RequestParam String name, @RequestParam("file") MultipartFile file,
                       Model model, @RequestParam String tag) throws IOException {
        Cat cat = new Cat(name, tag);
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = uuidFile + "." + file.getOriginalFilename();
        file.transferTo(new File(uploadPath + "/" + resultFileName));
        cat.setFilename(resultFileName);
        catRepository.save(cat);
        List<Cat> cats = catRepository.findByTag(tag);
        model.addAttribute("cats", cats);
        model.addAttribute("tag", tag);
        return "collectionEdit";
    }

}
