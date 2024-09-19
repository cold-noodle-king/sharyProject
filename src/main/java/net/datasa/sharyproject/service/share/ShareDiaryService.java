package net.datasa.sharyproject.service.share;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.share.ShareDiaryDTO;
import net.datasa.sharyproject.domain.dto.share.ShareMemberDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.personal.CoverTemplateEntity;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
import net.datasa.sharyproject.domain.entity.share.ShareMemberEntity;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.CategoryRepository;
import net.datasa.sharyproject.repository.personal.CoverTemplateRepository;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.repository.share.ShareMemberRepository;
import net.datasa.sharyproject.security.AuthenticatedUser;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

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
     * @return
     */
    public List<ShareDiaryDTO> getCreatedList(String memberId){

        List<ShareDiaryEntity> shareDiaryEntities = shareDiaryRepository.findByMember_MemberId(memberId);

        // 조회한 엔티티를 DTO로 변환하여 반환
        return shareDiaryEntities.stream()
                .map(this::convertEntityToDTO) // 엔티티를 DTO로 변환하는 메서드 호출
                .collect(Collectors.toList());
    }

    /**
     * 다이어리를 가져오는 메서드
     * @param diaryNum
     * @param memberId
     * @return
     */
    public ShareDiaryDTO getDiary(Integer diaryNum, String memberId){
        ShareDiaryEntity shareDiaryEntity = shareDiaryRepository.findById(diaryNum)
                .orElseThrow(() -> new EntityNotFoundException("다이어리 정보를 찾을 수 없습니다."));

       /* MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 정보를 찾을 수 없습니다."));*/

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
            shareDiaryList.add(shareDiaryDTO);
        }

        log.debug("가져온 리스트 정보!!!!:{}", shareDiaryList);
        return shareDiaryList;
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

        // 해당 다이어리와 멤버가 이미 가입 요청을 했는지 확인
        Optional<ShareMemberEntity> existingRequest = shareMemberRepository
                .findByShareDiary_ShareDiaryNumAndMember_MemberId(diaryNum, memberId);

        if (existingRequest.isPresent()) {
            // 이미 가입 요청을 한 경우 예외를 던짐
            throw new IllegalStateException("이미 가입 요청을 하셨습니다.");
        }

        // 새로운 가입 요청 생성
        ShareMemberEntity shareMemberEntity = new ShareMemberEntity();
        shareMemberEntity.setMember(memberEntity);
        shareMemberEntity.setManager(shareDiaryEntity.getMember());
        shareMemberEntity.setShareDiary(shareDiaryEntity);
        shareMemberEntity.setStatus("PENDING"); // 가입 요청 시 기본 상태를 보류로 설정

        log.debug("저장되는 엔티티: {}", shareMemberEntity);

        // 새로운 요청 저장
        shareMemberRepository.save(shareMemberEntity);
    }


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

        // 엔티티를 DTO로 변환
        List<ShareMemberDTO> pendingMemberDTOs = pendingMembers.stream()
                .map(this::convertShareMemberEntityToDTO)
                .collect(Collectors.toList());

        log.debug("가입 요청 리스트:{}", pendingMemberDTOs);
        return pendingMemberDTOs;
    }

    /**
     * 공유 다이어리 가입 요청을 수락하는 메서드
     * @param diaryNum
     * @param memberId
     */
    public void acceptRegister(Integer diaryNum, String memberId){
        // 공유 멤버 엔티티 조회
        ShareMemberEntity entity = shareMemberRepository.ShareDiary_shareDiaryNumAndMember_memberId(diaryNum, memberId);

        log.debug("가져온 회원 정보:{}", entity);
        entity.setStatus("ACCEPTED");

    }
    
    // ShareMemberEntity를 ShareMemberDTO로 변환하는 헬퍼 메서드
    private ShareMemberDTO convertShareMemberEntityToDTO(ShareMemberEntity member) {
        return ShareMemberDTO.builder()
                .shareMemberNum(member.getShareMemberNum())
                .memberId(member.getMember().getMemberId())
                .nickname(member.getMember().getNickname())
                .shareDiaryNum(member.getShareDiary().getShareDiaryNum())
                .managerId(member.getManager() != null ? member.getManager().getMemberId() : null)
                .managerName(member.getManager() != null ? member.getManager().getNickname() : null)
                .status(member.getStatus())
                .build();
    }

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


   /* private ShareDiaryDTO convertEntityToDTO(ShareDiaryEntity diary) {
        return ShareDiaryDTO.builder()
                .shareDiaryNum(diary.getShareDiaryNum())   // 다이어리 고유 번호
                .shareDiaryName(diary.getShareDiaryName())                 // 다이어리 제목
                .createdDate(diary.getCreatedDate())             // 다이어리 생성 날짜
                .updatedDate(diary.getUpdatedDate())             // 다이어리 수정 날짜
                .categoryNum(diary.getCategory().getCategoryNum()) // 카테고리 번호
                .categoryName(diary.getCategory().getCategoryName()) // 카테고리 이름
                .coverTemplateNum(diary.getCoverTemplate().getCoverNum())  // 커버 번호
                .coverTemplateName(diary.getCoverTemplate().getCoverName()) // 커버 이름 (추가)
                .memberId(diary.getMember().getMemberId())        // 회원 ID
                .nickname(diary.getMember().getNickname())
                .diaryBio(diary.getDiaryBio())
                .shareMemberList(diary.setShareMemberList())
                .build();
    }
*/
}
