package net.datasa.sharyproject.service;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
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

    // PersonalNoteEntity를 PersonalNoteDTO로 변환
    private PersonalNoteDTO convertToDTO(PersonalNoteEntity entity) {
        String coverImagePath = entity.getPersonalDiary().getCoverTemplate().getCoverImage();

        // 상대 경로로 변경 (절대 경로에서 필요한 부분만 추출)
        if (coverImagePath.contains("static/images/")) {
            coverImagePath = coverImagePath.substring(coverImagePath.indexOf("static/images/") + "static/images/".length());
        }

        return PersonalNoteDTO.builder()
                .personalNoteNum(entity.getPersonalNoteNum())
                .noteTitle(entity.getNoteTitle())
                .fileName(entity.getFileName())
                .diaryNum(entity.getPersonalDiary().getPersonalDiaryNum())
                .coverImage("/images/" + coverImagePath) // 상대 경로로 변경
                .profilePicture(entity.getProfile().getProfilePicture())
                .build();
    }
}
