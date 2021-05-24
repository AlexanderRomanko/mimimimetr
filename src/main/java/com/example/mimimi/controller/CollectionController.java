package com.example.mimimi.controller;

import com.example.mimimi.entity.Coll;
import com.example.mimimi.service.CollectionService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class CollectionController {

    @Value("${upload.path}")
    private String uploadPath;

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/collection")
    public String index() {
        return "collection";
    }

    @PostMapping("/collection")
    public String indexCollection(@RequestParam String collName, Model model) {
        if (collName.isEmpty()) return "collection";
        else if (collectionService.collectionExists(collName)) {
            model.addAttribute("error", "Collection exists, please choose another name");
            return "collection";
        } else {
            collectionService.createCollection(collName);
            return "redirect:/collection/" + collectionService.getCollection(collName).getId();
        }
    }

    @GetMapping("/collection/{coll}")
    public String editCollection(@PathVariable Coll coll, @RequestParam(required = false) String redirectError, Model model) {
        if (redirectError != null) model.addAttribute("redirectError", redirectError);
        model.addAttribute("comparableElements", coll.getComparableElementList());
        return "collectionEdit";
    }

    @PostMapping("/collection/{coll}")
    public String editCollection(@PathVariable Coll coll, @RequestParam String name, @RequestParam("file") MultipartFile file,
                                 Model model) {
        if (new File(uploadPath + "/" + coll.getName() + "/" + file.getOriginalFilename()).isFile()) {
            model.addAttribute("error", "File already exists, choose another.");
            model.addAttribute("name", name);
        } else {
            collectionService.createNewComparableElement(name, file, coll.getName());
        }
        model.addAttribute("comparableElements", coll.getComparableElementList());
        return "collectionEdit";
    }

    @PostMapping("/collection/{coll}/remove")
    public String deleteElement(@PathVariable Coll coll, @RequestParam String filename) {
        collectionService.remove(coll, filename);
        return "redirect:/collection/{coll}";
    }

    @PostMapping("/collection/{coll}/save")
    public String saveCollection(@PathVariable Coll coll, RedirectAttributes redirectError) {
        if (coll.getComparableElementList().size() == 0)
            return "redirect:/collection/{coll}";
        if (coll.getComparableElementList().size() % 2 > 0) {
            redirectError.addAttribute("redirectError",
                    "The number of elements to compare must be a multiple of two.");
            return "redirect:/collection/{coll}";
        }
        return "redirect:/vote/chooseCollection";
    }

    @PostMapping("/collection/delete")
    public String deleteWrongCollections() {
        try {
            collectionService.deleteWrongCollections();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/collection";
    }

    @GetMapping("/vote/chooseCollection")
    public String showCollectionsList(Model model) {
        List<Coll> collList = (List<Coll>) collectionService.getCollectionsList();
        if (collList.size() == 0) return "redirect:/main";
        model.addAttribute("collectionsList", collectionService.getCollectionsList());
        return "chooseVoteCollection";
    }
}

