package com.example.mimimi.controller;

import com.example.mimimi.entity.ComparableElement;
import com.example.mimimi.repos.ComparableElementRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private ComparableElementRepository comparableElementRepository;

    @GetMapping("/chooseCollection")
    public String showCollectionsList(Model model) throws IOException {
        File upload = new File(uploadPath);
        for (String file : upload.list()) {
            File dir = new File(uploadPath + "/" + file);
            if (dir.list().length % 2 > 0) {
                FileUtils.deleteDirectory(dir);
                comparableElementRepository.deleteByTag(file);
            }
        }
        List<String> tags = new ArrayList<>();
        comparableElementRepository.findDistinctCatsWithDistinctTags().forEach(x -> tags.add(x.getTag()));
        for (String tag : tags) {
            if (comparableElementRepository.findByTag(tag).size() % 2 > 0) {
                File uploadDir = new File(uploadPath + "/" + tag);
                FileUtils.deleteDirectory(uploadDir);
                comparableElementRepository.deleteByTag(tag);
            }
        }
//        for (String tag : tags) {
//            List<Cat> collectionTemp = catRepository.findByTag(tag);
//            if (collectionTemp.size() % 2 > 0) {
//                for (Cat cat :
//                        collectionTemp) {
//                    catRepository.deleteById(cat.getId());
//                }
//            }
//        }
        model.addAttribute("collectionsList", comparableElementRepository.findDistinctCatsWithDistinctTags());
        return "chooseVoteCollection";
    }

    @GetMapping("/{tag}")
    public String vote(@PathVariable String tag, Model model) {
        List<ComparableElement> comparableElements = comparableElementRepository.findByTag(tag);
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();//if it's better to move this field up
        comparableElements.removeIf(comparableElement -> comparableElement.getVotedUsers().contains(principal));
        if (comparableElements.isEmpty()) return "redirect:/vote/{tag}/results";
        if (!comparableElements.get(0).getVotedUsers().contains(principal)) {
            Random random = new Random();
            List<ComparableElement> twoComparableElements = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                int randomIndex = random.nextInt(comparableElements.size());
                twoComparableElements.add(comparableElements.get(randomIndex));
                comparableElements.remove(randomIndex);
            }
            model.addAttribute("comparableElement1", twoComparableElements.get(0));
            model.addAttribute("comparableElement2", twoComparableElements.get(1));
        }
        return "vote";
    }

    @PostMapping("/{tag}")
    public String vote(@PathVariable String tag, @RequestParam("comparableElement1Id") ComparableElement comparableElement1, @RequestParam("comparableElement2Id") ComparableElement comparableElement2,
                       @RequestParam(required = false) String button1) {
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        if (button1 != null) comparableElement1.setLikes(comparableElement1.getLikes() + 1);
        else comparableElement2.setLikes(comparableElement2.getLikes() + 1);
        comparableElement1.getVotedUsers().add(principal);
        comparableElement2.getVotedUsers().add(principal);
        comparableElementRepository.save(comparableElement1);
        comparableElementRepository.save(comparableElement2);
        return "redirect:/vote/{tag}";
    }

    @GetMapping("{tag}/results")
    public String showResults(@PathVariable String tag, Model model) {
        ArrayList<ComparableElement> votedList = new ArrayList<>(2);
        votedList.addAll(comparableElementRepository.findByTagOrderByLikesDesc(tag));
        votedList.trimToSize();
        model.addAttribute("votedList", votedList);
        return "results";
    }

}
