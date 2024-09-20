package net.datasa.sharyproject.domain.entity.mypage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profile")
@Entity
public class ProfileEntity {

    //프로필 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_num", nullable = false)
    private int profileNum;

    //저장된 이미지 파일명
    @Column(name = "profile_picture", nullable = false, length=100)
    private String profilePicture;

    //원본 이미지 파일명
    @Column(name = "profile_original_name", length=255)
    private String profileOriginalName;

    //프로필 소개글
    @Column(name = "ment", length=300)
    private String ment;

    //사용자 아이디
    //회원당 하나의 프로필, MemberEntity와 1:1 관계 설정
    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    private MemberEntity member;


}
