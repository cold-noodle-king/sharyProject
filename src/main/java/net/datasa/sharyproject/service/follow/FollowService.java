package net.datasa.sharyproject.service.follow;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.entity.follow.FollowEntity;
import net.datasa.sharyproject.domain.entity.follow.FollowId;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;  // MemberEntity 임포트 추가
import net.datasa.sharyproject.repository.follow.FollowRepository;
import net.datasa.sharyproject.repository.member.MemberRepository;
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
    private final MemberRepository memberRepository;

    public String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            throw new IllegalStateException("현재 인증된 사용자가 없습니다.");
        }
    }

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
    }

    public void unfollow(String followingId) {
        String followerId = getCurrentUserId();
        FollowId followId = new FollowId(followerId, followingId);
        followRepository.deleteById(followId);
    }

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

    public List<FollowDTO> getFollowListForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findByFollowerId(currentUserId);

        return entities.stream()
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }
}
