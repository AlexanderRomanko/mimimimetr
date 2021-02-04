package com.example.mimimi.repos;

import com.example.mimimi.domain.Cat;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CatRepository extends CrudRepository<Cat, Long> {
    List<Cat> findByTag(String filter);
    Cat findByFilename(String filename);
}