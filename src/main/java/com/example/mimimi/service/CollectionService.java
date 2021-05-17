package com.example.mimimi.service;

import com.example.mimimi.entity.Coll;
import com.example.mimimi.entity.ComparableElement;
import com.example.mimimi.repos.CollRepository;
import com.example.mimimi.repos.ComparableElementRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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



    public void removeOddNumberOfFilesCollections() throws IOException {
        /*
         * Remove collection from repository if the collection consists of an odd number of files
         * Checking by file numbers in each folder
         */
//        File upload = new File(uploadPath);
//        for (String file : upload.list()) {
//            File dir = new File(uploadPath + "/" + file);
//            if (dir.list().length % 2 > 0) {
//                FileUtils.deleteDirectory(dir);
//                comparableElementRepository.deleteByTag(file);
//            }
//        }

        /*
         * Remove collection from repository if the collection consists of an odd number of files
         * Checking by file numbers in each collection in repository
         */
//        List<String> distinctTagsList = new ArrayList<>();
//        comparableElementRepository.findDistinctCatsWithDistinctTags().forEach(x -> distinctTagsList.add(x.getTag()));
//        for (String tag : distinctTagsList) {
//            if (comparableElementRepository.findByTag(tag).size() % 2 > 0) {
//                File uploadDir = new File(uploadPath + "/" + tag);
//                FileUtils.deleteDirectory(uploadDir);
//                comparableElementRepository.deleteByTag(tag);
//            }
//        }
    }

    public Iterable<Coll> getCollectionsList() {
//        List<String> collectionsList = new ArrayList<>();
//        for (Coll coll : collRepository.findAll()) {
//            collectionsList.add(coll.getName());
//        }
        return collRepository.findAll();
//        return comparableElementRepository.findDistinctCatsWithDistinctTags();
    }

    public boolean collectionExists(String tag) {
        Coll coll =  collRepository.findFirstByName(tag);
        return coll != null;
//        ComparableElement comparableElement = comparableElementRepository.findFirstByTag(tag);
//        return comparableElement != null;

    }

    public void createNewComparableElement(ComparableElement comparableElement, String name) {
        ComparableElement compElement = new ComparableElement(comparableElement.getName(), comparableElement.getFilename());
        comparableElementRepository.save(compElement);
        Coll coll = new Coll(name);
        collRepository.save(coll);
    }

    public void createCollection(String name) {
        Coll coll = new Coll(name);
        collRepository.save(coll);
    }

    public Coll getCollection(String tag) {
        return collRepository.findFirstByName(tag);
    }
}
