package net.datasa.sharyproject.service.follow;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.entity.follow.FollowEntity;
import net.datasa.sharyproject.domain.entity.follow.FollowId;
import net.datasa.sharyproject.domain.entity.member.MemberEntity; // MemberEntity 임포트 추가
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
    private final MemberRepository memberRepository; // MemberRepository 추가

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public void follow(String followerId, String followingId) {
        FollowId followId = new FollowId(followerId, followingId);

        // 팔로우 관계가 이미 존재하는지 확인
        Optional<FollowEntity> existingFollow = followRepository.findById(followId);

        if (existingFollow.isPresent()) {
            throw new RuntimeException("이미 팔로우하고 있는 사용자입니다.");
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

    public List<FollowDTO> getFollowListForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findAll();

        // 현재 사용자가 팔로우하는 사용자 목록 가져오기
        List<FollowDTO> followingList = entities.stream()
                .filter(entity -> entity.getFollowerId().equals(currentUserId))
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());

        // 현재 사용자를 팔로우하는 사용자 목록 가져오기
        List<FollowDTO> followerList = entities.stream()
                .filter(entity -> entity.getFollowingId().equals(currentUserId))
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());

        // 필요하다면 두 목록을 합치기
        followingList.addAll(followerList);
        return followingList;
    }

    public void insert() {
        // 모든 회원 ID를 가져오기
        List<String> memberIds = memberRepository.findAll()
                .stream()
                .map(MemberEntity::getMemberId) // MemberEntity의 getMemberId 메서드 호출
                .collect(Collectors.toList());

        // 회원들 간의 팔로우 관계 형성 (임의로)
        for (int i = 0; i < memberIds.size(); i++) {
            for (int j = i + 1; j < memberIds.size(); j++) {
                String followerId = memberIds.get(i);
                String followingId = memberIds.get(j);

                // 같은 ID일 경우 무시
                if (!followerId.equals(followingId)) {
                    follow(followerId, followingId);
                }
            }
        }
    }
}
