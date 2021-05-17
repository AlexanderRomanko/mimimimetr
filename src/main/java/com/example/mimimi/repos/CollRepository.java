package com.example.mimimi.repos;

import com.example.mimimi.entity.Coll;
import com.example.mimimi.entity.ComparableElement;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CollRepository extends CrudRepository<Coll, Long> {
    Coll findFirstByName(String tag);
}
