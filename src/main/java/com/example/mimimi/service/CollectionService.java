package com.example.mimimi.service;

import com.example.mimimi.entity.Coll;
import com.example.mimimi.entity.ComparableElement;
import com.example.mimimi.repos.CollRepository;
import com.example.mimimi.repos.ComparableElementRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class CollectionService {

    @Value("${upload.path}")
    private String uploadPath;

    private final ComparableElementRepository comparableElementRepository;
    private final CollRepository collRepository;

    public CollectionService(ComparableElementRepository comparableElementRepository, CollRepository collRepository) {
        this.comparableElementRepository = comparableElementRepository;
        this.collRepository = collRepository;
    }



    public void deleteWrongCollections() throws IOException {
        /*
         * Delete useless folders and collection from repository if the collection consists of an odd number of files
         * Checking by file numbers in each folder
         */
        for (String collName : Objects.requireNonNull(new File(uploadPath).list())) {
            File dir = new File(uploadPath + "/" + collName);
            Coll coll = collRepository.findFirstByName(collName);
            if (coll == null) {
                FileUtils.deleteDirectory(dir);
            } else if (Objects.requireNonNull(dir.list()).length % 2 > 0 || Objects.requireNonNull(dir.list()).length == 0) {
                FileUtils.deleteDirectory(dir);
                collRepository.delete(coll);
            }
        }
        /*
         * Delete  useless folders and collection from repository if the collection consists of an odd number of files
         * Checking by comparable_element numbers in each coll in CollRepository
         */
        for (Coll coll : collRepository.findAll()) {
            File dir = new File(uploadPath + "/" + coll.getName());
            if (!dir.exists()) {
                collRepository.delete(coll);
            } else if (coll.getComparableElementList().size() % 2 > 0 || coll.getComparableElementList().isEmpty()) {
                FileUtils.deleteDirectory(dir);
                collRepository.delete(coll);
            }
        }
    }

    public void createNewComparableElement(String name, MultipartFile file, String collName) {
        try {
            file.transferTo(new File(uploadPath + "/" + collName + "/" + file.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        comparableElementRepository.save(new ComparableElement(name, file.getOriginalFilename(), collRepository.findFirstByName(collName)));
    }

    public Coll createCollection(String collName) {
        Coll coll = new Coll(collName);
        collRepository.save(coll);
        File uploadDir = new File(uploadPath + "/" + collName);
        try {
            FileUtils.deleteDirectory(uploadDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uploadDir.mkdirs();
        return coll;
    }

    public void remove(Coll coll, String filename) {
        File file = new File(uploadPath + "/" + coll.getName() + "/" + filename);
        file.delete();
        comparableElementRepository.deleteByCollAndFilename(coll, filename);
    }
}
