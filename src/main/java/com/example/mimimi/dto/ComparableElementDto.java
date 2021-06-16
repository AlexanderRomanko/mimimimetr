package com.example.mimimi.dto;

import com.example.mimimi.entity.Coll;

import java.util.Set;

public class ComparableElementDto {

    private Long id;
    private String name;
    private String filename;

    private Coll coll;

    private int likes;

    private Set<String> votedUsers;

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Coll getColl() {
        return coll;
    }

    public void setColl(Coll coll) {
        this.coll = coll;
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
