package com.example.mimimi.controller;

import com.example.mimimi.entity.ComparableElement;
import com.example.mimimi.repos.ComparableElementRepository;
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
//@RequestMapping("/collection")
public class CollectionController {

    @Value("${upload.path}")
    private String uploadPath;

    HashMap<String, String> collectionTemp = new HashMap<>();/*??replace????????????????*/

    private final ComparableElementRepository comparableElementRepository;

    public CollectionController(ComparableElementRepository comparableElementRepository) {
        this.comparableElementRepository = comparableElementRepository;
    }

    @GetMapping("/vote/chooseCollection")
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
        model.addAttribute("collectionsList", comparableElementRepository.findDistinctCatsWithDistinctTags());
        return "chooseVoteCollection";
    }

    @GetMapping("/collection")
    public String index(@RequestParam(required = false, defaultValue = "") String tag) {
        if (tag.isEmpty()) return "collection";
        return "collectionEdit";
    }

    @PostMapping("/collection")
    public String indexCollection(@RequestParam String tag, Model model) throws IOException {
        if (tag.isEmpty()) return "collection";
        ComparableElement comparableElement = comparableElementRepository.findFirstByTag(tag);
        if (comparableElement != null) {
            model.addAttribute("error", "Collection exists, please choose another name.");
            return "collection";
        }
        File uploadDir = new File(uploadPath + "/" + tag);
        FileUtils.deleteDirectory(uploadDir);
        collectionTemp.clear();/*??????????????????????*/
        return "redirect:/collection/" + tag;

    }

    @GetMapping("/collection/{tag}")
    public String createCollection(@PathVariable String tag, @RequestParam(required = false) String redirectError, Model model) {
        if (redirectError != null) model.addAttribute("redirectError", redirectError);
        if (!collectionTemp.isEmpty()) model.addAttribute("collectionTemp", collectionTemp);
        return "collectionEdit";
    }

    @PostMapping("/collection/{tag}")
    public String editCollection(@PathVariable String tag, @RequestParam String name, @RequestParam("file") MultipartFile file,
                                 Model model) throws IOException {
        File uploadDir = new File(uploadPath + "/" + tag);
        String filename = file.getOriginalFilename();
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        if (new File(uploadPath + "/" + tag + "/" + filename).isFile()) {
            model.addAttribute("collectionTemp", collectionTemp);
            model.addAttribute("error", "File already exists, choose another.");
            model.addAttribute("name", name);
            return "collectionEdit";
        }
        file.transferTo(new File(uploadPath + "/" + tag + "/" + filename));
        collectionTemp.put(filename, name);
        model.addAttribute("collectionTemp", collectionTemp);
        return "collectionEdit";
    }

    @PostMapping("/collection/{tag}/remove")
    public String deleteCat(@PathVariable String tag, @RequestParam("comparableElementKey") String comparableElementKey) throws IOException {
        File file = new File(uploadPath + "/" + tag + "/" + comparableElementKey);
        file.delete();
        File uploadDir = new File(uploadPath + "/" + tag);
        if (Objects.requireNonNull(uploadDir.list()).length == 0) FileUtils.deleteDirectory(uploadDir);
        collectionTemp.remove(comparableElementKey);
        return "redirect:/collection/{tag}";
    }

    @PostMapping("/collection/{tag}/save")
    public String saveCollection(@PathVariable String tag, RedirectAttributes redirectError, Model model) {
        if (collectionTemp.size() == 0) return "redirect:/collection/{tag}";
        if (collectionTemp.size() % 2 > 0) {
            redirectError.addAttribute("redirectError",
                    "The number of elements to compare in the collection must be a multiple of two. Please add or remove an item.");
            return "redirect:/collection/{tag}";
        }
        for (Map.Entry mapElement : collectionTemp.entrySet()) {
            ComparableElement comparableElement = new ComparableElement(mapElement.getValue().toString(), tag, mapElement.getKey().toString());
            comparableElementRepository.save(comparableElement);
        }
        collectionTemp.clear();
        return "redirect:/vote/chooseCollection";
    }
}

