package net.datasa.sharyproject.service;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import net.datasa.sharyproject.repository.personal.PersonalNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PersonalNoteRepository personalNoteRepository;

    // 전체공개된 노트만 가져오는 메서드
    public List<PersonalNoteDTO> getPublicNotes() {
        return personalNoteRepository.findPublicNotes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 노트를 번호로 가져오는 메서드
    public PersonalNoteDTO getNoteByNum(Integer noteNum) {
        PersonalNoteEntity noteEntity = personalNoteRepository.findById(noteNum)
                .orElseThrow(() -> new RuntimeException("해당 노트가 존재하지 않습니다."));
        return convertToDTO(noteEntity);
    }

    // PersonalNoteEntity를 PersonalNoteDTO로 변환
    private PersonalNoteDTO convertToDTO(PersonalNoteEntity entity) {
        String coverImagePath = entity.getPersonalDiary().getCoverTemplate().getCoverImage();

        // 기존 이미지 경로 처리 방식 유지
        if (coverImagePath.contains("static/images/")) {
            coverImagePath = coverImagePath.substring(coverImagePath.indexOf("static/images/") + "static/images/".length());
        }

        return PersonalNoteDTO.builder()
                .personalNoteNum(entity.getPersonalNoteNum())
                .noteTitle(entity.getNoteTitle())
                .fileName(entity.getFileName())
                .diaryNum(entity.getPersonalDiary().getPersonalDiaryNum())
                .coverImage("/images/" + coverImagePath)  // 커버 이미지 복원
                .profilePicture(entity.getProfile().getProfilePicture())
                .contents(entity.getContents())
                .diaryDate(entity.getDiaryDate())
                .location(entity.getLocation())
                .emotionNum(entity.getEmotion().getEmotionNum())
                .hashtags(entity.getHashtags().stream()
                        .map(HashtagEntity::getHashtagName) // 해시태그 이름만 추출
                        .collect(Collectors.toList())) // 해시태그 이름 리스트로 변환
                .build();
    }
}
