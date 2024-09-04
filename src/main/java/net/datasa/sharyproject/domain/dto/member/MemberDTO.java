package net.datasa.sharyproject.domain.dto.member;

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
}
