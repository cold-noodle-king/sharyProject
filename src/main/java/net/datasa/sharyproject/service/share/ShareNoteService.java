package net.datasa.sharyproject.service.share;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.dto.share.ShareNoteDTO;
import net.datasa.sharyproject.domain.entity.EmotionEntity;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.domain.entity.personal.NoteTemplateEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareNoteEntity;
import net.datasa.sharyproject.domain.entity.share.ShareNoteHashtagEntity;
import net.datasa.sharyproject.repository.EmotionRepository;
import net.datasa.sharyproject.repository.HashtagRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.mypage.ProfileRepository;
import net.datasa.sharyproject.repository.personal.*;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.repository.share.ShareNoteHashtagRepository;
import net.datasa.sharyproject.repository.share.ShareNoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class ShareNoteService {

    private final ShareNoteRepository shareNoteRepository;
    private final HashtagRepository hashtagRepository;
    private final EmotionRepository emotionRepository;
    private final ShareDiaryRepository shareDiaryRepository;
    private final NoteTemplateRepository noteTemplateRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final ShareNoteHashtagRepository shareNoteHashtagRepository;

    private static final Logger log = LoggerFactory.getLogger(ShareNoteService.class);

    /**
     * 다이어리 번호로 ShareNote 목록을 가져오는 메서드
     *
     * @param diaryNum 다이어리 번호
     * @return ShareNoteDTO 리스트
     */
    public List<ShareNoteDTO> getNotesByDiaryNum(Integer diaryNum) {
        log.info("다이어리 번호 {}로 ShareNote 목록 조회 중", diaryNum);
        List<ShareNoteEntity> noteEntities = shareNoteRepository.findByShareDiary_ShareDiaryNum(diaryNum);
        log.debug("다이어리 번호로 조회한 노트:{}", noteEntities);
        return noteEntities.stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 노트 번호로 노트를 조회하는 메서드
     * @param noteNum 노트 번호
     * @return ShareNoteDTO
     */
    public ShareNoteDTO getNoteByNum(Integer noteNum) {
        ShareNoteEntity noteEntity = shareNoteRepository.findById(noteNum)
                .orElseThrow(() -> new RuntimeException("해당 노트 정보를 찾을 수 없습니다."));

        return convertEntityToDTO(noteEntity);  // DTO 변환 후 반환
    }

    /**
     * ShareNoteEntity를 ShareNoteDTO로 변환하는 메서드
     *
     * @param note ShareNoteEntity
     * @return ShareNoteDTO
     */
    private ShareNoteDTO convertEntityToDTO(ShareNoteEntity note) {
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

        return ShareNoteDTO.builder()
                .shareNoteNum(note.getShareNoteNum())
                .shareNoteTitle(note.getShareNoteTitle())
                .contents(note.getContents())
                .diaryDate(note.getDiaryDate())
                .likeCount(note.getLikeCount())
                .weather(note.getWeather())
                .emotionName(note.getEmotion().getEmotionName())
                .hashtagNums(hashtagNums)
                .location(note.getLocation())
                .fileName(note.getFileName())
                .shareDiaryNum(note.getShareDiary().getShareDiaryNum())
                .noteTemplate(noteTemplateDTO)
                .profilePicture(profilePicture) // Profile 정보 추가
                .build();
    }

    /**
     * 노트 번호로 노트를 조회하는 메서드
     * @return ShareNoteDTO
     */
    /*@Transactional
    public void saveNote(ShareNoteDTO noteDTO, List<Integer> hashtags) {
        // 1. MemberEntity 조회
        MemberEntity member = memberRepository.findById(noteDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 memberId를 가진 멤버를 찾을 수 없습니다: " + noteDTO.getMemberId()));

        // 2. ShareDiaryEntity 조회
        ShareDiaryEntity shareDiary = shareDiaryRepository.findById(noteDTO.getShareDiaryNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 다이어리를 찾을 수 없습니다: " + noteDTO.getShareDiaryNum()));

        // 3. NoteTemplateEntity 조회
        NoteTemplateEntity noteTemplate = noteTemplateRepository.findById(noteDTO.getNoteTemplate().getNoteNum())
                .orElseThrow(() -> new IllegalArgumentException("노트 템플릿을 찾을 수 없습니다: " + noteDTO.getNoteTemplate().getNoteNum()));

        // 4. EmotionEntity 조회
        EmotionEntity emotion = emotionRepository.findById(noteDTO.getEmotionNum())
                .orElseThrow(() -> new IllegalArgumentException("Emotion을 찾을 수 없습니다: " + noteDTO.getEmotionNum()));

        // 5. ProfileEntity 조회 (옵션)
        ProfileEntity profile = profileRepository.findByMember(member).orElse(null);

        // 6. ShareNoteEntity 생성 및 필드 설정
        ShareNoteEntity shareNoteEntity = ShareNoteEntity.builder()
                .shareDiary(shareDiary)
                .noteTemplate(noteTemplate)
                .shareNoteTitle(noteDTO.getShareNoteTitle())
                .diaryDate(noteDTO.getDiaryDate())
                .emotion(emotion) // EmotionEntity 설정
                .location(noteDTO.getLocation())
                .contents(noteDTO.getContents())
                .likeCount(noteDTO.getLikeCount())
                .weather(noteDTO.getWeather())
                .member(member)
                .profile(profile)
                .build();

        // 7. Hashtags 설정
        if (hashtags != null && !hashtags.isEmpty()) {
            List<HashtagEntity> hashtagEntities = hashtagRepository.findAllById(hashtags);
            shareNoteEntity.setHashtags(hashtagEntities); // List<HashtagEntity> 설정
        }

        // 8. 엔티티 저장
        shareNoteRepository.save(shareNoteEntity);
    }*/

    @org.springframework.transaction.annotation.Transactional
    public Integer saveNote(ShareNoteDTO noteDTO, List<Integer> hashtagIds) {
        try {
            ShareNoteEntity noteEntity = convertToEntity(noteDTO);
            if (hashtagIds != null && !hashtagIds.isEmpty()) {
                List<HashtagEntity> hashtags = hashtagRepository.findAllById(hashtagIds);
                noteEntity.setHashtags(hashtags);
            }
            ShareNoteEntity savedNote = shareNoteRepository.save(noteEntity);
            return savedNote.getShareNoteNum();
        } catch (Exception e) {
            log.error("노트 저장 중 예외 발생", e);
            throw e;
        }
    }

    /**
     * ShareNoteDTO를 ShareNoteEntity로 변환하는 메서드
     *
     * @param noteDTO ShareNoteDTO
     * @return ShareNoteEntity
     */
    private ShareNoteEntity convertToEntity(ShareNoteDTO noteDTO) {
        EmotionEntity emotion = emotionRepository.findById(noteDTO.getEmotionNum())
                .orElseThrow(() -> new RuntimeException("감정 정보를 찾을 수 없습니다."));

        ShareDiaryEntity diary = shareDiaryRepository.findById(noteDTO.getShareDiaryNum())
                .orElseThrow(() -> new RuntimeException("다이어리 정보를 찾을 수 없습니다."));

        // NoteTemplateEntity가 존재하는지 확인 후 가져오기
        NoteTemplateEntity noteTemplate = noteTemplateRepository.findById(noteDTO.getNoteTemplate().getNoteNum())
                .orElseThrow(() -> new RuntimeException("노트 템플릿 정보를 찾을 수 없습니다."));

        MemberEntity member = memberRepository.findById(noteDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        ProfileEntity profile = profileRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("프로필 정보가 없습니다."));

        return ShareNoteEntity.builder()
                .shareNoteTitle(noteDTO.getShareNoteTitle())
                .contents(noteDTO.getContents())
                .diaryDate(noteDTO.getDiaryDate())
                .likeCount(noteDTO.getLikeCount())
                .weather(noteDTO.getWeather())
                .emotion(emotion)
                .location(noteDTO.getLocation())
                .fileName(noteDTO.getFileName())
                .shareDiary(diary)
                .noteTemplate(noteTemplate)  // noteTemplate 설정
                .member(member)
                .profile(profile)
                .build();
    }

    /**
     * 특정 노트에 연결된 해시태그를 불러오는 메서드
     */
    public List<String> getHashtagsByNoteNum(Integer noteNum) {
        log.info("노트 번호 {}에 연결된 해시태그 조회 중", noteNum);
        List<ShareNoteHashtagEntity> noteHashtags = shareNoteHashtagRepository.findByShareNote_ShareNoteNum(noteNum);
        return noteHashtags.stream()
                .map(noteHashtag -> noteHashtag.getHashtag().getHashtagName())
                .collect(Collectors.toList());
    }
}
