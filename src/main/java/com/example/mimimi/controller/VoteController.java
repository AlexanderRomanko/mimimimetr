package com.example.mimimi.controller;

import com.example.mimimi.dto.CollDto;
import com.example.mimimi.dto.ComparableElementDto;
import com.example.mimimi.entity.Coll;
import com.example.mimimi.entity.ComparableElement;
import com.example.mimimi.service.ConverterService;
import com.example.mimimi.service.VoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vote")
public class VoteController {

    private final VoteService voteService;
    private final ConverterService converterService;

    public VoteController(VoteService voteService, ConverterService converterService) {
        this.voteService = voteService;
        this.converterService = converterService;
    }

    @GetMapping("/chooseCollection")
    public String showCollectionsList(Model model) {
        Iterable<Coll> collList = voteService.getCollectionsList();
        List<CollDto> collDtoList = new ArrayList<>();
        for (Coll coll: collList) {
            collDtoList.add(converterService.convertToDto(coll));
        }
        if (collDtoList.size() == 0) {
            return "redirect:/main";
        }
        model.addAttribute("coll", collDtoList);
        return "chooseVoteCollection";
    }

    @GetMapping("/{coll}")
    public String showComparableElements(@PathVariable Coll coll, Principal principal, Model model) {
        List<ComparableElement> twoComparableElements = voteService.getComparableElements(coll, principal);
        if (twoComparableElements.isEmpty())
            return "redirect:/vote/{coll}/results";
        model.addAttribute("comparableElement1", converterService.convertToDto(twoComparableElements.get(0)));
        model.addAttribute("comparableElement2", converterService.convertToDto(twoComparableElements.get(1)));
        return "vote";
    }

    @PostMapping("/{coll}")
    public String vote(@PathVariable Coll coll, @RequestParam("comparableElement1Id") ComparableElement comparableElement1, @RequestParam("comparableElement2Id") ComparableElement comparableElement2,
                       @RequestParam(required = false) String button1, Principal principal) {
        voteService.vote(comparableElement1, comparableElement2, button1, principal);
        return "redirect:/vote/{coll}";
    }

    @GetMapping("{coll}/results")
    public String showResults(@PathVariable Coll coll, Model model) {
        List<ComparableElementDto> votedList = new ArrayList<>();
        for (ComparableElement comparableElement:
                voteService.getResults(coll)) {
            votedList.add(converterService.convertToDto(comparableElement));
        }
        model.addAttribute("votedList", votedList);
        return "results";
    }

}
