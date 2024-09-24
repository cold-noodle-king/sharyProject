package net.datasa.sharyproject.service.share;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.dto.share.LikeResponseDTO;
import net.datasa.sharyproject.domain.dto.share.ShareLikeDTO;
import net.datasa.sharyproject.domain.dto.share.ShareNoteDTO;
import net.datasa.sharyproject.domain.entity.EmotionEntity;
import net.datasa.sharyproject.domain.entity.HashtagEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.domain.entity.personal.NoteTemplateEntity;
import net.datasa.sharyproject.domain.entity.personal.PersonalNoteEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareLikeEntity;
import net.datasa.sharyproject.domain.entity.share.ShareNoteEntity;
import net.datasa.sharyproject.domain.entity.share.ShareNoteHashtagEntity;
import net.datasa.sharyproject.repository.EmotionRepository;
import net.datasa.sharyproject.repository.HashtagRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.mypage.ProfileRepository;
import net.datasa.sharyproject.repository.personal.*;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.repository.share.ShareLikeRepository;
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
    private final ShareLikeRepository shareLikeRepository;

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

        // 좋아요 정보 가져오기
        List<ShareLikeDTO> likes = note.getLikeList().stream()
                .map(like -> ShareLikeDTO.builder()
                        .likeNum(like.getLikeNum())
                        .shareNote(like.getShareNote())
                        .member(like.getMember())
                        .likeClicked(like.getLikeClicked())
                        .build())
                .collect(Collectors.toList());

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
                .likeList(likes)  // Like 정보 추가
                .build();
    }



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

        ShareNoteEntity shareNoteEntity = ShareNoteEntity.builder()
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

        // 좋아요 리스트 변환
        if (noteDTO.getLikeList() != null && !noteDTO.getLikeList().isEmpty()) {
            List<ShareLikeEntity> likeEntities = noteDTO.getLikeList().stream()
                    .map(likeDTO -> ShareLikeEntity.builder()
                            .likeNum(likeDTO.getLikeNum())
                            .member(memberRepository.findById(likeDTO.getMember().getMemberId())
                                    .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다.")))
                            .shareNote(shareNoteEntity)  // 연관된 ShareNoteEntity 설정
                            .likeClicked(likeDTO.isLikeClicked())
                            .build())
                    .collect(Collectors.toList());

            shareNoteEntity.setLikeList(likeEntities);  // Like 리스트 설정
        }

        return shareNoteEntity;
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

    public LikeResponseDTO like(Integer num, String userName){
        ShareNoteEntity shareNoteEntity = shareNoteRepository.findById(num)
                .orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        MemberEntity memberEntity = memberRepository.findById(userName)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        boolean isLiked = false;
        for (ShareLikeEntity entity : shareNoteEntity.getLikeList()){
            if (entity.getMember().getMemberId().equals(userName)){
                isLiked = true;
                break;
            }
        }

        int cnt = 0;
        if (isLiked){
            cnt = shareNoteEntity.getLikeCount();
        } else {
            cnt = shareNoteEntity.getLikeCount() + 1;
            shareNoteEntity.setLikeCount(cnt);

            ShareLikeEntity likeEntity = ShareLikeEntity.builder()
                    .member(memberEntity)
                    .shareNote(shareNoteEntity)
                    .likeClicked(true)
                    .build();

            shareLikeRepository.save(likeEntity);
        }

        // LikeReponseDTO 만들어서 cnt 랑 isLiked 셋팅해서 리턴하기
        return LikeResponseDTO.builder().isLiked(isLiked).cnt(cnt).build();
    }
}
