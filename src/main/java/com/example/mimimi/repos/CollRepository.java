package com.example.mimimi.repos;

import com.example.mimimi.entity.Coll;
import org.springframework.data.repository.CrudRepository;

public interface CollRepository extends CrudRepository<Coll, Long> {
    Coll findFirstByName(String tag);
}
