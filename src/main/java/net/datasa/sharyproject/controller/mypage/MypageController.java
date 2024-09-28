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
    //private MemberEntity memberEntity;
    //private Object profile;

    /**
     * 메인페이지 첫 화면
     * @param user 현재 로그인 중이 유저
     * @param model 멤버 정보
     * @return 메인페이지 뷰 html로 이동
     */
    @GetMapping("mypageView")
    public String mypage(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        MemberDTO memberDTO = memberService.getMember(user.getUsername());
        model.addAttribute("member", memberDTO);

        log.info("개인정보 내용 : {}", memberDTO);

        MemberEntity member = memberService.findById(user.getMemberId())
                .orElseThrow(() -> new RuntimeException("사용자 (" + user + ")를 찾을 수 없습니다."));
        // 프로필 정보를 데이터베이스에서 가져옴
        ProfileEntity profile = profileService.findByMember(member)
                .orElseGet(() -> {
                    // 프로필 정보가 없으면 기본 프로필을 생성하여 반환
                    ProfileEntity defaultProfile = ProfileEntity.builder()
                            .member(member)
                            .profilePicture("/images/profile.png")  // 기본 이미지 설정
                            .ment("")  // 기본 소개글 설정
                            .build();
                    profileService.saveProfile(defaultProfile);  // 생성한 기본 프로필을 저장
                    return defaultProfile;
                });

        model.addAttribute("profile", profile);

        return "mypage/mypageView";

    }

    /**
     * 개인정보 조회
     * @param user 현재 로그인 된 사용자
     * @param model 멤버 정보
     * @return 개인정보 html로 이동
     */
    @GetMapping("info")
    public String info(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        MemberDTO memberDTO = memberService.getMember(user.getUsername());
        model.addAttribute("member", memberDTO);

        log.info("개인정보 내용 : {}", memberDTO);

        MemberEntity member = memberService.findById(user.getMemberId())
                .orElseThrow(() -> new RuntimeException("사용자 (" + user + ")를 찾을 수 없습니다."));
        // 프로필 정보를 데이터베이스에서 가져옴
        ProfileEntity profile = profileService.findByMember(member)
                .orElseGet(() -> {
                    // 프로필 정보가 없으면 기본 프로필을 생성하여 반환
                    ProfileEntity defaultProfile = ProfileEntity.builder()
                            .member(member)
                            .profilePicture("/images/profile.png")  // 기본 이미지 설정
                            .ment("")  // 기본 소개글 설정
                            .build();
                    profileService.saveProfile(defaultProfile);  // 생성한 기본 프로필을 저장
                    return defaultProfile;
                });

        model.addAttribute("profile", profile);
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

    /**
     * 프로필 페이지
     * @param model
     * @param user 현재 로그인 중인 유저
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

        // 프로필 정보를 데이터베이스에서 가져옴
        ProfileEntity profile = profileService.findByMember(member)
                .orElseGet(() -> {
                    // 프로필 정보가 없으면 기본 프로필을 생성하여 반환
                    ProfileEntity defaultProfile = ProfileEntity.builder()
                            .member(member)
                            .profilePicture("/images/profile.png")  // 기본 이미지 설정
                            .ment("")  // 기본 소개글 설정
                            .build();
                    profileService.saveProfile(defaultProfile);  // 생성한 기본 프로필을 저장
                    return defaultProfile;
                });

        log.info("로그인된 사용자: {}", username);
        log.info("멤버 정보: {}", member);

        model.addAttribute("profile", profile);
        model.addAttribute("member", member);
        return "mypage/profile";
    }


    @PostMapping("profileUpdate")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam("ment") String ment,
            @RequestParam("memberId") String memberId,
            @RequestParam(value = "resetImage", required = false, defaultValue = "false") String resetImage) {



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
        ProfileEntity profile = profileService.findByMember(member)
                .orElse(null);  // Optional에서 프로필 정보를 가져옴, 없으면 null

        // 프로필이 없을 경우 생성
        if (profile == null) {
            profile = ProfileEntity.builder()
                    .member(member)
                    .profilePicture("/images/default.png")  // 기본 이미지 설정
                    .ment("")  // 기본 소개글
                    .build();
            profileService.saveProfile(profile);  // 새 프로필 저장
        }

        // updatedProfile을 if-else 블록 밖에서 선언
        //ProfileDTO updatedProfile;

        // 이미지 및 소개글 업데이트
        //ProfileDTO updatedProfile = profileService.updateProfile(profileImage, ment, profile);

        // 이미지 삭제 여부 확인
        if ("true".equals(resetImage)) {
            // 이미지가 삭제된 경우 기본 이미지로 설정
            profile.setProfilePicture("/images/profile.png");
            profile.setProfileOriginalName("default.png");
        } else if (profileImage != null && !profileImage.isEmpty()) {
            // 새로운 이미지가 업로드된 경우
            ProfileDTO updatedProfile = profileService.updateProfile(profileImage, ment, profile);
            profileService.saveProfile(profile);
        } else {
            // 기존 이미지를 유지하는 경우
            profile.setMent(ment);
        }

        profileService.saveProfile(profile);  // 변경된 프로필 정보 저장




        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("profilePicture", profile.getProfilePicture()); // 이미지 경로
        response.put("ment", profile.getMent());

        log.info("프로필 업데이트가 성공적으로 처리되었습니다.");  // 응답을 보내기 전에 로그 출력

        return ResponseEntity.ok(response);

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
     * 모달용 프로필 페이지
     * @param model
     * @param user 현재 로그인 중인 유저
     * @return modal에 필요한 데이터를 전달하는 메서드
     */
    @GetMapping("/profile/modal")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> profileModal(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            log.error("인증된 사용자가 없습니다.");
            return ResponseEntity.badRequest().build();
        }

        String username = user.getUsername();
        MemberEntity member = memberService.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자 (" + username + ")를 찾을 수 없습니다."));

        // 프로필 정보를 데이터베이스에서 가져옴
        ProfileEntity profile = profileService.findByMember(member)
                .orElseGet(() -> {
                    ProfileEntity defaultProfile = ProfileEntity.builder()
                            .member(member)
                            .profilePicture("/images/profile.png")  // 기본 이미지 설정
                            .ment("기본 소개글")  // 기본 소개글 설정
                            .build();
                    profileService.saveProfile(defaultProfile);
                    return defaultProfile;
                });

        // 프로필 정보를 JSON으로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("profilePicture", profile.getProfilePicture());
        response.put("nickname", member.getNickname());  // 멤버의 닉네임
        response.put("ment", profile.getMent());

        return ResponseEntity.ok(response);
    }
}
