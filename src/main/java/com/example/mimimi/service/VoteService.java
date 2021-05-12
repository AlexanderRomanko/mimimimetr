package com.example.mimimi.service;

import com.example.mimimi.entity.ComparableElement;
import com.example.mimimi.repos.ComparableElementRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class VoteService {

    private final ComparableElementRepository comparableElementRepository;

    public VoteService(ComparableElementRepository comparableElementRepository) {
        this.comparableElementRepository = comparableElementRepository;
    }

    public List<ComparableElement> getComparableElements(String tag) { //todo put user to method param
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();//if it's better to move this field up //todo and remove this
        List<ComparableElement> comparableElements = comparableElementRepository.findByTag(tag); // Each time calls repository //todo will better create SQL request with user filter
        comparableElements.removeIf(comparableElement -> comparableElement.getVotedUsers().contains(principal));
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

    public void vote(ComparableElement comparableElement1, ComparableElement comparableElement2, String button1) { //todo put user to method param
        String principal = SecurityContextHolder.getContext().getAuthentication().getName(); //todo and remove this
        if (button1 != null) comparableElement1.setLikes(comparableElement1.getLikes() + 1);
        else comparableElement2.setLikes(comparableElement2.getLikes() + 1);
        comparableElement1.getVotedUsers().add(principal);
        comparableElement2.getVotedUsers().add(principal);
        comparableElementRepository.save(comparableElement1); //todo catch IllegalArgumentException
        comparableElementRepository.save(comparableElement2); //todo catch IllegalArgumentException
    }

    public List<ComparableElement> getResults(String tag) {
        return new ArrayList<>(comparableElementRepository.findByTagOrderByLikesDesc(tag));
    }

}
