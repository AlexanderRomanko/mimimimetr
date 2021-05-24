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
//    public String index(@RequestParam(required = false) String collName) {
//        if (collName == null) return "collection";
        return "collection";
//        else return "collectionEdit";
    }

    @PostMapping("/collection")
    public String indexCollection(@RequestParam String collName, Model model) {
        if (collName.isEmpty()) return "collection";
        else if (collectionService.collectionExists(collName)) {
            model.addAttribute("error", "Collection exists, please choose another name");
            return "collection";
        } else {
            collectionService.createCollection(collName);
            Coll coll = collectionService.getCollection(collName);
            model.addAttribute("coll", coll);
            return "redirect:/collection/" + coll.getId();
        }
    }

    @GetMapping("/collection/{coll}")
    public String editCollection(@PathVariable Coll coll, @RequestParam(required = false) String redirectError, Model model) {
        if (redirectError != null) model.addAttribute("redirectError", redirectError);
        model.addAttribute("comparableElements", coll.getComparableElementList());
//        model.addAttribute("comparableElements", collectionService.getCollection(collName).getComparableElementList());
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

    @PostMapping("/collection/delete")
    public String deleteWrongCollections() {
        try {
            collectionService.deleteWrongCollections();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/main";
    }

    @GetMapping("/vote/chooseCollection")
    public String showCollectionsList(Model model) {
        List<Coll> collList = (List<Coll>) collectionService.getCollectionsList();
        if (collList.size() == 0) return "redirect:/main";
        model.addAttribute("collectionsList", collectionService.getCollectionsList());
        return "chooseVoteCollection";
    }
}

