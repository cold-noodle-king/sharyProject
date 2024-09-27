package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.CategoryEntity;
import net.datasa.sharyproject.domain.entity.personal.CoverTemplateEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 *공유 다이어리(표지) 엔티티
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "share_diary")
@EntityListeners(AuditingEntityListener.class)
public class ShareDiaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_diary_num")
    private Integer shareDiaryNum;

    @Column(name = "share_diary_name", nullable = false, length = 100)
    private String shareDiaryName;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    //다이어리 카테고리 정보 (외래키)
    //다대일 관계. 다이어리 여러개가 하나의 카테고리를 참조한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_num", referencedColumnName = "category_num")
    private CategoryEntity category;

    //다이어리 커버 정보 (외래키)
    //다대일 관계. 다이어리 여러개가 하나의 커버를 참조한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_num", referencedColumnName = "cover_num")
    private CoverTemplateEntity coverTemplate;

    //다이어리 주인 정보 (외래키)
    //다대일 관계. 다이어리 여러개가 회원정보 하나를 참조한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    @Column(name = "diary_bio")
    private String diaryBio;

    @OneToMany(mappedBy = "shareDiary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShareMemberEntity> shareMemberList;

}
