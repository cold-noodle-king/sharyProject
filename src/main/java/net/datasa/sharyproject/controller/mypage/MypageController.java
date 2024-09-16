package net.datasa.sharyproject.controller.mypage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.service.member.MemberService;
import net.datasa.sharyproject.service.mypage.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("mypage")
public class MypageController {

    private final MemberService memberService;
    private final ProfileService profileService;
    private MemberEntity member;

    @GetMapping("mypageView")
    public String mypage() {
        return "mypage/mypageView";

    }

    @GetMapping("infoForm")
    public String infoForm() {
        return "mypage/infoForm";
    }

    @GetMapping("follow")
    public String follow() {
        return "mypage/follow";
    }

    @GetMapping("message")
    public String message() {
        return "mypage/message";
    }

    /**
     * 프로필 페이지
     * @return profile HTML로 이동
     */
    @GetMapping("profile")
    public String profile() {
        return "mypage/profile";
    }


    @PostMapping("profileUpdate")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam("ment") String ment,
            @RequestParam("memberId") String memberId) {

        // 디버깅을 위한 로그 출력
        log.info("프로필 업데이트 요청을 받았습니다. memberId: {}, ment: {}", memberId, ment);

        // memberId를 사용하여 MemberEntity를 조회
        MemberEntity member = memberService.findById(memberId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 프로필 업데이트 서비스 호출
        ProfileDTO updatedProfile = profileService.updateProfile(profileImage, ment, member);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("profilePicture", "/images/" + updatedProfile.getProfilePicture()); // 이미지 경로
        response.put("ment", updatedProfile.getMent());

        log.info("프로필 업데이트가 성공적으로 처리되었습니다.");  // 응답을 보내기 전에 로그 출력

        return ResponseEntity.ok(response);
    }
}
