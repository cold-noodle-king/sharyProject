package net.datasa.sharyproject.repository;

import net.datasa.sharyproject.domain.entity.HashtagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<HashtagEntity, Integer> {

    /**
     * 카테고리 번호로 해시태그 목록을 조회하는 메서드
     * @param categoryNum 카테고리 번호
     * @return 해당 카테고리 번호에 해당하는 해시태그 목록
     */
    List<HashtagEntity> findByCategory_CategoryNum(Integer categoryNum);
}
