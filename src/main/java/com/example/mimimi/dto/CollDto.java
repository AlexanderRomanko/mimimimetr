package com.example.mimimi.dto;

import com.example.mimimi.entity.ComparableElement;

import java.util.List;

public class CollDto {

    private Long id;
    private String name;
    private List<ComparableElement> comparableElementList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ComparableElement> getComparableElementList() {
        return comparableElementList;
    }

    public void setComparableElementList(List<ComparableElement> comparableElementList) {
        this.comparableElementList = comparableElementList;
    }
}
