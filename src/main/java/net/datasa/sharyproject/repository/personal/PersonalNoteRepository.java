package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalNoteRepository extends JpaRepository<PersonalNoteEntity, Integer> {

    // 다이어리 번호로 PersonalNote 목록을 찾는 메서드
    List<PersonalNoteEntity> findByPersonalDiary_PersonalDiaryNum(Integer personalDiaryNum);

    // Granted가 전체 공개인 노트 목록을 찾는 메서드 (GrantedNum = 3)
    @Query("SELECT p FROM PersonalNoteEntity p WHERE p.granted.grantedNum = 3")
    List<PersonalNoteEntity> findPublicNotes(); // 메서드명을 의미에 맞게 수정

    // 카테고리별로 다이어리 개수를 집계하는 쿼리
    @Query("SELECT p.personalDiary.category.categoryName, COUNT(p) " +
            "FROM PersonalNoteEntity p " +
            "GROUP BY p.personalDiary.category.categoryName")
    List<Object[]> countDiariesByCategory();
}
