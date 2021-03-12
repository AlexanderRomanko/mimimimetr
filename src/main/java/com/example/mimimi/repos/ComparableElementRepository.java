package com.example.mimimi.repos;

import com.example.mimimi.entity.ComparableElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComparableElementRepository extends JpaRepository<ComparableElement, Long> {
    List<ComparableElement> findByTag(String tag);

    ComparableElement findFirstByTag(String tag);

    @Query(value = "select * from comparable_element t order by likes desc limit 10", nativeQuery = true)
    List<Long> findWinnersIdList();

    List<ComparableElement> findByTagOrderByLikesDesc  (String tag);

    @Modifying
    @Query(value = "delete from comparable_element t where t.tag = :tags", nativeQuery = true)
    void deleteByTag(@Param("tags") String tags);

    @Query(value = "select * from comparable_element where id not in (select d2.id from comparable_element as d1 " +
            "inner join comparable_element as d2 on d2.tag=d1.tag where d2.id > d1.id)", nativeQuery = true)
    List<ComparableElement> findDistinctCatsWithDistinctTags();

}