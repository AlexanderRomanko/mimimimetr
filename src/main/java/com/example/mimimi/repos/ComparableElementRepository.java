package com.example.mimimi.repos;

import com.example.mimimi.entity.Coll;
import com.example.mimimi.entity.ComparableElement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ComparableElementRepository extends CrudRepository<ComparableElement, Long> {

    @Transactional
    void deleteByCollAndFilename(Coll coll, String filename);

}