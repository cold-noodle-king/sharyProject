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

    /**
     * 다이어리 번호로 PersonalNote 목록을 가져오는 메서드
     *
     * @param diaryNum 다이어리 번호
     * @return PersonalNoteDTO 리스트
     */
    public List<PersonalNoteDTO> getNotesByDiaryNum(Integer diaryNum) {
        // 다이어리 번호로 PersonalNote 목록 조회
        List<PersonalNoteEntity> noteEntities = personalNoteRepository.findByPersonalDiary_PersonalDiaryNum(diaryNum);

        // PersonalNoteEntity를 PersonalNoteDTO로 변환하여 반환
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
        // 해시태그 번호 리스트로 변환
        List<Integer> hashtagNums = note.getHashtags().stream()
                .map(HashtagEntity::getHashtagNum)
                .collect(Collectors.toList());

        // NoteTemplate 정보 추가
        NoteTemplateDTO noteTemplateDTO = null;
        if (note.getNoteTemplate() != null) {
            noteTemplateDTO = NoteTemplateDTO.builder()
                    .noteNum(note.getNoteTemplate().getNoteNum())
                    .noteName(note.getNoteTemplate().getNoteName())
                    .noteImage(note.getNoteTemplate().getNoteImage()) // 템플릿 이미지 추가
                    .build();
        }

        // PersonalNoteEntity에서 필요한 값들을 PersonalNoteDTO로 변환
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
                .noteTemplate(noteTemplateDTO) // 노트 템플릿 정보 추가
                .build();
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
            // PersonalNoteDTO를 PersonalNoteEntity로 변환
            PersonalNoteEntity noteEntity = convertToEntity(noteDTO);

            // 해시태그가 있는 경우 해시태그를 엔티티에 설정
            if (hashtagIds != null && !hashtagIds.isEmpty()) {
                List<HashtagEntity> hashtags = hashtagRepository.findAllById(hashtagIds);
                noteEntity.setHashtags(hashtags);
            }

            // 노트 저장
            PersonalNoteEntity savedNote = personalNoteRepository.save(noteEntity);
            return savedNote.getPersonalNoteNum();

        } catch (Exception e) {
            // 저장 중 예외 발생 시 로그 출력
            log.error("노트 저장 중 예외 발생", e);
            throw e; // 예외를 다시 던져서 상위에서 처리하게 함
        }
    }

    /**
     * PersonalNoteDTO를 PersonalNoteEntity로 변환하는 메서드
     *
     * @param noteDTO PersonalNoteDTO
     * @return PersonalNoteEntity
     */
    private PersonalNoteEntity convertToEntity(PersonalNoteDTO noteDTO) {
        try {
            // 관련 엔티티 조회
            // 감정 정보 조회
            EmotionEntity emotion = emotionRepository.findById(noteDTO.getEmotionNum())
                    .orElseThrow(() -> new RuntimeException("감정 정보가 없습니다."));

            // 공개 권한 정보 조회
            GrantedEntity granted = grantedRepository.findById(noteDTO.getGrantedNum())
                    .orElseThrow(() -> new RuntimeException("공개 권한 정보가 없습니다."));

            // 다이어리 정보 조회
            PersonalDiaryEntity diary = personalDiaryRepository.findById(noteDTO.getDiaryNum())
                    .orElseThrow(() -> new RuntimeException("다이어리 정보가 없습니다."));

            // 노트 템플릿 정보 조회 (올바른 템플릿 번호로 변경)
            NoteTemplateEntity noteTemplate = noteTemplateRepository.findById(noteDTO.getPersonalNoteNum())
                    .orElseThrow(() -> new RuntimeException("노트 템플릿 정보가 없습니다."));

            // 회원 정보 조회
            MemberEntity member = memberRepository.findById(noteDTO.getMemberId())
                    .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다."));

            // 프로필 정보 조회
            ProfileEntity profile = profileRepository.findByMember(member)
                    .orElseThrow(() -> new RuntimeException("프로필 정보가 없습니다."));

            // PersonalNoteEntity 생성 및 값 설정
            return PersonalNoteEntity.builder()
                    .noteTitle(noteDTO.getNoteTitle() != null ? noteDTO.getNoteTitle() : "제목 없음")
                    .contents(noteDTO.getContents())
                    .diaryDate(noteDTO.getDiaryDate() != null ? noteDTO.getDiaryDate() : new Timestamp(System.currentTimeMillis()))
                    .likeCount(noteDTO.getLikeCount() != null ? noteDTO.getLikeCount() : 0)
                    .viewCount(noteDTO.getViewCount() != null ? noteDTO.getViewCount() : 0)
                    .weather(noteDTO.getWeather() != null ? noteDTO.getWeather() : "Unknown")
                    .emotion(emotion)
                    .granted(granted)
                    .location(noteDTO.getLocation())
                    .fileName(noteDTO.getFileName())
                    .personalDiary(diary)
                    .noteTemplate(noteTemplate) // 노트 템플릿 설정
                    .member(member) // 회원 설정
                    .profile(profile) // 프로필 설정
                    .build();

        } catch (Exception e) {
            log.error("엔티티 변환 중 예외 발생", e);
            throw e; // 예외를 다시 던져 상위에서 처리
        }
    }
}
