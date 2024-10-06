package net.datasa.sharyproject.controller.share;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.share.AcceptRequestDTO;
import net.datasa.sharyproject.domain.dto.share.ShareDiaryDTO;
import net.datasa.sharyproject.domain.dto.share.ShareMemberDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.member.MemberService;
import net.datasa.sharyproject.service.mypage.ProfileService;
import net.datasa.sharyproject.service.personal.CoverTemplateService;
import net.datasa.sharyproject.service.personal.NoteTemplateService;
import net.datasa.sharyproject.service.share.ShareDiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("sharemember")
public class SharememberController {

    private final CoverTemplateService coverTemplateService;
    private final NoteTemplateService noteTemplateService;
    private final ShareDiaryService shareDiaryService;
    private final ShareDiaryRepository shareDiaryRepository;
    private final ProfileService profileService;
    private final MemberService memberService;

    //공유 다이어리 멤버 관리 페이지로 이동
    @GetMapping("viewMember")
    public String viewMember(@RequestParam("diaryNum") Integer diaryNum
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        model.addAttribute("diary", dto);

        return "share/ViewMember";
    }

    //공유 다이어리 멤버 리스트 출력
    @GetMapping("memberList")
    public String memberList(@RequestParam("diaryNum") Integer diaryNum
            ,@AuthenticationPrincipal AuthenticatedUser user
            ,Model model) {

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

        // 공유 다이어리 멤버 수를 카운트해서 가져옴
        int memberCount  = shareDiaryService.getMemberCount(diaryNum);

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        model.addAttribute("diary", dto);
        List<ShareMemberDTO> dtoList = shareDiaryService.getMemberList(diaryNum, user.getUsername());
        model.addAttribute("list", dtoList);
        model.addAttribute("profile", profile);
        model.addAttribute("memberCount", memberCount);

        return "share/MemberList";
    }

    // 가입 요청 메서드
    @GetMapping("join")
    @ResponseBody
    public String join(@RequestParam("diaryNum") Integer diaryNum,
                       @AuthenticationPrincipal AuthenticatedUser user) {
        String result;

        try {
            shareDiaryService.join(diaryNum, user.getUsername());
            result = "가입 요청이 완료되었습니다.";

        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }

        return result; // JSON 형태로 응답
    }

    //공유 다이어리 가입 요청 리스트 출력
    @GetMapping("registerRequest")
    public String registerRequest(@RequestParam("diaryNum") Integer diaryNum
            , @AuthenticationPrincipal AuthenticatedUser user
            , Model model
            , RedirectAttributes redirectAttributes){

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        List<ShareMemberDTO> dtoList = shareDiaryService.getPendingMembers(diaryNum);
        model.addAttribute("requestList", dtoList);
        model.addAttribute("diary", dto);

        return "share/RegisterRequest";
    }

    // 공유 다이어리 가입 요청 수락 메서드
    @ResponseBody
    @PostMapping("/acceptRequest")
    public ResponseEntity<String> acceptRequest(@RequestBody AcceptRequestDTO requestDTO) {
        try {
            Integer diaryNum = requestDTO.getDiaryNum();
            String memberId = requestDTO.getMemberId();

            log.debug("Received accept request for diaryNum: {}, memberId: {}", diaryNum, memberId);

            shareDiaryService.acceptRegister(diaryNum, memberId);

            return ResponseEntity.ok("가입 요청을 수락하였습니다.");
        } catch (Exception e) {
            log.error("Error accepting join request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가입 요청 수락 중 오류 발생");
        }
    }
}
