package com.example.mimimi.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class ComparableElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
//    private String tag;
    private String filename;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int likes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "voted_users", joinColumns = @JoinColumn(name = "voted_users_id"))
    private Set<String> votedUsers;

    public ComparableElement() {
    }

    public ComparableElement(String name, String filename) {
        this.name = name;
        this.filename = filename;
    }

//    public ComparableElement(String name, String tag, String filename) {
//        this.name = name;
//        this.tag = tag;
//        this.filename = filename;
//    }

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

//    public String getTag() {
//        return tag;
//    }
//
//    public void setTag(String tag) {
//        this.tag = tag;
//    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Set<String> getVotedUsers() {
        return votedUsers;
    }

    public void setVotedUsers(Set<String> votedUsers) {
        this.votedUsers = votedUsers;
    }
}
