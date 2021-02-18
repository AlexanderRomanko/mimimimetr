package com.example.mimimi.controller;

import com.example.mimimi.domain.Cat;
import com.example.mimimi.domain.MimimiCollections;
import com.example.mimimi.repos.CatRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    public String indexCollection(@RequestParam String tag, Model model) throws IOException {
        if (tag.isEmpty()) return "collection";
        List<Cat> cats = catRepository.findByTag(tag);
        if (!cats.isEmpty()) {
            model.addAttribute("error", "Collection exists, please choose another name.");
            return "collection";
        }
        File uploadDir = new File(uploadPath + "/" + tag);
        FileUtils.deleteDirectory(uploadDir);
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
        String filename = file.getOriginalFilename();
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        if(new File(uploadPath + "/" + tag + "/" + filename).isFile()) {
            List<Cat> cats = catRepository.findByTag(tag);
            model.addAttribute("cats", cats);
            model.addAttribute("error", "File already exists, choose another.");
            model.addAttribute("name", name);
            model.addAttribute("tag", tag);
            return "collectionEdit";
        }
        file.transferTo(new File(uploadPath + "/" + tag + "/" + filename));
        File root = new File(uploadPath);

//        System.out.println(catRepository.findAllTags());
//        System.out.println(Arrays.toString(root.list()));
//        MimimiCollections.collectionsMap.put(tag, filename);
        Cat cat = new Cat(name, tag, filename, false);
        catRepository.save(cat);
        List<Cat> cats = catRepository.findByTag(tag);
        model.addAttribute("cats", cats);
        model.addAttribute("tag", tag);
        return "collectionEdit";
    }

    @PostMapping("{tag}/remove")
    public String deleteCat(@PathVariable String tag, @RequestParam("catId") Cat cat) throws IOException {
        File file = new File(uploadPath + "/" + tag + "/" + cat.getFilename());
        file.delete();
        File uploadDir = new File(uploadPath + "/" + tag);
        if (uploadDir.list().length == 0) FileUtils.deleteDirectory(uploadDir);
        catRepository.delete(cat);
        MimimiCollections.collectionsMap.remove(tag);
        return "redirect:/collection/" + tag;
    }

}
