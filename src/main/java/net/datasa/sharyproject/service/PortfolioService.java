package net.datasa.sharyproject.service;

import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalLikeDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalLikeEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.personal.PersonalLikeRepository;
import net.datasa.sharyproject.repository.personal.PersonalNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PersonalNoteRepository personalNoteRepository;
    private final MemberRepository memberRepository;
    private final PersonalLikeRepository personalLikeRepository;

    // 전체공개된 노트만 가져오는 메서드
    public List<PersonalNoteDTO> getPublicNotes() {
        return personalNoteRepository.findPublicNotes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 노트를 번호로 가져오는 메서드
    public PersonalNoteDTO getNoteByNum(Integer noteNum) {
        // 노트를 데이터베이스에서 조회
        PersonalNoteEntity noteEntity = personalNoteRepository.findById(noteNum)
                .orElseThrow(() -> new RuntimeException("해당 노트가 존재하지 않습니다."));
        // DTO로 변환하여 반환
        return convertToDTO(noteEntity);
    }

    // 특정 멤버의 프로필 정보를 가져오는 메서드
    public ProfileDTO getMemberProfileById(String memberId) {
        // 멤버 엔티티 조회
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 멤버가 존재하지 않습니다."));

        // 프로필 엔티티 조회
        ProfileEntity profile = member.getProfile();

        // 프로필이 없는 경우 기본 값을 설정하거나 예외 처리
        if (profile == null) {
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
                .nickname(member.getNickname())
                .memberId(member.getMemberId())
                .build();
    }

    // ========== 좋아요 토글 기능 추가 ==========

    /**
     * 노트에 대한 좋아요 상태를 토글하는 메서드
     * @param personalNoteNum 노트 번호
     * @param memberId 좋아요를 누르는 사용자의 ID
     * @return PersonalLikeDTO - 업데이트된 좋아요 상태 정보를 담은 DTO 반환
     */
    public PersonalLikeDTO toggleLike(Integer personalNoteNum, String memberId) {
        Optional<PersonalLikeEntity> likeEntityOpt = personalLikeRepository.findByPersonalNoteNumAndMemberId(personalNoteNum, memberId);

        PersonalLikeEntity likeEntity;
        if (likeEntityOpt.isPresent()) {
            // 이미 존재하는 좋아요의 상태를 토글
            likeEntity = likeEntityOpt.get();
            likeEntity.setLikeClicked(!likeEntity.isLikeClicked());
        } else {
            // 새로운 좋아요 생성
            likeEntity = PersonalLikeEntity.builder()
                    .personalNoteNum(personalNoteNum)
                    .memberId(memberId)
                    .likeClicked(true)
                    .build();
        }

        // 엔티티 저장
        personalLikeRepository.save(likeEntity);

        // 총 좋아요 수 계산
        int likeCount = personalLikeRepository.countByPersonalNoteNumAndLikeClicked(personalNoteNum, true);

        // 엔티티를 DTO로 변환하여 반환
        return PersonalLikeDTO.builder()
                .personalLikeNum(likeEntity.getPersonalLikeNum())
                .personalNoteNum(likeEntity.getPersonalNoteNum())
                .memberId(likeEntity.getMemberId())
                .likeClicked(likeEntity.isLikeClicked())
                .likeCount(likeCount)
                .build();
    }

    // PersonalNoteEntity를 PersonalNoteDTO로 변환하는 메서드
    private PersonalNoteDTO convertToDTO(PersonalNoteEntity entity) {
        String coverImagePath = entity.getPersonalDiary().getCoverTemplate().getCoverImage();

        if (coverImagePath.contains("static/images/")) {
            coverImagePath = coverImagePath.substring(coverImagePath.indexOf("static/images/") + "static/images/".length());
        }

        NoteTemplateDTO noteTemplate = new NoteTemplateDTO();
        noteTemplate.setNoteNum(entity.getNoteTemplate().getNoteNum());
        noteTemplate.setNoteName(entity.getNoteTemplate().getNoteName());

        String imagePath = entity.getNoteTemplate().getNoteImage();
        if (imagePath.contains("static/images/")) {
            imagePath = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        }
        noteTemplate.setNoteImage(imagePath);

        MemberEntity member = entity.getMember();
        ProfileEntity profile = member.getProfile();
        String profilePicture = (profile != null) ? profile.getProfilePicture() : "default_profile.png";

        int likeCount = personalLikeRepository.countByPersonalNoteNumAndLikeClicked(entity.getPersonalNoteNum(), true);

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
                .likeCount(likeCount) // 좋아요 수 추가
                .build();
    }
}
