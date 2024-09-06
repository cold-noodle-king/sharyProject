package net.datasa.sharyproject.service.follow;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.entity.follow.FollowEntity;
import net.datasa.sharyproject.domain.entity.follow.FollowId;
import net.datasa.sharyproject.repository.follow.FollowRepository;
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

    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public void follow(String followerId, String followingId) {
        FollowId followId1 = new FollowId(followerId, followingId);
        FollowId followId2 = new FollowId(followingId, followerId);

        Optional<FollowEntity> existingFollow1 = followRepository.findById(followId1);
        Optional<FollowEntity> existingFollow2 = followRepository.findById(followId2);

        if (existingFollow1.isPresent() && existingFollow2.isPresent()) {
            throw new RuntimeException("이미 양방향 팔로우 관계가 설정되어 있습니다.");
        }

        if (!existingFollow1.isPresent()) {
            FollowEntity followEntity1 = FollowEntity.builder()
                    .followerId(followerId)
                    .followingId(followingId)
                    .followDate(LocalDateTime.now())
                    .build();
            followRepository.save(followEntity1);
        }

        if (!existingFollow2.isPresent()) {
            FollowEntity followEntity2 = FollowEntity.builder()
                    .followerId(followingId)
                    .followingId(followerId)
                    .followDate(LocalDateTime.now())
                    .build();
            followRepository.save(followEntity2);
        }
    }

    public void insert() {
        follow("aaa", "bbb");
    }

    public void unfollow(String followingId) {
        String followerId = getCurrentUserId();
        FollowId followId = new FollowId(followerId, followingId);
        followRepository.deleteById(followId);
    }

    public List<FollowDTO> getFollowListForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findAll();
        return entities.stream()
                .filter(entity -> entity.getFollowerId().equals(currentUserId) || entity.getFollowingId().equals(currentUserId))
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }

    /**
     * 현재 로그인한 사용자 ID 가져오기
     */
/*    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }*/

    /**
     * 팔로우 설정 메서드
     */
/*    public void follow(String followingId) {
        String followerId = getCurrentUserId(); // 현재 로그인한 사용자 ID 가져오기

        // followerId -> followingId 관계가 존재하는지 확인
        FollowId followId1 = new FollowId(followerId, followingId);
        Optional<FollowEntity> existingFollow = followRepository.findById(followId1);

        if (existingFollow.isPresent()) {
            throw new RuntimeException("이미 팔로우 관계가 설정되어 있습니다.");
        }

        // 현재 관계가 없으면 팔로우 저장
        FollowEntity followEntity = FollowEntity.builder()
                .followerId(followerId)
                .followingId(followingId)
                .followDate(LocalDateTime.now())
                .build();
        followRepository.save(followEntity);
    }*/


    /**
     * 양방향 팔로우 설정 메서드
     */
    /*public void follow(String followerId, String followingId) {
        FollowId followId1 = new FollowId(followerId, followingId);
        FollowId followId2 = new FollowId(followingId, followerId);

        Optional<FollowEntity> existingFollow1 = followRepository.findById(followId1);
        Optional<FollowEntity> existingFollow2 = followRepository.findById(followId2);

        if (existingFollow1.isPresent() && existingFollow2.isPresent()) {
            throw new RuntimeException("이미 양방향 팔로우 관계가 설정되어 있습니다.");
        }

        if (!existingFollow1.isPresent()) {
            FollowEntity followEntity1 = FollowEntity.builder()
                    .followerId(followerId)
                    .followingId(followingId)
                    .followDate(LocalDateTime.now())
                    .build();
            followRepository.save(followEntity1);
        }

        if (!existingFollow2.isPresent()) {
            FollowEntity followEntity2 = FollowEntity.builder()
                    .followerId(followingId)
                    .followingId(followerId)
                    .followDate(LocalDateTime.now())
                    .build();
            followRepository.save(followEntity2);
        }
    }
*/

    /**
     * 저장 테스트 메서드
     */
/*    public void insert() {
        follow("aaa", "bbb");
    }*/


    /**
     * 팔로우 관계 삭제 메서드
     */
    /*public void unfollow(String followingId) {
        String followerId = getCurrentUserId();
        FollowId followId = new FollowId(followerId, followingId);
        followRepository.deleteById(followId);
    }*/

    /**
     * 현재 사용자의 팔로우 목록을 가져오는 메서드
     */
    /*public List<FollowDTO> getFollowListForCurrentUser() {
        String currentUserId = getCurrentUserId();
        List<FollowEntity> entities = followRepository.findAll();
        return entities.stream()
                .filter(entity -> entity.getFollowerId().equals(currentUserId) || entity.getFollowingId().equals(currentUserId))
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }*/


    /**
     * 전체 팔로우 목록을 가져오는 메서드
     */
/*    public List<FollowDTO> followAll() {
        List<FollowEntity> entities = followRepository.findAll();
        return entities.stream()
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }*/

    /**
     * 팔로우 관계 삭제 메서드
     */
/*    public void delete(String followerId, String followingId) {
        // 현재 관계 삭제
        FollowId followId1 = new FollowId(followerId, followingId);
        followRepository.deleteById(followId1);

        // 반대 관계 삭제 (이건 관계를 모두 삭제하도록 설정)
        *//*FollowId followId2 = new FollowId(followingId, followerId);
        followRepository.deleteById(followId2);*//*
    }*/
}
