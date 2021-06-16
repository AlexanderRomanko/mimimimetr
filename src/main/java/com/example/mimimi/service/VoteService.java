package com.example.mimimi.service;

import com.example.mimimi.entity.Coll;
import com.example.mimimi.entity.ComparableElement;
import com.example.mimimi.repos.CollRepository;
import com.example.mimimi.repos.ComparableElementRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class VoteService {

    @Value("${upload.path}")
    private String uploadPath;

    private final ComparableElementRepository comparableElementRepository;
    private final CollRepository collRepository;

    public VoteService(ComparableElementRepository comparableElementRepository, CollRepository collRepository) {
        this.comparableElementRepository = comparableElementRepository;
        this.collRepository = collRepository;
    }

    public Iterable<Coll> getCollectionsList() {
        List<Coll> collList = (List<Coll>) collRepository.findAll();
        collList.removeIf(coll ->
            coll.getComparableElementList().isEmpty() ||
            coll.getComparableElementList().size() % 2 > 0 ||
            Objects.requireNonNull(new File(uploadPath + "/" + coll.getName()).list()).length % 2 > 0 ||
            Objects.requireNonNull(new File(uploadPath + "/" + coll.getName()).list()).length == 0);
        return collList;
    }


    public List<ComparableElement> getComparableElements(Coll coll, Principal principal) {
        List<ComparableElement> comparableElements = coll.getComparableElementList();
        comparableElements.removeIf(comparableElement ->
                comparableElement.getVotedUsers().contains(principal.getName()));
        if (comparableElements.isEmpty()) return comparableElements;
        Random random = new Random();
        List<ComparableElement> twoComparableElements = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            int randomIndex = random.nextInt(comparableElements.size());
            twoComparableElements.add(comparableElements.get(randomIndex));
            comparableElements.remove(randomIndex);
        }
        return twoComparableElements;
    }

    public void vote(ComparableElement comparableElement1, ComparableElement comparableElement2,
                     String button1, Principal principal) {
        if (button1 != null) comparableElement1.setLikes(comparableElement1.getLikes() + 1);
        else comparableElement2.setLikes(comparableElement2.getLikes() + 1);
        comparableElement1.addVotedUser(principal.getName());
        comparableElement2.addVotedUser(principal.getName());
        comparableElementRepository.save(comparableElement1);
        comparableElementRepository.save(comparableElement2);
    }

    public List<ComparableElement> getResults(Coll coll) { //or if it's better to use SQL???
        coll.getComparableElementList().sort(Comparator.comparing(ComparableElement::getLikes).reversed());
        return new ArrayList<>(coll.getComparableElementList()).stream().limit(10).collect(Collectors.toList());
//        return new ArrayList<>(comparableElementRepository.findByTagOrderByLikesDesc(coll.getName()));
    }

}
