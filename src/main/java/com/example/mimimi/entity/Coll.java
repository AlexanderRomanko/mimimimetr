package com.example.mimimi.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Coll {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    @NotBlank
    private String name;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "coll_id")
    private List<ComparableElement> comparableElementList;

    public Coll() {
    }

    public Coll(String name) {
        this.name = name;
    }

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
