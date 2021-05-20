package com.example.mimimi.repos;

import com.example.mimimi.entity.ComparableElement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ComparableElementRepository extends CrudRepository<ComparableElement, Long> {


//    List<ComparableElement> findBy(String tag);

//    ComparableElement findFirstByTag(String tag);

//    @Query(value = "select * from comparable_element t where t.tag = :tag order by likes desc limit 10", nativeQuery = true)
//    List<ComparableElement> findByTagOrderByLikesDesc(String tag);

//    @Query(value = "delete from comparable_element t where t.tag = :tags", nativeQuery = true)
//    void deleteByTag(String tags);

//    @Query(value = "select * from comparable_element where id not in (select d2.id from comparable_element as d1 " +
//            "inner join comparable_element as d2 on d2.tag=d1.tag where d2.id > d1.id)", nativeQuery = true)
//    List<ComparableElement> findDistinctCatsWithDistinctTags();

    void deleteByFilename(String filename);

}