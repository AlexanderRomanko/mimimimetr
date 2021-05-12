package com.example.mimimi.controller;

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

    @Value("${upload.path}") //todo dont use absolute path, in unix server path will be different
    private String uploadPath;

    Map<String, String> collectionTemp = new LinkedHashMap<>();/*??replace????????????????*/

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/vote/chooseCollection") //todo this action need ?????
    public String showCollectionsList(Model model) {
        try { // move try/catch somewhere else????????
            collectionService.removeOddNumberOfFilesCollections();
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("collectionsList", collectionService.getCollectionsList());
        return "chooseVoteCollection";
    }

    @GetMapping("/collection")
    public String index(@RequestParam(required = false, defaultValue = "") String tag) {
        if (tag.isEmpty()) return "collection";
        return "collectionEdit";
    }

    @PostMapping("/collection") //todo check it
    public String indexCollection(@RequestParam String tag, Model model) throws IOException {
        if (tag.isEmpty()) return "collection";
        if (collectionService.collectionExists(tag)) {
            model.addAttribute("error", "Collection exists, please choose another name");
            return "collection";
        }
        File uploadDir = new File(uploadPath + "/" + tag);
        FileUtils.deleteDirectory(uploadDir);
        collectionTemp.clear();/*??????????????????????*/
        return "redirect:/collection/" + tag;
    }

    @GetMapping("/collection/{tag}")
    public String editCollection(@PathVariable String tag, @RequestParam(required = false) String redirectError, Model model) {
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
        if (new File(uploadPath + "/" + tag + "/" + filename).isFile() && collectionTemp.isEmpty()) {
            model.addAttribute("error", "File already exists, choose another.");
            return "redirect:/collection";
        }
        if (new File(uploadPath + "/" + tag + "/" + filename).isFile()) {
//            model.addAttribute("collectionTemp", collectionTemp);
            model.addAttribute("error", "File already exists, choose another.");
            model.addAttribute("name", name);
//            return "collectionEdit";
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
    public String saveCollection(@PathVariable String tag, RedirectAttributes redirectError) {
        if (collectionTemp.size() == 0) return "redirect:/collection/{tag}";
        if (collectionTemp.size() % 2 > 0) {
            redirectError.addAttribute("redirectError",
                    "The number of elements to compare must be a multiple of two.");
            return "redirect:/collection/{tag}";
        }
        collectionTemp.entrySet().forEach(x -> collectionService.createNewComparableElement(x, tag));
        collectionTemp.clear();
        return "redirect:/vote/chooseCollection";
    }
}

