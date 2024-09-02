package net.datasa.sharyproject.Service.Follow;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import net.datasa.sharyproject.domain.entity.Follow.FollowEntity;
import net.datasa.sharyproject.domain.entity.Follow.FollowId;
import net.datasa.sharyproject.repository.Follow.FollowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 복합키 사용 테스트
 */
@RequiredArgsConstructor
@Transactional
@Service
public class FollowService {

    private final FollowRepository repository;

    /**
     * 저장 테스트
     */
    @Transactional
    public void insert() {
        try {
            FollowEntity entity = FollowEntity.builder()
                    .followerId("aaa")
                    .followingId("bbb")
                    .followDate(LocalDateTime.now())
                    .build();

            FollowId followId = new FollowId("aaa", "bbb");

            Optional<FollowEntity> existingFollow = repository.findById(followId);
            if (existingFollow.isPresent()) {
                throw new RuntimeException("이미 존재하는 팔로우 관계입니다.");
            }

            repository.save(entity);
        } catch (RuntimeException e) {
            //log.error("Error while inserting follow relationship: {}", e.getMessage());
            throw e; // 필요시 사용자 정의 예외로 변환하거나 처리할 수 있습니다.
        }
    }


}
