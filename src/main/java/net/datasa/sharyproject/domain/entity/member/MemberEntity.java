package net.datasa.sharyproject.domain.entity.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class)
public class MemberEntity {

    @Id
    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;

    @Column(name = "password", nullable = false, length = 100)
    private String memberPw;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "gender", nullable = false, columnDefinition = "CHAR(10)")
    private String gender;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;  // 필드를 Boolean으로 정의하고 기본값을 true로 설정

    // 회원이 선택한 카테고리들 (일대다 관계)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<UserCategoryEntity> userCategories;

}
