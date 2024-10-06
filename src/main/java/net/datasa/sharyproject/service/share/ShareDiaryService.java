package net.datasa.sharyproject.service.share;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.share.ShareDiaryDTO;
import net.datasa.sharyproject.domain.dto.share.ShareMemberDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.member.UserCategoryEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.domain.entity.personal.CoverTemplateEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareMemberEntity;
import net.datasa.sharyproject.domain.entity.sse.NotificationEntity; //추가
import net.datasa.sharyproject.service.mypage.ProfileService;
import net.datasa.sharyproject.service.sse.SseService;  // 추가
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.CategoryRepository;
import net.datasa.sharyproject.repository.member.UserCategoryRepository;
import net.datasa.sharyproject.repository.personal.CoverTemplateRepository;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.repository.share.ShareMemberRepository;
import net.datasa.sharyproject.repository.sse.NotificationRepository;
import net.datasa.sharyproject.security.AuthenticatedUser;
import org.apache.catalina.User;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;




@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ShareDiaryService {

    private final ShareDiaryRepository shareDiaryRepository;
    private final MemberRepository memberRepository;
    private final CoverTemplateRepository coverTemplateRepository;
    private final CategoryRepository categoryRepository;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactory;
    private final ShareMemberRepository shareMemberRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final ProfileService profileService;

    // 추가된 부분(윤조 알림)
    private final SseService sseService;
    private final NotificationRepository notificationRepository;
    
    /**
     * 생성한 다이어리를 저장하는 메서드
     * @param shareDiaryDTO 생성한 다이어리 정보를 담은 DTO
     * @param user 다이어리를 생성한 유저 정보
     * @return
     */
    public ShareDiaryEntity saveDiary(ShareDiaryDTO shareDiaryDTO, AuthenticatedUser user){
        MemberEntity memberEntity = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        CoverTemplateEntity coverTemplateEntity = coverTemplateRepository.findById(shareDiaryDTO.getCoverTemplateNum())
                .orElseThrow(() -> new EntityNotFoundException("커버 템플릿을 불러올 수 없습니다."));

        switch (shareDiaryDTO.getCategoryName()){
            case "일상":
                shareDiaryDTO.setCategoryNum(1); break;
            case "여행":
                shareDiaryDTO.setCategoryNum(2); break;
            case "육아":
                shareDiaryDTO.setCategoryNum(3); break;
            case "연애":
                shareDiaryDTO.setCategoryNum(4); break;
            case "취미":
                shareDiaryDTO.setCategoryNum(5); break;
            case "운동":
                shareDiaryDTO.setCategoryNum(6); break;
            default:
                break;
        }
        CategoryEntity categoryEntity = categoryRepository.findById(shareDiaryDTO.getCategoryNum())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 불러올 수 없습니다."));

        ShareDiaryEntity shareDiaryEntity = ShareDiaryEntity.builder()
                .shareDiaryName(shareDiaryDTO.getShareDiaryName())
                .member(memberEntity)
                .category(categoryEntity)
                .coverTemplate(coverTemplateEntity)
                .build();

        shareDiaryRepository.save(shareDiaryEntity);

        log.debug("저장되는 엔티티:{}", shareDiaryEntity);

        return shareDiaryEntity;
    }

    /**
     * 사용자가 생성한 다이어리 리스트를 가져오는 메서드
     * @param memberId 현재 로그인한 사용자의 ID
     * @return 다이어리 리스트 반환
     */
    public List<ShareDiaryDTO> getCreatedList(String memberId){

        List<ShareDiaryEntity> shareDiaryEntities = shareDiaryRepository.findByMember_MemberId(memberId);

        // 조회한 엔티티를 DTO로 변환하여 반환
        return shareDiaryEntities.stream()
                .map(this::convertEntityToDTO) // 엔티티를 DTO로 변환하는 메서드 호출
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 가입한 다이어리 리스트를 가져오는 메서드
     * @param memberId 현재 로그인한 사용자 ID
     * @return
     */
    public List<ShareDiaryDTO> getJoinedList(String memberId){
        // 특정 memberId에 해당하며, status가 ACCEPTED인 다이어리 리스트 조회
        List<ShareDiaryEntity> acceptedShareDiaries = shareMemberRepository.findAcceptedShareDiariesByMemberId(memberId);

        // 조회한 엔티티를 DTO로 변환하여 반환
        return acceptedShareDiaries.stream()
                .map(this::convertEntityToDTO) // 엔티티를 DTO로 변환하는 메서드 호출
                .collect(Collectors.toList());

    }

    /**
     * 사용자가 가입한 특정 다이어리를 가져오는 메서드
     * @param diaryNum 해당 다이어리 넘버
     * @param memberId 사용자 ID
     * @return
     */
    public ShareDiaryDTO getJoinedDiary(Integer diaryNum, String memberId){
        // 특정 다이어리 번호와 사용자 ID로 상태가 ACCEPTED인 다이어리 조회
        ShareDiaryEntity shareDiaryEntity = shareMemberRepository.findAcceptedShareDiaryByDiaryNumAndMemberId(diaryNum, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 다이어리나 사용자가 없거나, 가입 요청이 승인되지 않았습니다."));

        // 조회한 엔티티를 DTO로 변환하여 반환
        return convertEntityToDTO(shareDiaryEntity);
    }

    /**
     * 특정 다이어리 하나를 가져오는 메서드
     * @param diaryNum
     * @return
     */
    public ShareDiaryDTO getDiary(Integer diaryNum){
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리 정보를 찾을 수 없습니다."));

        return convertEntityToDTO(shareDiaryEntity);
    }

    /**
     * 전체 공유 다이어리 리스트를 가져오는 메서드
     * @return
     */
    public List<ShareDiaryDTO> getDiaryList(){

        Sort sort = Sort.by(Sort.Direction.DESC, "shareDiaryNum");

        List<ShareDiaryEntity> shareDiaryEntities = shareDiaryRepository.findAll(sort);
        List<ShareDiaryDTO> shareDiaryList = new ArrayList<>();

        for (ShareDiaryEntity shareDiaryEntity : shareDiaryEntities) {
            ShareDiaryDTO shareDiaryDTO = convertEntityToDTO(shareDiaryEntity);

            // 다이어리 번호를 가져와 멤버 수를 조회
            Integer diaryNum = shareDiaryEntity.getShareDiaryNum();
            int memberCount = getMemberCount(diaryNum);

            // 멤버 수를 DTO에 설정
            shareDiaryDTO.setMemberCount(memberCount);

            shareDiaryList.add(shareDiaryDTO);
        }

        log.debug("가져온 리스트 정보!!!!:{}", shareDiaryList);
        return shareDiaryList;
    }

    /**
     * 사용자의 관심 카테고리별 다이어리 리스트를 가져오는 메서드
     * @param user
     * @return List<ShareDiaryDTO>
     */
    public List<ShareDiaryDTO> listedByUserCategory(AuthenticatedUser user) {
        // 사용자의 UserCategory 리스트 가져오기
        List<UserCategoryEntity> userCategories = userCategoryRepository.findByMember_MemberId(user.getUsername());

        // 사용자의 관심 카테고리명 리스트 추출
        List<String> categoryNames = userCategories.stream()
                .map(uc -> uc.getCategory().getCategoryName())
                .collect(Collectors.toList());


        // 카테고리에 해당하는 다이어리 리스트 가져오기
        List<ShareDiaryEntity> diaries = shareDiaryRepository.findByCategoryNames(categoryNames);

        // ShareDiaryEntity를 ShareDiaryDTO로 변환하여 반환
        return diaries.stream()
                .map(this::convertEntityToDTO)  // 엔티티를 DTO로 변환하는 매퍼 메서드 가정
                .collect(Collectors.toList());
    }

    public List<String> getUserCategories(AuthenticatedUser user){

        // 사용자의 UserCategory 리스트 가져오기
        List<UserCategoryEntity> userCategories = userCategoryRepository.findByMember_MemberId(user.getUsername());

        // 사용자의 관심 카테고리명 리스트 추출
        List<String> categoryNames = userCategories.stream()
                .map(uc -> uc.getCategory().getCategoryName())
                .collect(Collectors.toList());

        log.debug("카테고리 이름 추출:{}", categoryNames);
        return categoryNames;
    }

    /**
     * 각 카테고리별 다이어리 리스트를 가져오는 메서드
     * @param categoryName
     * @return
     */
    public List<ShareDiaryDTO> getCategoryList(String categoryName){
        Integer categoryNum = 0;
        switch (categoryName){
            case "일상":
                categoryNum = 1; break;
            case "여행":
                categoryNum = 2; break;
            case "육아":
                categoryNum = 3; break;
            case "연애":
                categoryNum = 4; break;
            case "취미":
                categoryNum = 5; break;
            case "운동":
                categoryNum = 5; break;
        }

        List<ShareDiaryEntity> entity = shareDiaryRepository.findByCategory_CategoryNum(categoryNum);
        log.debug("카테고리별 다이어리 리스트:{}", entity);


        // 각 다이어리별 멤버 수를 카운트하여 DTO에 추가
        return entity.stream()
                .map(shareDiaryEntity -> {
                    ShareDiaryDTO shareDiaryDTO = convertEntityToDTO(shareDiaryEntity);

                    // 다이어리별 멤버 수를 조회하여 DTO에 설정
                    int memberCount = getMemberCount(shareDiaryEntity.getShareDiaryNum());
                    shareDiaryDTO.setMemberCount(memberCount);

                    return shareDiaryDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * 공유 다이어리 소개글을 저장하는 메서드
     * @param diaryNum
     * @param diaryBio
     * @param memberId
     */
    public void updateBio(Integer diaryNum, String diaryBio, String memberId){
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리 정보를 찾을 수 없습니다."));

        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));

        shareDiaryEntity.setDiaryBio(diaryBio);

    }

    /**
     * 가입 요청한 사용자를 공유 멤버 테이블에 저장하는 메서드
     * @param diaryNum
     * @param memberId
     */
    public void join(Integer diaryNum, String memberId) {
        // 다이어리 정보 조회
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리 정보를 찾을 수 없습니다."));

        // 사용자 정보 조회
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));


        Optional<ShareDiaryEntity> existingRequest = shareMemberRepository
                .findPendingShareDiaryByDiaryNumAndMemberId(diaryNum, memberId);

        // 해당 다이어리와 멤버가 이미 가입되어 있는지 확인
        Optional<ShareDiaryEntity> existingMember = shareMemberRepository
                .findAcceptedShareDiaryByDiaryNumAndMemberId(diaryNum, memberId);

        if (existingRequest.isPresent()) {
            // 이미 가입 요청을 한 경우 예외를 던짐
            throw new IllegalStateException("이미 가입 요청을 보낸 다이어리입니다.");
        }

        if (existingMember.isPresent()) {
            // 이미 가입을 한 경우 예외를 던짐
            throw new IllegalStateException("이미 가입이 완료된 다이어리입니다.");
        }

        // 새로운 가입 요청 생성
        ShareMemberEntity shareMemberEntity = new ShareMemberEntity();
        shareMemberEntity.setMember(memberEntity);
        shareMemberEntity.setManager(shareDiaryEntity.getMember());
        shareMemberEntity.setShareDiary(shareDiaryEntity);
        shareMemberEntity.setJoinDate(LocalDateTime.now());
        shareMemberEntity.setStatus("PENDING"); // 가입 요청 시 기본 상태를 보류로 설정

        log.debug("저장되는 엔티티: {}", shareMemberEntity);

        // 새로운 요청 저장
        shareMemberRepository.save(shareMemberEntity);

        // **가입 요청 알림 생성 및 전송**
        MemberEntity managerEntity = shareDiaryEntity.getMember(); // 다이어리 매니저 정보
        String message = memberEntity.getNickname() + "님이 공유 다이어리 가입을 요청했습니다";

        // 알림 엔티티 생성 및 저장
        NotificationEntity notification = NotificationEntity.builder()
                .receiver(managerEntity)
                .content(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .notificationType("join_request")
                .build();
        notificationRepository.save(notification);

        // SSE 알림 전송
        sseService.sendNotification(managerEntity.getMemberId(), message, "join_request");
    }


    /**
     * 상태가 'PENDING'인 공유 멤버 리스트를 가져오는 메서드
     * @param diaryNum 공유 다이어리 번호
     * @return 상태가 'PENDING'인 공유 멤버 DTO 리스트
     */
    /**
     * 상태가 'PENDING'인 공유 멤버 리스트를 가져오는 메서드
     * @param diaryNum 공유 다이어리 번호
     * @return 상태가 'PENDING'인 공유 멤버 DTO 리스트
     */
    public List<ShareMemberDTO> getPendingMembers(Integer diaryNum) {
        // 공유 다이어리 엔티티 조회
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리를 찾을 수 없습니다."));

        // 상태가 'PENDING'인 공유 멤버 엔티티 리스트 조회
        List<ShareMemberEntity> pendingMembers = shareMemberRepository.findByShareDiaryAndStatus(shareDiaryEntity, "PENDING");

        // 엔티티를 DTO로 변환하면서 프로필 정보 추가
        List<ShareMemberDTO> pendingMemberDTOs = pendingMembers.stream()
                .map(shareMemberEntity -> {
                    MemberEntity member = shareMemberEntity.getMember();

                    // 프로필 정보 조회
                    ProfileEntity profile = profileService.findByMember(member)
                            .orElseGet(() -> {
                                // 프로필 정보가 없으면 기본 프로필 생성
                                ProfileEntity defaultProfile = ProfileEntity.builder()
                                        .member(member)
                                        .profilePicture("/images/profile.png") // 기본 이미지 설정
                                        .ment("") // 기본 소개글 설정
                                        .build();
                                profileService.saveProfile(defaultProfile);
                                return defaultProfile;
                            });

                    // DTO 변환
                    return ShareMemberDTO.builder()
                            .shareMemberNum(shareMemberEntity.getShareMemberNum())
                            .memberId(member.getMemberId())
                            .nickname(member.getNickname())
                            .profilePicture(profile.getProfilePicture()) // 프로필 사진 추가
                            .status(shareMemberEntity.getStatus())
                            .joinDate(shareMemberEntity.getJoinDate())
                            // 필요한 다른 필드들...
                            .build();
                })
                .collect(Collectors.toList());

        log.debug("가입 요청 리스트: {}", pendingMemberDTOs);
        return pendingMemberDTOs;
    }

   /* public List<ShareMemberDTO> getPendingMembers(Integer diaryNum) {
        // 공유 다이어리 엔티티 조회
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리를 찾을 수 없습니다."));

        // 상태가 'PENDING'인 공유 멤버 엔티티 리스트 조회
        List<ShareMemberEntity> pendingMembers = shareMemberRepository.findByShareDiaryAndStatus(shareDiaryEntity, "PENDING");

        // 엔티티를 DTO로 변환
        List<ShareMemberDTO> pendingMemberDTOs = pendingMembers.stream()
                .map(this::convertShareMemberEntityToDTO)
                .collect(Collectors.toList());

        log.debug("가입 요청 리스트:{}", pendingMemberDTOs);
        return pendingMemberDTOs;
    }
*/
    /**
     * 공유 다이어리 가입 요청을 수락하는 메서드
     * @param diaryNum
     * @param memberId
     */
    public void acceptRegister(Integer diaryNum, String memberId){
        // 공유 멤버 엔티티 조회
        ShareMemberEntity entity = shareMemberRepository
                .findByShareDiary_ShareDiaryNumAndMember_MemberId(diaryNum, memberId)
                .orElseThrow(() -> new EntityNotFoundException("가입 요청을 찾을 수 없습니다."));

        log.debug("가져온 회원 정보:{}", entity);

        // 상태를 ACCEPTED로 변경하고 가입 날짜 설정
        entity.setStatus("ACCEPTED");
        entity.setJoinDate(LocalDateTime.now());

        // 변경된 엔티티를 저장
        shareMemberRepository.save(entity);

        // 가입 수락 알림 생성 및 전송
        MemberEntity requester = entity.getMember(); // 가입 요청한 사용자
        MemberEntity manager = entity.getManager();  // 다이어리 매니저

        String message = manager.getNickname() + "님이 공유 다이어리 가입 요청을 수락했습니다.";

        // 알림 엔티티 생성 및 저장
        NotificationEntity notification = NotificationEntity.builder()
                .receiver(requester)
                .content(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .notificationType("join_accept")
                .build();
        notificationRepository.save(notification);

        // SSE 알림 전송
        sseService.sendNotification(requester.getMemberId(), message, "join_accept");
    }

    public List<ShareMemberDTO> getMemberList(Integer diaryNum, String memberId) {
        // 공유 다이어리 엔티티 조회
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리를 찾을 수 없습니다."));

        // 상태가 'ACCEPTED'인 공유 멤버 엔티티 리스트 조회
        List<ShareMemberEntity> memberList = shareMemberRepository.findByShareDiaryAndStatus(shareDiaryEntity, "ACCEPTED");

        // 엔티티를 DTO로 변환하면서 프로필 정보 추가
        List<ShareMemberDTO> memberDTOs = memberList.stream()
                .map(shareMemberEntity -> {
                    MemberEntity member = shareMemberEntity.getMember();

                    // 프로필 정보 조회
                    ProfileEntity profile = profileService.findByMember(member)
                            .orElseGet(() -> {
                                // 프로필 정보가 없으면 기본 프로필 생성
                                ProfileEntity defaultProfile = ProfileEntity.builder()
                                        .member(member)
                                        .profilePicture("/images/profile.png") // 기본 이미지 설정
                                        .ment("") // 기본 소개글 설정
                                        .build();
                                profileService.saveProfile(defaultProfile);
                                return defaultProfile;
                            });

                    // DTO 변환
                    return ShareMemberDTO.builder()
                            .shareMemberNum(shareMemberEntity.getShareMemberNum())
                            .memberId(member.getMemberId())
                            .nickname(member.getNickname())
                            .profilePicture(profile.getProfilePicture()) // 프로필 사진 추가
                            .status(shareMemberEntity.getStatus())
                            .joinDate(shareMemberEntity.getJoinDate())
                            // 필요한 다른 필드들...
                            .build();
                })
                .collect(Collectors.toList());

        log.debug("멤버 리스트:{}", memberDTOs);
        return memberDTOs;
    }

    /**
     * 공유 다이어리별 멤버 수를 가져오는 메서드
     * @param diaryNum
     * @return
     */
    public int getMemberCount(Integer diaryNum) {
        return shareMemberRepository.countByShareDiary_ShareDiaryNumAndStatus(diaryNum, "ACCEPTED");
    }

    /**
     * ShareMemberEntity를 ShareMemberDTO로 변환하는 헬퍼 메서드
     * @param member
     * @return
     */
    private ShareMemberDTO convertShareMemberEntityToDTO(ShareMemberEntity member) {
        return ShareMemberDTO.builder()
                .shareMemberNum(member.getShareMemberNum())
                .memberId(member.getMember().getMemberId())
                .nickname(member.getMember().getNickname())
                .shareDiaryNum(member.getShareDiary().getShareDiaryNum())
                .managerId(member.getManager() != null ? member.getManager().getMemberId() : null)
                .managerName(member.getManager() != null ? member.getManager().getNickname() : null)
                .status(member.getStatus())
                .joinDate(member.getJoinDate())
                .build();
    }

    /**
     * ShareDiaryEntity를 ShareDiaryDTO로 변환하는 헬퍼 메서드
     * @param diary
     * @return
     */
    private ShareDiaryDTO convertEntityToDTO(ShareDiaryEntity diary) {
        // 공유 멤버 리스트를 DTO로 변환
        List<ShareMemberDTO> shareMemberDTOList = new ArrayList<>();

        // Null 체크를 통해 NPE 방지
        if (diary.getShareMemberList() != null) {
            shareMemberDTOList = diary.getShareMemberList().stream()
                    .map(member -> ShareMemberDTO.builder()
                            .shareMemberNum(member.getShareMemberNum()) // 공유 멤버 번호
                            .memberId(member.getMember().getMemberId()) // 회원 ID
                            .nickname(member.getMember().getNickname()) // 회원의 닉네임
                            .shareDiaryNum(diary.getShareDiaryNum()) // 공유 다이어리 번호
                            .managerId(member.getManager() != null ? member.getManager().getMemberId() : null) // 매니저 ID
                            .managerName(member.getManager() != null ? member.getManager().getNickname() : null) // 매니저 닉네임
                            .status(member.getStatus()) // 요청 상태
                            .joinDate(member.getJoinDate())
                            .build())
                    .collect(Collectors.toList());
        }

        // ShareDiaryDTO를 빌드하면서 shareMemberList를 포함
        return ShareDiaryDTO.builder()
                .shareDiaryNum(diary.getShareDiaryNum())   // 다이어리 고유 번호
                .shareDiaryName(diary.getShareDiaryName()) // 다이어리 제목
                .createdDate(diary.getCreatedDate())       // 다이어리 생성 날짜
                .updatedDate(diary.getUpdatedDate())       // 다이어리 수정 날짜
                .categoryNum(diary.getCategory().getCategoryNum()) // 카테고리 번호
                .categoryName(diary.getCategory().getCategoryName()) // 카테고리 이름
                .coverTemplateNum(diary.getCoverTemplate().getCoverNum())  // 커버 번호
                .coverTemplateName(diary.getCoverTemplate().getCoverName()) // 커버 이름
                .memberId(diary.getMember().getMemberId()) // 회원 ID
                .nickname(diary.getMember().getNickname())
                .diaryBio(diary.getDiaryBio())
                .shareMemberList(shareMemberDTOList) // 공유 멤버 리스트 추가
                .build();
    }

    /**
     * 공유 다이어리를 생성한 사람이 현재 로그인한 사용자인지 확인하는 메서드
     * @param diaryNum
     * @param memberId
     * @return
     */
    public boolean isDiaryCreatedByUser(Integer diaryNum, String memberId) {
        ShareDiaryEntity diary = shareDiaryRepository.findById(diaryNum).orElseThrow(() -> new RuntimeException("Diary not found"));
        return diary.getMember().getMemberId().equals(memberId);  // 다이어리의 생성자가 현재 사용자와 같은지 확인
    }


}
