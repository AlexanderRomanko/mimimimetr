package com.example.mimimi.controller;

import com.example.mimimi.domain.Cat;
import com.example.mimimi.repos.CatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("collection")
public class CollectionController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private CatRepository catRepository;

    @GetMapping
    public String index(@RequestParam(required = false, defaultValue = "") String tag) {
        if (tag.isEmpty()) return "collection";
        return "collectionEdit";
    }

    @PostMapping
    public String indexCollection(@RequestParam String tag, Model model) {
        if (tag.isEmpty()) return "collection";
        List<Cat> cats = catRepository.findByTag(tag);
        if (!cats.isEmpty()) {
            model.addAttribute("error", "Такая коллекция уже существует, выберите другое название.");
            return "collection";
        }
        model.addAttribute("cats", cats);
        return "redirect:/collection/" + tag;

    }

    @GetMapping("{tag}")
    public String createCollection(@PathVariable String tag, Model model) {
        List<Cat> cats = catRepository.findByTag(tag);
        model.addAttribute("cats", cats);
        return "collectionEdit";
    }

    @PostMapping("{tag}")
    public String editCollection(@RequestParam String name, @RequestParam("file") MultipartFile file,
                       Model model, @PathVariable String tag) throws IOException {
        File uploadDir = new File(uploadPath + "/" + tag);
        if (!uploadDir.exists()) uploadDir.mkdir();
        String filename = file.getOriginalFilename();
        if(new File(uploadPath + "/" + tag + "/" + filename).isFile()) {
            List<Cat> cats = catRepository.findByTag(tag);
            model.addAttribute("cats", cats);
            model.addAttribute("error", "Такой кот уже есть в коллекции, выберите другой файл.");
            model.addAttribute("name", name);
            model.addAttribute("tag", tag);
            return "collectionEdit";
        }
        file.transferTo(new File(uploadPath + "/" + tag + "/" + filename));
        Cat cat = new Cat(name, tag, filename);
        catRepository.save(cat);
        List<Cat> cats = catRepository.findByTag(tag);
        model.addAttribute("cats", cats);
        model.addAttribute("tag", tag);
        return "collectionEdit";
    }

    @PostMapping("{tag}/remove")
    public String deleteCat(@PathVariable String tag, @RequestParam("catId") Cat cat) {
        catRepository.delete(cat);
        File file = new File(uploadPath + "/" + tag + "/" + cat.getFilename());
        file.delete();
        File uploadDir = new File(uploadPath + "/" + tag);
        if (uploadDir.list().length == 0) uploadDir.delete();
        return "redirect:/collection/" + tag;
    }

}
