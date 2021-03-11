package com.example.mimimi.repos;

import com.example.mimimi.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatRepository extends JpaRepository<Cat, Long> {
    List<Cat> findByTag(String tag);


    @Modifying
    @Query(value = "delete from cat t where t.tag = :tags", nativeQuery = true)
    List<Integer> deleteByTag(@Param("tags") String tags);
//    void deleteNotFull(@Param("tags") String tags);
    //    @Query(value = "select id from cat order by random();", nativeQuery = true)
//    List<Long> findAllIds();
//    @Query(value = "select distinct(tag) from cat", nativeQuery = true)
//    List<String> findAllTags();
    @Query(value = "SELECT * FROM cat WHERE id NOT IN (SELECT d2.id FROM cat AS d1 INNER JOIN cat AS d2 ON d2.tag=d1.tag WHERE d2.id > d1.id)", nativeQuery = true)
    List<Cat> findDistinctCatsWithDistinctTags();

//    Cat (String filename);
}