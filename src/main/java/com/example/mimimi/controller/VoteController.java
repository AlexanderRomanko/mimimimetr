package com.example.mimimi.controller;

import com.example.mimimi.domain.Cat;
import com.example.mimimi.repos.CatRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("vote")
public class VoteController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private CatRepository catRepository;

    @GetMapping("/chooseCollection")
    public String showCollectionsList(Model model) throws IOException {
        File upload = new File(uploadPath);
        for (String file : upload.list()) {
            File dir = new File(uploadPath + "/" + file);
            if (dir.list().length % 2 > 0) {
                FileUtils.deleteDirectory(dir);
                catRepository.deleteByTag(file);
            }
        }
        List<String> tags = new ArrayList<>();
        catRepository.findDistinctCatsWithDistinctTags().forEach(x -> tags.add(x.getTag()));
        for (String tag : tags) {
            if (catRepository.findByTag(tag).size() % 2 > 0) {
                File uploadDir = new File(uploadPath + "/" + tag);
                FileUtils.deleteDirectory(uploadDir);
                catRepository.deleteByTag(tag);
            }
        }
//        for (String tag : tags) {
//            List<Cat> cats = catRepository.findByTag(tag);
//            if (cats.size() % 2 > 0) {
//                for (Cat cat :
//                        cats) {
//                    catRepository.deleteById(cat.getId());
//                }
//            }
//        }
        model.addAttribute("collectionsList", catRepository.findDistinctCatsWithDistinctTags());
        return "chooseVoteCollection";
    }

    @GetMapping("/{tag}")
    public String vote(@PathVariable String tag, Model model) {
        List<Cat> cats = catRepository.findByTag(tag);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        System.out.println(principal);
        cats.removeIf(cat -> cat.getVotedUsers().contains(principal));
        if (!cats.get(0).getVotedUsers().contains(principal)) {
            Random random = new Random();
            List<Cat> twoCats = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                int randomIndex = random.nextInt(cats.size());
                twoCats.add(cats.get(randomIndex));
                cats.remove(randomIndex);
            }

//        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
            model.addAttribute("cats", cats);
            model.addAttribute("cat1", twoCats.get(0));
            model.addAttribute("cat2", twoCats.get(1));
//        model.addAttribute("filter", tag);
        }
        return "vote";

    }
//    @PostMapping("/{tag}")
//    public String vote(@PathVariable String tag, @RequestParam List cats, Model model) {
//        if (cats.isEmpty()) return "redirect:/vote/{tag}/results";
//        Random random = new Random();
//        List<Cat> twoCats = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            int randomIndex = random.nextInt(cats.size());
//            twoCats.add(cats.get(randomIndex));
//            cats.remove(randomIndex);
//        }
//        model.addAttribute("cats", cats);
//        model.addAttribute("cat1", twoCats.get(0));
//        model.addAttribute("cat2", twoCats.get(1));
//        return "vote";
//    }

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
