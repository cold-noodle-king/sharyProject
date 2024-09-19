package net.datasa.sharyproject.domain.entity.share;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@ToString(exclude = "shareDiary") //현재 객체 중 특정 변수를 출력하지 않게 하기 위해 붙임: board의 호출을 끊어서 순환 참조를 막음
@Table(name = "share_member")
@EntityListeners(AuditingEntityListener.class)
public class ShareMemberEntity {

    //공유 다이어리 멤버 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_member_num")
    private Integer shareMemberNum;

    //공유 다이어리 멤버(외래키로 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;

    //공유 다이어리 번호(외래키로 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_diary_num", referencedColumnName = "share_diary_num")
    private ShareDiaryEntity shareDiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", referencedColumnName = "member_id")
    private MemberEntity manager;

    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED'))")
    private String status;

}
