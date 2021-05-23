package com.example.mimimi.controller;

import com.example.mimimi.entity.Coll;
import com.example.mimimi.entity.ComparableElement;
import com.example.mimimi.service.CollectionService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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
public class CollectionController {

    @Value("${upload.path}")
    private String uploadPath;

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/vote/chooseCollection")
    public String showCollectionsList(Model model) {
//        try { // move try/catch somewhere else????????
//            collectionService.removeOddNumberOfFilesCollections();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        List<Coll> collList = (List<Coll>) collectionService.getCollectionsList();
        if (collList.size() == 0) return "redirect:/main";
        model.addAttribute("collectionsList", collectionService.getCollectionsList());
        return "chooseVoteCollection";
    }

    @GetMapping("/collection")
    public String index(@RequestParam(required = false, defaultValue = "") String collName) {
        if (collName.isEmpty()) return "collection";
        return "collectionEdit";
    }

    @PostMapping("/collection")
    public String indexCollection(@RequestParam String collName, Model model) throws IOException {
        if (collName.isEmpty()) return "collection";
        if (collectionService.collectionExists(collName)) {
            model.addAttribute("error", "Collection exists, please choose another name");
            return "collection";
        }
        File uploadDir = new File(uploadPath + "/" + collName);
        FileUtils.deleteDirectory(uploadDir);
        collectionService.createCollection(collName);
        return "redirect:/collection/" + collName;
    }

    @GetMapping("/collection/{collName}")
    public String editCollection(@PathVariable String collName, @RequestParam(required = false) String redirectError, Model model) {
        if (redirectError != null) model.addAttribute("redirectError", redirectError);
        model.addAttribute("comparableElements", collectionService.getCollection(collName).getComparableElementList());
        return "collectionEdit";
    }

    @PostMapping("/collection/{collName}")
    public String editCollection(@PathVariable String collName, @RequestParam String name, @RequestParam("file") MultipartFile file,
                                 Model model) throws IOException {
        if (new File(uploadPath + "/" + collName + "/" + file.getOriginalFilename()).isFile()) {
            model.addAttribute("error", "File already exists, choose another.");
            model.addAttribute("name", name);
        } else {
            file.transferTo(new File(uploadPath + "/" + collName + "/" + file.getOriginalFilename()));
            collectionService.createNewComparableElement(name, file.getOriginalFilename(), collName);
        }
        model.addAttribute("comparableElements", collectionService.getCollection(collName).getComparableElementList());
        return "collectionEdit";
    }

    @PostMapping("/collection/{collName}/remove")
    public String deleteElement(@PathVariable String collName, @RequestParam String filename) {
        File file = new File(uploadPath + "/" + collName + "/" + filename);
        file.delete();
//        File uploadDir = new File(uploadPath + "/" + collName);
//        if (Objects.requireNonNull(uploadDir.list()).length == 0) FileUtils.deleteDirectory(uploadDir);
        collectionService.remove(filename);
        return "redirect:/collection/{collName}";
    }

    @PostMapping("/collection/{collName}/save")
    public String saveCollection(@PathVariable String collName, RedirectAttributes redirectError) {
        if (collectionService.getCollection(collName).getComparableElementList().size() == 0)
            return "redirect:/collection/{collName}";
        if (collectionService.getCollection(collName).getComparableElementList().size() % 2 > 0) {
            redirectError.addAttribute("redirectError",
                    "The number of elements to compare must be a multiple of two.");
            return "redirect:/collection/{collName}";
        }
        return "redirect:/vote/chooseCollection";
    }
}

