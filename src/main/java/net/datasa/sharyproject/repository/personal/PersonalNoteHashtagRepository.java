package net.datasa.sharyproject.repository.personal;

import net.datasa.sharyproject.domain.entity.personal.PersonalNoteHashtagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalNoteHashtagRepository extends JpaRepository<PersonalNoteHashtagEntity, Long> {
    // 예시: 특정 노트에 연결된 해시태그들 조회
    List<PersonalNoteHashtagEntity> findByPersonalNote_PersonalNoteNum(int personalNoteNum);
}