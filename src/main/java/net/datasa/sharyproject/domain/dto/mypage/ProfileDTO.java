package net.datasa.sharyproject.domain.dto.mypage;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    //프로필 번호
    private int profileNum;

    //이미지 저장 이름
    private String profilePicture;

    //이미지 원본 이름
    private String profileOriginalName;

    //프로필 소개글
    private String ment;

    // 사용자 아이디 (MemberEntity 대신 memberId를 별도로 저장)
    private String memberId;

    // 닉네임 추가
    private String nickname;
}
