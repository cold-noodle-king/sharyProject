package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalNoteRepository extends JpaRepository<PersonalNoteEntity, Integer> {

    // 다이어리 번호로 PersonalNote 목록을 찾는 메서드
    List<PersonalNoteEntity> findByPersonalDiary_PersonalDiaryNum(Integer personalDiaryNum);
}
