package net.datasa.sharyproject.controller.mypage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.member.MemberService;
import net.datasa.sharyproject.service.mypage.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("mypage")
public class MypageController {

    private final MemberService memberService;
    private final ProfileService profileService;
    private MemberEntity member;

    @GetMapping("mypageView")
    public String mypage(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        MemberDTO memberDTO = memberService.getMember(user.getUsername());
        model.addAttribute("member", memberDTO);

        log.info("개인정보 내용 : {}", memberDTO);
        return "mypage/mypageView";

    }

    /**
     * 개인정보 조회
     * @param user 현재 로그인 된 사용자
     * @param model
     * @return
     */
    @GetMapping("info")
    public String info(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        MemberDTO memberDTO = memberService.getMember(user.getUsername());
        model.addAttribute("member", memberDTO);

        log.info("개인정보 내용 : {}", memberDTO);
        return "mypage/infoForm";
    }

    @PostMapping("info")
    public String info(@AuthenticationPrincipal AuthenticatedUser user
            , @ModelAttribute MemberDTO memberDTO, Model model) {

        log.debug("수정폼에서 전달된 값 : {}", memberDTO);
        memberDTO.setMemberId(user.getUsername());
        //서비스로 전달하여 DB수정
        memberService.infoUpdate(memberDTO);
        model.addAttribute("member", memberDTO);
        return "mypage/mypageView";
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
    public String profile(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            log.error("인증된 사용자가 없습니다.");
            throw new RuntimeException("인증된 사용자가 없습니다.");
        }

        String username = user.getUsername();
        MemberEntity member = memberService.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자 (" + username + ")를 찾을 수 없습니다."));

        log.info("로그인된 사용자: {}", username);
        log.info("멤버 정보: {}", member);

//        model.addAttribute("profile", profile);
        model.addAttribute("member", member);
        return "mypage/profile";
    }


    @PostMapping("profileUpdate")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam("ment") String ment,
            @RequestParam("memberId") String memberId) {

        log.info("memberId: {}", memberId);  // memberId가 잘 전달되는지 확인
        // 디버깅을 위한 로그 출력
        log.info("프로필 업데이트 요청을 받았습니다. memberId: {}, ment: {}", memberId, ment);

        Optional<MemberEntity> memberOpt = memberService.findById(memberId);
        if (!memberOpt.isPresent()) {
            log.error("사용자 ({})를 찾을 수 없습니다.", memberId);
            throw new RuntimeException("사용자 (" + memberId + ")를 찾을 수 없습니다.");
        }
        MemberEntity member = memberOpt.get();

        // 프로필 조회
        ProfileEntity profile = profileService.findByMember(member);

        // 프로필이 없을 경우 생성
        if (profile == null) {
            profile = ProfileEntity.builder()
                    .member(member)
                    .profilePicture("/images/default.png")  // 기본 이미지 설정
                    .ment("")  // 기본 소개글
                    .build();
            profileService.saveProfile(profile);  // 새 프로필 저장
        }

        // 이미지 및 소개글 업데이트
        ProfileDTO updatedProfile = profileService.updateProfile(profileImage, ment, profile);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("profilePicture", updatedProfile.getProfilePicture()); // 이미지 경로
        response.put("ment", updatedProfile.getMent());

        log.info("프로필 업데이트가 성공적으로 처리되었습니다.");  // 응답을 보내기 전에 로그 출력

        return ResponseEntity.ok(response);

    }
}