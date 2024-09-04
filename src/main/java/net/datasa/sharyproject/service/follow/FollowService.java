package net.datasa.sharyproject.service.follow;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.entity.follow.FollowEntity;
import net.datasa.sharyproject.domain.entity.follow.FollowId;
import net.datasa.sharyproject.repository.follow.FollowRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 복합키 사용 테스트
 */
@RequiredArgsConstructor
@Transactional
@Service
public class FollowService {

    private final FollowRepository followRepository;


    /**
     * 저장 테스트
     */
    public void insert() {
        try {
            FollowEntity entity = FollowEntity.builder()
                    .followerId("aaa")
                    .followingId("bbb")
                    .followDate(LocalDateTime.now())
                    .build();

            FollowId followId = new FollowId("aaa", "bbb");

            Optional<FollowEntity> existingFollow = followRepository.findById(followId);
            if (existingFollow.isPresent()) {
                throw new RuntimeException("이미 존재하는 팔로우 관계입니다.");
            }

            followRepository.save(entity);
        } catch (RuntimeException e) {
            // log.error("Error while inserting follow relationship: {}", e.getMessage());
            throw e;
        }
    }


    /**
     * 전체 팔로우 목록을 가져오는 메서드
     */
    public List<FollowDTO> followAll() {
        List<FollowEntity> entities = followRepository.findAll();
        return entities.stream()
                .map(entity -> new FollowDTO(
                        entity.getFollowerId(),
                        entity.getFollowingId(),
                        entity.getFollowDate()))
                .collect(Collectors.toList());
    }

    /**
     * 팔로우 관계 삭제 메서드
     */
    public void delete(String followerId, String followingId) {
        FollowId followId = new FollowId(followerId, followingId);
        followRepository.deleteById(followId);
    }


}
