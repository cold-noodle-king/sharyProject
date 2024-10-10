package net.datasa.sharyproject.domain.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * FollowDTO 클래스
 * 사용자의 팔로우/언팔로우 정보를 담고 있는 데이터 전송 객체(DTO)입니다.
 * 이 객체는 팔로우 관계에 대한 정보를 포함하고 있습니다.
 */
@Data  // Getter, Setter, toString, equals, hashCode 메서드를 자동으로 생성하는 Lombok 어노테이션
@NoArgsConstructor  // 기본 생성자를 자동으로 생성하는 Lombok 어노테이션
@AllArgsConstructor  // 모든 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
public class FollowDTO {

    // 팔로우를 한 사용자의 ID (팔로워의 ID)
    private String followerId;

    // 팔로우를 받은 사용자의 ID (팔로잉의 ID)
    private String followingId;

    // 팔로우가 발생한 날짜와 시간 (언제 팔로우가 이루어졌는지 기록)
    private LocalDateTime followDate;
}
