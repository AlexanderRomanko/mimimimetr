package com.example.mimimi.controller;

import com.example.mimimi.entity.Cat;
import com.example.mimimi.repos.CatRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/collection")
public class CollectionController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private CatRepository catRepository;

    HashMap<String, String> catsTemp = new HashMap<>();

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
        catsTemp.clear();
        return "redirect:/collection/" + tag;

    }

    @GetMapping("{tag}")
    public String createCollection(@PathVariable String tag, @RequestParam(required = false) String redirectError, Model model) {
        model.addAttribute("redirectError", redirectError);
        model.addAttribute("cats", catsTemp);
        return "collectionEdit";
    }

    @PostMapping("{tag}")
    public String editCollection(@PathVariable String tag, @RequestParam String name, @RequestParam("file") MultipartFile file,
                                 Model model) throws IOException {
        File uploadDir = new File(uploadPath + "/" + tag);
        String filename = file.getOriginalFilename();
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        if (new File(uploadPath + "/" + tag + "/" + filename).isFile()) {
            model.addAttribute("cats", catsTemp);
            model.addAttribute("error", "File already exists, choose another.");
            model.addAttribute("name", name);
            return "collectionEdit";
        }
        file.transferTo(new File(uploadPath + "/" + tag + "/" + filename));
        catsTemp.put(filename, name);
        model.addAttribute("cats", catsTemp);
        return "collectionEdit";
    }

    @PostMapping("{tag}/remove")
    public String deleteCat(@PathVariable String tag, @RequestParam("catKey") String catKey) throws IOException {
        File file = new File(uploadPath + "/" + tag + "/" + catKey);
        file.delete();
        File uploadDir = new File(uploadPath + "/" + tag);
        if (Objects.requireNonNull(uploadDir.list()).length == 0) FileUtils.deleteDirectory(uploadDir);
        catsTemp.remove(catKey);
        return "redirect:/collection/{tag}";
    }

    @PostMapping("{tag}/save")
    public String saveCollection(@PathVariable String tag, RedirectAttributes redirectError, Model model) {
        if (catsTemp.size() % 2 > 0) {
            redirectError.addAttribute("redirectError",
                    "The number of elements to compare in the collection must be a multiple of two. Please add or remove an item.");
            return "redirect:/collection/{tag}";
        }
        for (Map.Entry mapElement : catsTemp.entrySet()) {
            Cat cat = new Cat(mapElement.getValue().toString(), tag, mapElement.getKey().toString());
            catRepository.save(cat);
        }
        catsTemp.clear();
        return "redirect:/vote/chooseCollection";
    }
}

