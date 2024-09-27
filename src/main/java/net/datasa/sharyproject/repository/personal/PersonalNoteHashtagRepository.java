package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.PersonalNoteHashtagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalNoteHashtagRepository extends JpaRepository<PersonalNoteHashtagEntity, Long> {

    // 특정 노트에 연결된 해시태그들 조회
    List<PersonalNoteHashtagEntity> findByPersonalNote_PersonalNoteNum(int personalNoteNum);

    // user_category 테이블을 사용하여 카테고리별 해시태그 사용 횟수 집계
    @Query("SELECT ht.hashtagName, COUNT(pnh) " +
            "FROM PersonalNoteHashtagEntity pnh " +
            "JOIN pnh.personalNote pn " +
            "JOIN pn.personalDiary pd " +
            "JOIN pnh.hashtag ht " +
            "WHERE pd.category.categoryNum = :categoryNum " +
            "GROUP BY ht.hashtagName")
    List<Object[]> findHashtagUsageByCategory(@Param("categoryNum") int categoryNum);
}