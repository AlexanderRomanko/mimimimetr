package com.example.mimimi.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class ComparableElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String filename;

    @ManyToOne
    @CollectionTable(name = "coll", joinColumns = @JoinColumn(name = "id"))
    private Coll coll;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int likes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "voted_users", joinColumns = @JoinColumn(name = "voted_users_id"))
    private Set<String> votedUsers;

    public ComparableElement() {
    }

    public ComparableElement(String name, String filename, Coll coll) {
        this.name = name;
        this.filename = filename;
        this.coll = coll;
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

    public Coll getColl() {
        return coll;
    }

    public void setColl(Coll coll) {
        this.coll = coll;
    }
}
