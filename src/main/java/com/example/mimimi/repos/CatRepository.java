package com.example.mimimi.repos;

import com.example.mimimi.domain.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;

public interface CatRepository extends JpaRepository<Cat, Long> {
    List<Cat> findByTag(String filter);

    //    @Query(value = "select id from cat order by random();", nativeQuery = true)
//    List<Long> findAllIds();
//    @Query(value = "select distinct(tag) from cat", nativeQuery = true)
//    List<String> findAllTags();
    @Query(value = "SELECT tag, filename FROM cat WHERE id NOT IN (SELECT d2.id FROM cat AS d1 INNER JOIN cat AS d2 ON d2.tag=d1.tag WHERE d2.id > d1.id)", nativeQuery = true)
    Map<String, String> findAllTags();



//    Cat (String filename);
}