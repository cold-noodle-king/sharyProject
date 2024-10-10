package net.datasa.sharyproject.service.follow;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import net.datasa.sharyproject.domain.entity.follow.FollowEntity;
import net.datasa.sharyproject.domain.entity.follow.FollowId;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;  // MemberEntity 임포트 추가
import net.datasa.sharyproject.domain.entity.sse.NotificationEntity;
import net.datasa.sharyproject.repository.follow.FollowRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
import net.datasa.sharyproject.repository.sse.NotificationRepository;
import net.datasa.sharyproject.service.sse.SseService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FollowService 클래스
 * 이 클래스는 팔로우/언팔로우 기능, 팔로우 리스트 조회, 실시간 팔로우 알림 전송 등을 담당합니다.
 */
@RequiredArgsConstructor  // 의존성 주입을 위한 생성자 자동 생성
@Transactional  // 모든 메서드가 트랜잭션 내에서 실행됨 (실패 시 롤백)
@Service  // 이 클래스가 서비스 역할을 한다는 것을 명시
@Slf4j  // 로그 기록을 위한 Lombok 어노테이션
public class FollowService {

    // 의존성 주입 (팔로우, 멤버, 알림과 관련된 레포지토리 및 서비스)
    private final FollowRepository followRepository;
    private final SseService sseService;  // 알림 전송 서비스 추가
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository; // 알림 리포지토리 추가


    /**
     * 현재 로그인한 사용자의 ID를 반환하는 메서드
     * @return 현재 로그인한 사용자의 사용자명 (username)
     */
    public String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
        }
    }

    /**
     * 현재 사용자의 팔로워 목록을 조회하는 메서드
     * @return 팔로워 목록 (FollowDTO 리스트)
     */
    public List<FollowDTO> getFollowersForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findByFollowingId(currentUserId);

        // FollowEntity를 FollowDTO로 변환하여 반환
        return entities.stream()
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용자가 팔로우하고 있는 사용자 목록을 조회하는 메서드
     * @return 팔로우 목록 (FollowDTO 리스트)
     */
    public List<FollowDTO> getFollowingForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findByFollowerId(currentUserId);

        // FollowEntity를 FollowDTO로 변환하여 반환
        return entities.stream()
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }

    // 팔로우 기능
/*    public void follow(String followerId, String followingId) {
        FollowId followId = new FollowId(followerId, followingId);

        // 팔로우 관계가 이미 존재하는지 확인
        Optional<FollowEntity> existingFollow = followRepository.findById(followId);

        if (existingFollow.isPresent()) {
            throw new RuntimeException("이미 팔로우하고 있는 사용자입니다."); // 이미 팔로우 중인 경우 예외 발생
        }

        // 팔로우 관계가 존재하지 않으면 저장
        FollowEntity followEntity = FollowEntity.builder()
                .followerId(followerId)
                .followingId(followingId)
                .followDate(LocalDateTime.now())
                .build();
        followRepository.save(followEntity);

        // 팔로우 알림 전송
        sendFollowNotification(followerId, followingId);
    }*/

    /**
     * 특정 사용자를 팔로우하는 메서드
     * @param followerId 팔로우 하는 사람의 ID
     * @param followingId 팔로우 받는 사람의 ID
     */
    public void follow(String followerId, String followingId) {
        FollowId followId = new FollowId(followerId, followingId);

        // 팔로우 관계가 이미 존재하는지 확인
        Optional<FollowEntity> existingFollow = followRepository.findById(followId);

        if (existingFollow.isPresent()) {
            // 이미 팔로우 중이면 경고 메시지를 남기고 중단
            log.warn("사용자 {}는 이미 {}를 팔로우 중입니다.", followerId, followingId);
            return;  // 이미 팔로우된 경우 추가 작업 수행하지 않음
        }

        // 팔로우 관계가 존재하지 않으면 저장
        FollowEntity followEntity = FollowEntity.builder()
                .followerId(followerId)
                .followingId(followingId)
                .followDate(LocalDateTime.now())
                .build();
        followRepository.save(followEntity); // 팔로우 관계 저장

        // 팔로우 알림 전송
        sendFollowNotification(followerId, followingId);
    }


    /**
     * 팔로우 알림을 데이터베이스에 저장하고 SSE로 실시간 알림 전송
     * @param followerId 팔로우 하는 사람의 ID
     * @param followingId 팔로우 받는 사람의 ID
     */
    private void sendFollowNotification(String followerId, String followingId) {
        // 팔로우 대상 사용자(MemberEntity)를 조회
        MemberEntity followingMember = memberRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 대상 사용자를 찾을 수 없습니다."));

        // 알림 메시지 생성
        String message = followerId + "님이 회원님을 팔로우했습니다.";

        // 1. DB에 알림 저장
        NotificationEntity notification = NotificationEntity.builder()
                .receiver(followingMember)  // 알림을 받을 사용자 설정
                .content(message)  // 알림 내용
                .createdAt(LocalDateTime.now())  // 알림 생성 시간
                .isRead(false)  // 읽지 않은 상태로 설정
                .notificationType("follow")  // 알림 타입을 "팔로우"로 설정
                .build();
        notificationRepository.save(notification);  // 알림 저장

        // 2. 실시간으로 SSE 알림 전송
        sseService.sendNotification(followingId, message, "follow");
    }

    /**
     * 특정 사용자를 언팔로우하는 메서드
     * @param followingId 언팔로우 대상자의 ID
     */
    public void unfollow(String followingId) {
        String followerId = getCurrentUserId();  // 현재 사용자의 ID 가져오기
        FollowId followId = new FollowId(followerId, followingId);
        followRepository.deleteById(followId); // 팔로우 관계 삭제
    }


    /**
     * 모든 사용자를 팔로우하는 메서드 (테스트 용도)
     * 현재 사용자가 모든 기존 사용자를 팔로우하고, 모든 기존 사용자도 현재 사용자를 팔로우하게 함
     */
    public void insert() {
        String currentUserId = getCurrentUserId();
        // MemberEntity 클래스 임포트 및 사용
        List<String> existingUserIds = memberRepository.findAll()
                .stream()
                .map(MemberEntity::getMemberId) // MemberEntity의 getMemberId 메서드 호출
                .filter(id -> !id.equals(currentUserId))
                .collect(Collectors.toList());

        // 모든 기존 사용자와 상호 팔로우 관계를 생성
        for (String existingUserId : existingUserIds) {
            follow(currentUserId, existingUserId); // 신규 사용자가 기존 사용자 모두를 팔로우
            follow(existingUserId, currentUserId); // 기존 사용자가 신규 사용자를 팔로우
        }
    }

    /**
     * 팔로우 여부를 확인하는 메서드
     * @param currentUserId 현재 사용자 ID
     * @param targetUserId 팔로우 대상 사용자 ID
     * @return 팔로우 중이면 true, 그렇지 않으면 false
     */
    public boolean isFollowing(String currentUserId, String targetUserId) {
        Optional<FollowEntity> follow = followRepository.findById(new FollowId(currentUserId, targetUserId));
        return follow.isPresent(); // 팔로우 관계가 있으면 true 반환
    }

/*    public List<FollowDTO> getFollowListForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findByFollowerId(currentUserId);

        return entities.stream()
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }*/


    /**
     * 전체 사용자를 검색하는 메서드
     * @param query 검색어
     * @param currentUserId 현재 사용자 ID
     * @return 검색된 사용자 목록 (MemberDTO 리스트)
     */
    public List<MemberDTO> searchAllUsers(String query, String currentUserId) {
        // 현재 사용자가 팔로우하고 있는 사용자 ID 목록 가져오기
        List<String> followingIds = followRepository.findByFollowerId(currentUserId)
                .stream()
                .map(FollowEntity::getFollowingId)
                .collect(Collectors.toList());

        // 전체 사용자 중 검색어와 일치하는 사용자 목록을 반환
        return memberRepository.findAll().stream()
                .filter(member -> !member.getMemberId().equals(currentUserId)) // 현재 사용자 제외
                .filter(member -> member.getMemberId().toLowerCase().contains(query.toLowerCase()) ||
                        member.getNickname().toLowerCase().contains(query.toLowerCase()))
                .map(member -> {
                    boolean isFollowing = followingIds.contains(member.getMemberId());
                    return new MemberDTO(
                            member.getMemberId(),
                            null, // 비밀번호는 제외
                            member.getEmail(),
                            member.getPhoneNumber(),
                            member.getFullName(),
                            member.getNickname(),
                            member.getGender(),
                            member.getBirthdate(),
                            member.getCreatedDate(),
                            member.getUpdatedDate(),
                            member.getEnabled(),
                            member.getRoleName(),
                            isFollowing // isFollowing 값을 설정
                    );
                })
                .collect(Collectors.toList());
    }

    //
/*    public List<MemberDTO> getAllUsersExceptCurrentUser(String currentUserId) {
        return memberRepository.findAll().stream()
                .filter(member -> !member.getMemberId().equals(currentUserId)) // 현재 로그인한 사용자 제외
                .map(member -> new MemberDTO(
                        member.getMemberId(),
                        null, // 비밀번호는 제외
                        member.getEmail(),
                        member.getPhoneNumber(),
                        member.getFullName(),
                        member.getNickname(),
                        member.getGender(),
                        member.getBirthdate(),
                        member.getCreatedDate(),
                        member.getUpdatedDate(),
                        member.getEnabled(), // 필드 이름을 올바르게 사용
                        member.getRoleName(),
                ))
                .collect(Collectors.toList());
    }*/

    /**
     * 사용자의 팔로워를 검색하는 메서드
     * @param query 검색어
     * @param currentUserId 현재 사용자 ID
     * @return 검색된 팔로워 목록 (FollowDTO 리스트)
     */
    public List<FollowDTO> searchFollowers(String query, String currentUserId) {
        return followRepository.findByFollowingId(currentUserId).stream()
                .filter(follow -> follow.getFollowerId().toLowerCase().contains(query.toLowerCase()))
                .map(follow -> new FollowDTO(follow.getFollowerId(), follow.getFollowingId(), follow.getFollowDate()))
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 팔로잉을 검색하는 메서드
     * @param query 검색어
     * @param currentUserId 현재 사용자 ID
     * @return 검색된 팔로잉 목록 (FollowDTO 리스트)
     */
    public List<FollowDTO> searchFollowing(String query, String currentUserId) {
        return followRepository.findByFollowerId(currentUserId).stream()
                .filter(follow -> follow.getFollowingId().toLowerCase().contains(query.toLowerCase()))
                .map(follow -> new FollowDTO(follow.getFollowerId(), follow.getFollowingId(), follow.getFollowDate()))
                .collect(Collectors.toList());
    }



}
