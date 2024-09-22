package net.datasa.sharyproject.service.personal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.entity.EmotionEntity;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.domain.entity.personal.*;
import net.datasa.sharyproject.repository.EmotionRepository;
import net.datasa.sharyproject.repository.HashtagRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.mypage.ProfileRepository;
import net.datasa.sharyproject.repository.personal.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalNoteService {

    private final PersonalNoteRepository personalNoteRepository;
    private final HashtagRepository hashtagRepository;
    private final EmotionRepository emotionRepository;
    private final GrantedRepository grantedRepository;
    private final PersonalDiaryRepository personalDiaryRepository;
    private final NoteTemplateRepository noteTemplateRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final PersonalNoteHashtagRepository personalNoteHashtagRepository;

    /**
     * 다이어리 번호로 PersonalNote 목록을 가져오는 메서드
     *
     * @param diaryNum 다이어리 번호
     * @return PersonalNoteDTO 리스트
     */
    public List<PersonalNoteDTO> getNotesByDiaryNum(Integer diaryNum) {
        List<PersonalNoteEntity> noteEntities = personalNoteRepository.findByPersonalDiary_PersonalDiaryNum(diaryNum);
        return noteEntities.stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * PersonalNoteEntity를 PersonalNoteDTO로 변환하는 메서드
     *
     * @param note PersonalNoteEntity
     * @return PersonalNoteDTO
     */
    private PersonalNoteDTO convertEntityToDTO(PersonalNoteEntity note) {
        List<Integer> hashtagNums = note.getHashtags().stream()
                .map(HashtagEntity::getHashtagNum)
                .collect(Collectors.toList());

        NoteTemplateDTO noteTemplateDTO = null;
        if (note.getNoteTemplate() != null) {
            noteTemplateDTO = NoteTemplateDTO.builder()
                    .noteNum(note.getNoteTemplate().getNoteNum())
                    .noteName(note.getNoteTemplate().getNoteName())
                    .noteImage(note.getNoteTemplate().getNoteImage())
                    .build();
        }

        // 프로필 정보 가져오기
        ProfileEntity profileEntity = profileRepository.findByMember(note.getMember())
                .orElse(null);
        String profilePicture = "/images/default_profile.png";  // 기본 프로필 이미지
        if (profileEntity != null && profileEntity.getProfilePicture() != null) {
            profilePicture = profileEntity.getProfilePicture();  // 프로필 이미지 설정
        }

        return PersonalNoteDTO.builder()
                .personalNoteNum(note.getPersonalNoteNum())
                .noteTitle(note.getNoteTitle())
                .contents(note.getContents())
                .diaryDate(note.getDiaryDate())
                .likeCount(note.getLikeCount())
                .viewCount(note.getViewCount())
                .weather(note.getWeather())
                .emotionName(note.getEmotion().getEmotionName())
                .grantedName(note.getGranted().getGrantedName())
                .hashtagNums(hashtagNums)
                .location(note.getLocation())
                .fileName(note.getFileName())
                .diaryNum(note.getPersonalDiary().getPersonalDiaryNum())
                .noteTemplate(noteTemplateDTO)
                .profilePicture(profilePicture)  // 프로필 이미지 추가
                .build();
    }

    /**
     * 노트 번호로 노트를 조회하는 메서드
     * @param noteNum 노트 번호
     * @return PersonalNoteDTO
     */
    public PersonalNoteDTO getNoteByNum(Integer noteNum) {
        PersonalNoteEntity noteEntity = personalNoteRepository.findById(noteNum)
                .orElseThrow(() -> new RuntimeException("해당 노트 정보를 찾을 수 없습니다."));

        return convertEntityToDTO(noteEntity);  // DTO 변환 후 반환
    }

    /**
     * PersonalNoteDTO를 PersonalNoteEntity로 변환하여 저장하는 메서드
     *
     * @param noteDTO    PersonalNoteDTO
     * @param hashtagIds 해시태그 번호 리스트
     * @return 저장된 노트 번호
     */
    @Transactional
    public Integer saveNote(PersonalNoteDTO noteDTO, List<Integer> hashtagIds) {
        try {
            PersonalNoteEntity noteEntity = convertToEntity(noteDTO);
            if (hashtagIds != null && !hashtagIds.isEmpty()) {
                List<HashtagEntity> hashtags = hashtagRepository.findAllById(hashtagIds);
                noteEntity.setHashtags(hashtags);
            }
            PersonalNoteEntity savedNote = personalNoteRepository.save(noteEntity);
            return savedNote.getPersonalNoteNum();
        } catch (Exception e) {
            log.error("노트 저장 중 예외 발생", e);
            throw e;
        }
    }

    /**
     * PersonalNoteDTO를 PersonalNoteEntity로 변환하는 메서드
     *
     * @param noteDTO PersonalNoteDTO
     * @return PersonalNoteEntity
     */
    private PersonalNoteEntity convertToEntity(PersonalNoteDTO noteDTO) {
        EmotionEntity emotion = emotionRepository.findById(noteDTO.getEmotionNum())
                .orElseThrow(() -> new RuntimeException("감정 정보가 없습니다."));
        GrantedEntity granted = grantedRepository.findById(noteDTO.getGrantedNum())
                .orElseThrow(() -> new RuntimeException("공개 권한 정보가 없습니다."));
        PersonalDiaryEntity diary = personalDiaryRepository.findById(noteDTO.getDiaryNum())
                .orElseThrow(() -> new RuntimeException("다이어리 정보가 없습니다."));
        NoteTemplateEntity noteTemplate = noteTemplateRepository.findById(noteDTO.getPersonalNoteNum())
                .orElseThrow(() -> new RuntimeException("노트 템플릿 정보가 없습니다."));
        MemberEntity member = memberRepository.findById(noteDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));
        ProfileEntity profile = profileRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("프로필 정보가 없습니다."));

        return PersonalNoteEntity.builder()
                .noteTitle(noteDTO.getNoteTitle())
                .contents(noteDTO.getContents())
                .diaryDate(noteDTO.getDiaryDate())
                .likeCount(noteDTO.getLikeCount())
                .viewCount(noteDTO.getViewCount())
                .weather(noteDTO.getWeather())
                .emotion(emotion)
                .granted(granted)
                .location(noteDTO.getLocation())
                .fileName(noteDTO.getFileName())
                .personalDiary(diary)
                .noteTemplate(noteTemplate)
                .member(member)
                .profile(profile)
                .build();
    }

    /**
     * 특정 노트에 연결된 해시태그를 불러오는 메서드
     */
    public List<String> getHashtagsByNoteNum(Integer noteNum) {
        List<PersonalNoteHashtagEntity> noteHashtags = personalNoteHashtagRepository.findByPersonalNote_PersonalNoteNum(noteNum);
        return noteHashtags.stream()
                .map(noteHashtag -> noteHashtag.getHashtag().getHashtagName())
                .collect(Collectors.toList());
    }
}