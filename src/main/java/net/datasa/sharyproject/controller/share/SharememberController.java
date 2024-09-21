package net.datasa.sharyproject.controller.share;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.share.ShareDiaryDTO;
import net.datasa.sharyproject.domain.dto.share.ShareMemberDTO;
import net.datasa.sharyproject.repository.share.ShareDiaryRepository;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.personal.CoverTemplateService;
import net.datasa.sharyproject.service.personal.NoteTemplateService;
import net.datasa.sharyproject.service.share.ShareDiaryService;
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

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum);
        model.addAttribute("diary", dto);
        List<ShareMemberDTO> dtoList = shareDiaryService.getMemberList(diaryNum, user.getUsername());
        model.addAttribute("list", dtoList);

        return "share/MemberList";
    }

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

    @ResponseBody
    @PostMapping("/acceptRequest")
    public ResponseEntity<String> acceptRequest(@RequestBody Map<String, Object> requestData) {
        Integer diaryNum = (Integer) requestData.get("diaryNum");
        String memberId = (String) requestData.get("member");

        log.debug("다이어리넘버: {}, 요청한사용자: {}", diaryNum, memberId);

        shareDiaryService.acceptRegister(diaryNum, memberId);

        return ResponseEntity.ok("가입 요청을 수락하였습니다.");
    }

}
