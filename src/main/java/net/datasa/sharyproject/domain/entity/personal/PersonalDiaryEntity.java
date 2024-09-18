package net.datasa.sharyproject.domain.entity.personal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

/**
 * 개인 다이어리 엔티티
 * 사용자의 다이어리 정보를 저장하는 테이블과 매핑된 클래스입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // 생성 및 수정 날짜 자동 관리
@Table(name = "personal_diary")
public class PersonalDiaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    @Column(name = "personal_diary_num")
    private Integer personalDiaryNum; // 다이어리 고유 번호

    @Column(name = "diary_name", nullable = false, length = 100)
    private String diaryName; // 다이어리 이름

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Timestamp createdDate; // 생성 날짜 (자동 설정)

    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    private Timestamp updatedDate; // 수정 날짜 (자동 설정)

    @ManyToOne
    @JoinColumn(name = "category_num", nullable = false)
    private CategoryEntity category; // 카테고리 외래 키 (CategoryEntity 참조)

    @ManyToOne
    @JoinColumn(name = "cover_num", nullable = false)
    private CoverTemplateEntity coverTemplate; // 커버 외래 키 (CoverTemplateEntity 참조)

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member; // 회원 외래 키 (MemberEntity 참조)

    // 파라미터를 받는 생성자 추가
    public PersonalDiaryEntity(Integer personalDiaryNum) {
        this.personalDiaryNum = personalDiaryNum;
    }
}
