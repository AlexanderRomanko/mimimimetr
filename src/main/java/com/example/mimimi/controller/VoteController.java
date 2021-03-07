package com.example.mimimi.controller;

import com.example.mimimi.domain.Cat;
import com.example.mimimi.repos.CatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("vote")
public class VoteController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private CatRepository catRepository;

    @GetMapping("/chooseCollection")
    public String showCollectionsList(Model model) {
        List<Cat> collectionsList = catRepository.findAllTags();
        model.addAttribute("collectionsList", collectionsList);
        System.out.println(collectionsList);
        return "chooseVoteCollection";
    }

    @GetMapping("/{tag}")
    public String vote(@PathVariable String tag, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object pricipal = auth.getPrincipal();
        List<Cat> cats = catRepository.findByTag(tag);
        cats.get(0).getVotedUsers().add("author");
        if (cats.get(0).getVotedUsers().contains(pricipal)) {
            return "vote";
        } else {
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
            return "vote";
        }

    }
    @PostMapping("/{tag}")
    public String vote(@PathVariable String tag, @RequestParam Cat cats, Model model) {
        return "vote";
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
