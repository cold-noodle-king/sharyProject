package net.datasa.sharyproject.service.personal;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import net.datasa.sharyproject.repository.personal.PersonalNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalNoteService {

    private final PersonalNoteRepository personalNoteRepository; // PersonalNote 저장소

    /**
     * 다이어리 번호로 PersonalNote 목록을 가져오는 메서드
     * @param diaryNum 다이어리 번호
     * @return PersonalNote 목록 (DTO 리스트)
     */
    public List<PersonalNoteDTO> getNotesByDiaryNum(Integer diaryNum) {
        // 다이어리 번호로 PersonalNote 목록을 조회
        List<PersonalNoteEntity> noteEntities = personalNoteRepository.findByPersonalDiary_PersonalDiaryNum(diaryNum);

        // 조회된 엔티티를 DTO로 변환하여 반환
        return noteEntities.stream()
                .map(this::convertEntityToDTO) // 엔티티를 DTO로 변환하는 메서드 호출
                .collect(Collectors.toList());
    }

    /**
     * PersonalNoteEntity를 PersonalNoteDTO로 변환하는 메서드
     * @param note PersonalNoteEntity
     * @return PersonalNoteDTO
     */
    private PersonalNoteDTO convertEntityToDTO(PersonalNoteEntity note) {
        return PersonalNoteDTO.builder()
                .personalNoteNum(note.getPersonalNoteNum())  // 노트 고유 번호
                .noteTitle(note.getNoteTitle())              // 노트 제목
                .contents(note.getContents())                // 노트 내용
                .diaryDate(note.getDiaryDate())              // 작성 날짜
                .likeCount(note.getLikeCount())              // 추천 수
                .viewCount(note.getViewCount())              // 조회 수
                .weather(note.getWeather())                  // 날씨
                .emotionName(note.getEmotion().getEmotionName()) // 감정 이름
                .build();
    }
}