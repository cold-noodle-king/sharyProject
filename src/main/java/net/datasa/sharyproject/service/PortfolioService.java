package net.datasa.sharyproject.service;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.personal.PersonalNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PersonalNoteRepository personalNoteRepository;
    private final MemberRepository memberRepository;

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

    // 특정 멤버의 프로필 정보를 가져오는 메서드
    public ProfileDTO getMemberProfileById(String memberId) {
        // 멤버 엔티티 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 멤버가 존재하지 않습니다."));

        // 프로필 엔티티 조회
        ProfileEntity profile = member.getProfile();

        if (profile == null) {
            // 프로필이 없는 경우 기본 값을 설정하거나 예외 처리
            return ProfileDTO.builder()
                    .nickname(member.getNickname())
                    .ment("프로필 정보가 없습니다.")
                    .profilePicture("default_profile.png")
                    .memberId(member.getMemberId())
                    .build();
        }

        // ProfileEntity를 ProfileDTO로 변환하여 반환
        return ProfileDTO.builder()
                .profileNum(profile.getProfileNum())
                .profilePicture(profile.getProfilePicture())
                .profileOriginalName(profile.getProfileOriginalName())
                .ment(profile.getMent())
                .nickname(member.getNickname())     // 닉네임 추가
                .memberId(member.getMemberId())     // memberId 설정
                .build();
    }

    // PersonalNoteEntity를 PersonalNoteDTO로 변환하는 메서드
    private PersonalNoteDTO convertToDTO(PersonalNoteEntity entity) {
        // 다이어리의 커버 이미지 경로 가져오기
        String coverImagePath = entity.getPersonalDiary().getCoverTemplate().getCoverImage();

        // 커버 이미지 경로에서 불필요한 경로 제거
        if (coverImagePath.contains("static/images/")) {
            coverImagePath = coverImagePath.substring(coverImagePath.indexOf("static/images/") + "static/images/".length());
        }

        // NoteTemplateDTO 설정
        NoteTemplateDTO noteTemplate = new NoteTemplateDTO();
        noteTemplate.setNoteNum(entity.getNoteTemplate().getNoteNum());
        noteTemplate.setNoteName(entity.getNoteTemplate().getNoteName());

        // 노트 템플릿 이미지 경로에서 파일명만 추출
        String imagePath = entity.getNoteTemplate().getNoteImage();
        if (imagePath.contains("static/images/")) {
            imagePath = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        }
        noteTemplate.setNoteImage(imagePath);

        // 프로필 정보 가져오기
        MemberEntity member = entity.getMember();
        ProfileEntity profile = member.getProfile();
        String profilePicture = (profile != null) ? profile.getProfilePicture() : "default_profile.png";

        // PersonalNoteDTO 빌드하여 반환
        return PersonalNoteDTO.builder()
                .personalNoteNum(entity.getPersonalNoteNum())
                .noteTitle(entity.getNoteTitle())
                .fileName(entity.getFileName())
                .diaryNum(entity.getPersonalDiary().getPersonalDiaryNum())
                .coverImage("/images/" + coverImagePath)
                .profilePicture(profilePicture)
                .contents(entity.getContents())
                .diaryDate(entity.getDiaryDate())
                .location(entity.getLocation())
                .emotionNum(entity.getEmotion().getEmotionNum())
                .hashtags(entity.getHashtags().stream()
                        .map(HashtagEntity::getHashtagName)
                        .collect(Collectors.toList()))
                .noteTemplate(noteTemplate)
                .memberId(member.getMemberId())
                .build();
    }
}
