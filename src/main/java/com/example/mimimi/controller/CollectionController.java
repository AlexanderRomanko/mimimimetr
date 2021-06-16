package com.example.mimimi.controller;

import com.example.mimimi.dto.CollDto;
import com.example.mimimi.entity.Coll;
import com.example.mimimi.service.CollectionService;
import com.example.mimimi.service.ConverterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/collection")
public class CollectionController {

    @Value("${upload.path}")
    private String uploadPath;

    private final CollectionService collectionService;
    private final ConverterService converterService;

    public CollectionController(CollectionService collectionService, ConverterService converterService) {
        this.collectionService = collectionService;
        this.converterService = converterService;
    }

    @GetMapping
    public String index() {
        return "collection";
    }

    @PostMapping
    public String indexCollection(@RequestParam String collName, Model model) {
        try {
            Long id = collectionService.createCollection(collName).getId();
            return "redirect:/collection/" + id;
        } catch (TransactionSystemException e) { // if collName is empty
            return "collection";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Collection exists, please choose another name");
            return "collection";
        }
    }

    @GetMapping("/{coll}")
    public String editCollection(@PathVariable Coll coll,
                                 @RequestParam(required = false) String redirectError, Model model) {
        if (redirectError != null) model.addAttribute("redirectError", redirectError);
        model.addAttribute("comparableElements",
                converterService.convertToDto(coll).getComparableElementList());
        return "collectionEdit";
    }

    @PostMapping("/{coll}")
    public String editCollection(@PathVariable Coll coll, @RequestParam String name,
                                 @RequestParam("file") MultipartFile file, Model model) {
        if (new File(uploadPath + "/" + coll.getName() + "/" + file.getOriginalFilename()).isFile()) {
            model.addAttribute("error", "File already exists, choose another.");
            model.addAttribute("name", name);
        } else {
            collectionService.createNewComparableElement(name, file, coll.getName());
        }
        model.addAttribute("comparableElements",
                converterService.convertToDto(coll).getComparableElementList());
        return "collectionEdit";
    }

    @PostMapping("/{coll}/remove")
    public String deleteElement(@PathVariable Coll coll, @RequestParam String filename) {
        collectionService.remove(coll, filename);
        return "redirect:/collection/{coll}";
    }

    @PostMapping("/{coll}/save")
    public String saveCollection(@PathVariable Coll coll, RedirectAttributes redirectError) {
        int collSize = coll.getComparableElementList().size();
        if (collSize == 0 || collSize % 2 > 0) {
            redirectError.addAttribute("redirectError",
                    "The number of elements must be a multiple of two.");
            return "redirect:/collection/{coll}";
        }
        return "redirect:/vote/chooseCollection";
    }

    @PostMapping("/delete")
    public String deleteWrongCollections() {
        try {
            collectionService.deleteWrongCollections();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/collection";
    }
}

