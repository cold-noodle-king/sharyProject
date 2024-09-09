package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "share_member")
@EntityListeners(AuditingEntityListener.class)
public class ShareMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_member_num")
    private Integer shareMemberNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    @Column(name = "share_diary_num", nullable = false)
    private Integer shareDiaryNum;

    @Column(name = "manager_id", nullable = false, length = 50)
    private String managerId;

}
