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

import java.util.ArrayList;
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
                        .shareNoteNum(like.getShareNote().getShareNoteNum())
                        .memberId(like.getMember().getMemberId())
                        .likeClicked(like.getLikeClicked())
                        .build())
                .collect(Collectors.toList());

        return ShareNoteDTO.builder()
                .shareNoteNum(note.getShareNoteNum())
                .shareNoteTitle(note.getShareNoteTitle())
                .memberId(note.getMember().getMemberId())
                .nickname(note.getMember().getNickname())
                .contents(note.getContents())
                .diaryDate(note.getDiaryDate())
                .likeCount(note.getLikeCount())
                .weather(note.getWeather())
                .emotionName(note.getEmotion().getEmotionName())
                .hashtagNums(hashtagNums)
                .location(note.getLocation())
                .fileName(note.getFileName())
                .createdDate(note.getCreatedDate())
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
                            .member(memberRepository.findById(likeDTO.getMemberId())
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

    /**
     * 좋아요 버튼 클릭 시 중복을 체크하고 좋아요 수를 반환하는 메서드
     * @param noteNum
     * @param userName
     * @return
     */
    public LikeResponseDTO like(Integer noteNum, Integer emotionNum, String userName) {
        // 게시물 및 사용자 조회
        ShareNoteEntity shareNoteEntity = shareNoteRepository.findById(noteNum)
                .orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));
        MemberEntity memberEntity = memberRepository.findById(userName)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // emotionNum에 따른 emotionName 설정
        String emotionName = "";
        switch (emotionNum) {
            case 1: emotionName = "기쁨"; break;
            case 2: emotionName = "사랑"; break;
            case 3: emotionName = "슬픔"; break;
            case 4: emotionName = "화남"; break;
            case 5: emotionName = "놀람"; break;
        }

        // 이미 좋아요를 눌렀는지 확인
        ShareLikeEntity likeEntity = shareLikeRepository.findByShareNoteAndMember(shareNoteEntity, memberEntity);
        boolean isLiked = (likeEntity != null); // 이미 좋아요를 눌렀는지 여부

        int cnt;
        if (isLiked) {
            // 이미 좋아요를 눌렀다면 좋아요 취소 (likeEntity 삭제)
            shareLikeRepository.delete(likeEntity);
            cnt = shareNoteEntity.getLikeCount() - 1;
            shareNoteEntity.setLikeCount(cnt); // 좋아요 수 감소
        } else {
            // 좋아요 추가
            cnt = shareNoteEntity.getLikeCount() + 1;
            shareNoteEntity.setLikeCount(cnt); // 좋아요 수 증가

            // 새롭게 좋아요 엔티티 생성
            ShareLikeEntity newLikeEntity = ShareLikeEntity.builder()
                    .member(memberEntity)
                    .shareNote(shareNoteEntity)
                    .likeClicked(true)
                    .emotionName(emotionName) // 감정 설정
                    .build();
            shareLikeRepository.save(newLikeEntity);
        }

        // 좋아요 수 업데이트
        shareNoteRepository.save(shareNoteEntity);

        // 감정별 카운트 가져오기
        List<Object[]> emotionCounts = shareLikeRepository.countByEmotionName(noteNum);
        int joyCnt = 0, loveCnt = 0, sadCnt = 0, angryCnt = 0, wowCnt = 0;

        // 각 감정의 카운트를 DTO에 매핑
        for (Object[] result : emotionCounts) {
            String emotion = (String) result[0];
            Long count = (Long) result[1];

            switch (emotion) {
                case "기쁨": joyCnt = count.intValue(); break;
                case "사랑": loveCnt = count.intValue(); break;
                case "슬픔": sadCnt = count.intValue(); break;
                case "화남": angryCnt = count.intValue(); break;
                case "놀람": wowCnt = count.intValue(); break;
            }
        }

        // LikeResponseDTO 반환
        return LikeResponseDTO.builder()
                .isLiked(!isLiked) // 좋아요가 눌렸는지 여부 반전
                .cnt(cnt) // 좋아요 수
                .emotionName(emotionName) // 감정 이름 반환
                .joyCnt(joyCnt) // 감정별 카운트
                .loveCnt(loveCnt)
                .sadCnt(sadCnt)
                .angryCnt(angryCnt)
                .wowCnt(wowCnt)
                .build();
    }

    public LikeResponseDTO getLikeInfo(Integer noteNum, String username) {
        // 노트와 사용자 정보 가져오기
        ShareNoteEntity noteEntity = shareNoteRepository.findById(noteNum)
                .orElseThrow(() -> new IllegalArgumentException("Invalid note number: " + noteNum));
        MemberEntity memberEntity = memberRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member username: " + username));

        // 사용자가 해당 노트에 좋아요를 눌렀는지 확인
        ShareLikeEntity userLike = shareLikeRepository.findByShareNoteAndMember(noteEntity, memberEntity);
        boolean isLiked = userLike != null;

        // 감정별 좋아요 개수 계산
        List<Object[]> emotionCounts = shareLikeRepository.countByEmotionName(noteNum);

        // emotionCounts가 null일 경우 예외 처리
        if (emotionCounts == null) {
            emotionCounts = new ArrayList<>();
        }

        // 감정별 카운트 변수 초기화
        int joyCnt = 0, loveCnt = 0, sadCnt = 0, angryCnt = 0, wowCnt = 0;

        // 감정별 카운트를 emotionCounts에서 추출
        for (Object[] result : emotionCounts) {
            if (result[0] != null && result[1] != null) {
                String emotionName = (String) result[0];
                Long count = (Long) result[1];
                switch (emotionName) {
                    case "기쁨":
                        joyCnt = count.intValue();
                        break;
                    case "사랑":
                        loveCnt = count.intValue();
                        break;
                    case "슬픔":
                        sadCnt = count.intValue();
                        break;
                    case "화남":
                        angryCnt = count.intValue();
                        break;
                    case "놀람":
                        wowCnt = count.intValue();
                        break;
                }
            }
        }

        // 총 좋아요 수 계산
        int cnt = joyCnt + loveCnt + sadCnt + angryCnt + wowCnt;

        // LikeResponseDTO 생성 및 반환
        return LikeResponseDTO.builder()
                .cnt(cnt)
                .isLiked(isLiked)
                .joyCnt(joyCnt)
                .loveCnt(loveCnt)
                .sadCnt(sadCnt)
                .angryCnt(angryCnt)
                .wowCnt(wowCnt)
                .build();
    }



}
