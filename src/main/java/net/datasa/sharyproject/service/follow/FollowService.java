package net.datasa.sharyproject.service.follow;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Transactional
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final SseService sseService;  // 알림 전송 서비스 추가
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository; // 알림 리포지토리 추가


    /**
     * 현재 로그인한 사용자 ID를 반환
     * @return
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
     * 현재 사용자의 팔로워 목록을 가져옴
     * @return
     */
    public List<FollowDTO> getFollowersForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findByFollowingId(currentUserId);

        return entities.stream()
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용자가 팔로우하고 있는 사용자 목록을 가져옴
     * @return
     */
    public List<FollowDTO> getFollowingForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findByFollowerId(currentUserId);

        return entities.stream()
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }

    // 팔로우 기능
    public void follow(String followerId, String followingId) {
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
    }


    /**
     * 팔로우 알림을 db에 저장하고 sse로 전송
     * @param followerId
     * @param followingId
     */
    private void sendFollowNotification(String followerId, String followingId) {
        MemberEntity followingMember = memberRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("팔로우 대상 사용자를 찾을 수 없습니다."));

        // 알림 메시지 생성
        String message = followerId + "님이 회원님을 팔로우했습니다.";

        // 1. DB에 알림 저장
        NotificationEntity notification = NotificationEntity.builder()
                .receiver(followingMember)
                .content(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .notificationType("follow") // 알림 타입 설정 추가
                .build();
        notificationRepository.save(notification);

        // 2. 실시간으로 SSE 알림 전송
        sseService.sendNotification(followingId, message, "follow");
    }

    /**
     * 언팔로우 기능
     * @param followingId
     */
    public void unfollow(String followingId) {
        String followerId = getCurrentUserId();
        FollowId followId = new FollowId(followerId, followingId);
        followRepository.deleteById(followId);
    }

    /**
     *
     */
    public void insert() {
        String currentUserId = getCurrentUserId();
        // MemberEntity 클래스 임포트 및 사용
        List<String> existingUserIds = memberRepository.findAll()
                .stream()
                .map(MemberEntity::getMemberId) // MemberEntity의 getMemberId 메서드 호출
                .filter(id -> !id.equals(currentUserId))
                .collect(Collectors.toList());

        for (String existingUserId : existingUserIds) {
            follow(currentUserId, existingUserId); // 신규 사용자가 기존 사용자 모두를 팔로우
            follow(existingUserId, currentUserId); // 기존 사용자가 신규 사용자를 팔로우
        }
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
     * 전체 사용자 검색 기능 추가
     * @param query
     * @param currentUserId
     * @return
     */
    public List<MemberDTO> searchAllUsers(String query, String currentUserId) {
        // 현재 사용자가 팔로우하고 있는 사용자 ID 목록 가져오기
        List<String> followingIds = followRepository.findByFollowerId(currentUserId)
                .stream()
                .map(FollowEntity::getFollowingId)
                .collect(Collectors.toList());

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
     * 팔로워 검색 기능
     * @param query
     * @param currentUserId
     * @return
     */
    public List<FollowDTO> searchFollowers(String query, String currentUserId) {
        return followRepository.findByFollowingId(currentUserId).stream()
                .filter(follow -> follow.getFollowerId().toLowerCase().contains(query.toLowerCase()))
                .map(follow -> new FollowDTO(follow.getFollowerId(), follow.getFollowingId(), follow.getFollowDate()))
                .collect(Collectors.toList());
    }

    /**
     * 팔로잉 검색 기능
     * @param query
     * @param currentUserId
     * @return
     */
    public List<FollowDTO> searchFollowing(String query, String currentUserId) {
        return followRepository.findByFollowerId(currentUserId).stream()
                .filter(follow -> follow.getFollowingId().toLowerCase().contains(query.toLowerCase()))
                .map(follow -> new FollowDTO(follow.getFollowerId(), follow.getFollowingId(), follow.getFollowDate()))
                .collect(Collectors.toList());
    }

}
