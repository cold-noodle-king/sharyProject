package net.datasa.sharyproject.domain.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private String memberId;

    private String memberPw;

    private String email;

    private String phoneNumber;

    private String fullName;

    private String nickname;

    private String gender;

    private LocalDate birthdate;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private boolean enabled;

    private String roleName;

    @JsonProperty("isFollowing") // JSON 직렬화 시 필드 이름을 지정
    private boolean isFollowing; // 추가: 현재 사용자가 이 회원을 팔로우하고 있는지 여부
}
