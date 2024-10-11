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

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("sharemember")
public class SharememberController {

    // 서비스와 리포지토리 의존성을 주입받음 (DI)
    private final CoverTemplateService coverTemplateService;
    private final NoteTemplateService noteTemplateService;
    private final ShareDiaryService shareDiaryService;
    private final ShareDiaryRepository shareDiaryRepository;
    private final ProfileService profileService;
    private final MemberService memberService;

    /**
     * 공유 다이어리 멤버 관리 페이지로 이동
     * @param diaryNum 다이어리의 고유 번호
     * @param user 현재 인증된 사용자
     * @param model 데이터를 전달하기 위한 모델 객체
     * @return "share/ViewMember" 페이지를 반환
     */
    @GetMapping("viewMember")
    public String viewMember(@RequestParam("diaryNum") Integer diaryNum,
                             @AuthenticationPrincipal AuthenticatedUser user,
                             Model model) {
        // 특정 다이어리 정보를 가져옴
        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        // 모델에 다이어리 정보를 추가하여 View에 전달
        model.addAttribute("diary", dto);

        return "share/ViewMember";
    }

    /**
     * 공유 다이어리 멤버 리스트 출력
     * @param diaryNum 다이어리의 고유 번호
     * @param user 현재 인증된 사용자
     * @param model 데이터를 전달하기 위한 모델 객체
     * @return "share/MemberList" 페이지를 반환
     */
    @GetMapping("memberList")
    public String memberList(@RequestParam("diaryNum") Integer diaryNum,
                             @AuthenticationPrincipal AuthenticatedUser user,
                             Model model) {

        // 현재 사용자 이름 가져오기
        String username = user.getUsername();
        // 사용자 정보 조회
        MemberEntity member = memberService.findById(username)
                .orElseThrow(() -> new RuntimeException("사용자 (" + username + ")를 찾을 수 없습니다."));

        // 사용자 프로필 조회 (없을 경우 기본 프로필 생성)
        ProfileEntity profile = profileService.findByMember(member)
                .orElseGet(() -> {
                    // 기본 프로필 생성
                    ProfileEntity defaultProfile = ProfileEntity.builder()
                            .member(member)
                            .profilePicture("/images/profile.png")  // 기본 프로필 이미지 설정
                            .ment("")  // 기본 소개 글 설정
                            .build();
                    profileService.saveProfile(defaultProfile);  // 생성한 기본 프로필 저장
                    return defaultProfile;
                });

        // 다이어리의 멤버 수를 가져옴
        int memberCount = shareDiaryService.getMemberCount(diaryNum);

        // 다이어리 정보 가져오기
        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        model.addAttribute("diary", dto); // 다이어리 정보를 모델에 추가
        // 다이어리의 멤버 리스트 가져오기
        List<ShareMemberDTO> dtoList = shareDiaryService.getMemberList(diaryNum, user.getUsername());
        model.addAttribute("list", dtoList); // 멤버 리스트를 모델에 추가
        model.addAttribute("profile", profile); // 사용자 프로필을 모델에 추가
        model.addAttribute("memberCount", memberCount); // 멤버 수를 모델에 추가

        return "share/MemberList";
    }

    /**
     * 공유 다이어리 가입 요청 메서드
     * @param diaryNum 다이어리의 고유 번호
     * @param user 현재 인증된 사용자
     * @return 요청 처리 결과를 문자열로 반환 (성공 또는 실패 메시지)
     */
    @GetMapping("join")
    @ResponseBody
    public String join(@RequestParam("diaryNum") Integer diaryNum,
                       @AuthenticationPrincipal AuthenticatedUser user) {
        String result;

        try {
            // 다이어리에 가입 요청 처리
            shareDiaryService.join(diaryNum, user.getUsername());
            result = "가입 요청이 완료되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage(); // 오류 메시지를 반환
        }

        return result; // JSON 형태로 응답
    }

    /**
     * 공유 다이어리 가입 요청 리스트 출력
     * @param diaryNum 다이어리의 고유 번호
     * @param user 현재 인증된 사용자
     * @param model 데이터를 전달하기 위한 모델 객체
     * @param redirectAttributes 리다이렉트 시 추가할 데이터
     * @return "share/RegisterRequest" 페이지를 반환
     */
    @GetMapping("registerRequest")
    public String registerRequest(@RequestParam("diaryNum") Integer diaryNum,
                                  @AuthenticationPrincipal AuthenticatedUser user,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        // 다이어리 정보 가져오기
        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        // 가입 요청 중인 멤버 리스트 가져오기
        List<ShareMemberDTO> dtoList = shareDiaryService.getPendingMembers(diaryNum);
        model.addAttribute("requestList", dtoList); // 요청 리스트를 모델에 추가
        model.addAttribute("diary", dto); // 다이어리 정보를 모델에 추가
        model.addAttribute("diaryNum", diaryNum); // 다이어리 번호를 모델에 추가
        log.debug("가입 요청 리스트: {}", dtoList);

        return "share/RegisterRequest";
    }

    /**
     * 공유 다이어리 가입 요청 수락 메서드
     * @param requestDTO 가입 요청 데이터 (다이어리 번호와 사용자 ID 포함)
     * @return 가입 요청 처리 결과를 ResponseEntity로 반환 (성공 또는 오류 메시지)
     */
    @ResponseBody
    @PostMapping("/acceptRequest")
    public ResponseEntity<String> acceptRequest(@RequestBody AcceptRequestDTO requestDTO) {
        try {
            Integer diaryNum = requestDTO.getDiaryNum(); // 다이어리 번호 가져오기
            String memberId = requestDTO.getMemberId(); // 사용자 ID 가져오기

            log.debug("가입 요청 수락 - 다이어리 번호: {}, 사용자 ID: {}", diaryNum, memberId);

            // 가입 요청 수락 처리
            shareDiaryService.acceptRegister(diaryNum, memberId);

            return ResponseEntity.ok("가입 요청을 수락하였습니다.");
        } catch (Exception e) {
            log.error("가입 요청 수락 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가입 요청 수락 중 오류 발생");
        }
    }

    /**
     * 공유 다이어리 가입 요청 거절 메서드
     * @param diaryNum 다이어리의 고유 번호
     * @param memberId 사용자 ID
     * @return 가입 요청 처리 결과를 ResponseEntity로 반환 (성공 또는 오류 메시지)
     */
    @ResponseBody
    @PostMapping("/reject")
    public ResponseEntity<String> rejectRequest(@RequestParam("diaryNum") Integer diaryNum,
                                                @RequestParam("memberId") String memberId) {
        try {
            log.debug("거절할 다이어리 번호: {}", diaryNum);
            log.debug("거절할 사용자 ID: {}", memberId);

            // 가입 요청 거절 처리
            shareDiaryService.rejectRegister(diaryNum, memberId);

            return ResponseEntity.ok("가입 요청을 거절하였습니다.");
        } catch (Exception e) {
            log.error("가입 요청 거절 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("가입 요청 거절 중 오류 발생");
        }
    }
    /**
     * 가입한 공유다이어리를 탈퇴하는 메서드
     *
     * @return
     */
    @GetMapping("withdrawal")
    public String withdrawal(@RequestParam("diaryNum") Integer diaryNum, @AuthenticationPrincipal AuthenticatedUser user) {

        shareDiaryService.withdrawal(diaryNum, user.getUsername());

        return "share/main";
    }


}
