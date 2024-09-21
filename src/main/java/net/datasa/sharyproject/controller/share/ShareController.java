package net.datasa.sharyproject.controller.share;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.personal.CoverTemplateDTO;
import net.datasa.sharyproject.domain.dto.personal.NoteTemplateDTO;
import net.datasa.sharyproject.domain.dto.share.SelectedNoteDTO;
import net.datasa.sharyproject.domain.dto.share.ShareDiaryDTO;
import net.datasa.sharyproject.domain.dto.share.ShareMemberDTO;
import net.datasa.sharyproject.domain.dto.share.ShareNoteDTO;
import net.datasa.sharyproject.domain.entity.share.ShareDiaryEntity;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Controller
@RequestMapping("share")
public class ShareController {

    private final CoverTemplateService coverTemplateService;
    private final NoteTemplateService noteTemplateService;
    private final ShareDiaryService shareDiaryService;
    private final ShareDiaryRepository shareDiaryRepository;

    //공유 다이어리 메인 페이지로 이동
    //내가 생성한 다이어리 페이지를 디폴트로 설정
    @GetMapping("main")
    public String viewMain(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        //내가 생성한 다이어리 리스트를 불러오는 메서드
        List<ShareDiaryDTO> createdDiaries = shareDiaryService.getCreatedList(user.getUsername());

        log.debug("불러온 다이어리들 정보:{}",createdDiaries);
        model.addAttribute("diaryList", createdDiaries);

        return "share/Main";
    }

    //내가 생성한 공유 다이어리로 이동
    @GetMapping("createdDiary")
    public String viewCreatedDiary(@RequestParam("diaryNum") Integer diaryNum
                                 , @AuthenticationPrincipal AuthenticatedUser user
                                 , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum, user.getUsername());
        log.debug("가져온 다이어리 정보:{}", dto);
        model.addAttribute("diary", dto);

        return "share/createdDiary";
    }

    //내가 가입한 공유 다이어리 리스트 페이지 출력
    @GetMapping("joinedList")
    public String joined(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        List<ShareDiaryDTO> joinedDiaries = shareDiaryService.getJoinedList(user.getUsername());
        log.debug("가입한 다이어리 리스트:{}", joinedDiaries);
        model.addAttribute("diaryList", joinedDiaries);

        return "share/joinedList";
    }

    //내가 가입한 공유 다이어리로 이동
    @GetMapping("joinedDiary")
    public String joinedDiary(@RequestParam("diaryNum") Integer diaryNum
                            , @AuthenticationPrincipal AuthenticatedUser user
                            , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getJoinedDiary(diaryNum, user.getUsername());
        log.debug("가져온 다이어리 정보:{}", dto);

        return "share/JoinedDiary";
    }

    //다이어리 카테고리 선택 페이지로 이동
    @GetMapping("categorySelect")
    public String categorySelect() {
        return "share/CategorySelect";
    }

    //리스트 형태로 받아온 카테고리를 문자열로 변환하는 메서드
    public class CategoryUtil {

        // 리스트를 쉼표로 구분된 문자열로 변환
        public static String listToString(List<String> categories) {
            return String.join(", ", categories);  // 쉼표와 공백으로 구분하여 문자열 생성
        }
    }

    @PostMapping("coverSelect")
    public String coverSelect(@RequestParam("categories") List<String> categories, Model model) {
        log.debug("지정한 카테고리: {}", categories);

        String categoryName = CategoryUtil.listToString(categories);
        log.debug("문자열로 변환한 카테고리 이름:{}", categoryName);

        model.addAttribute("categoryName", categoryName);

        return "share/CoverSelect";
    }

    //다이어리 카테고리 수정 페이지로 이동
    @GetMapping("categoryUpdate")
    public String categoryUpdate() {
        return "share/CategoryUpdate";
    }

    //다이어리 커버 페이지로 이동
    @GetMapping("cover")
    public String cover() {
        return "share/CoverSelect";
    }

    //다이어리를 DB에 저장하는 메서드
    @PostMapping("saveDiary")
    public String saveDiary(@ModelAttribute ShareDiaryDTO shareDiaryDTO
                          , @AuthenticationPrincipal AuthenticatedUser user
                          , RedirectAttributes redirectAttributes){

        log.debug("컨틀롤러로 갔는지 확인:{}", shareDiaryDTO);
        ShareDiaryEntity entity = shareDiaryService.saveDiary(shareDiaryDTO, user);
        log.debug("엔티티로 잘 넘어왔니?:{}", entity);

        redirectAttributes.addFlashAttribute("msg", "다이어리가 저장되었습니다. 이어서 노트를 작성하시겠습니까?");
        redirectAttributes.addFlashAttribute("diaryNum", entity.getShareDiaryNum());

        return "redirect:/share/main";
    }

    //다이어리 관리 페이지로 이동
    @GetMapping("manageDiary")
    public String manageDiary(@RequestParam("diaryNum") Integer diaryNum
                            , @AuthenticationPrincipal AuthenticatedUser user
                            , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum, user.getUsername());
        model.addAttribute("diary", dto);

        return "share/manageDiary";
    }

    //내가 생성한 공유다이어리를 삭제하는 메서드
    @GetMapping("deleteDiary")
    public String deleteDiary() {

        return "share/main";
    }

    //가입한 공유다이어리를 탈퇴하는 메서드
    @PostMapping("withdrawal")
    public String withdrawal() {
        
        return "share/main";
    }

    //공유다이어리 정보 수정 페이지 출력
    @GetMapping("infoUpdate")
    public String infoUpdate(@RequestParam("diaryNum") Integer diaryNum
                           , @AuthenticationPrincipal AuthenticatedUser user
                           , Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum, user.getUsername());
        model.addAttribute("diary", dto);

        return "share/infoUpdate";
    }

    //공유 다이어리 소개 멘트를 수정하는 메서드
    @PostMapping("bio")
    public String bio(@RequestParam("diaryNum") Integer diaryNum
                    , @RequestParam("diaryBio") String diaryBio
                    , @AuthenticationPrincipal AuthenticatedUser user
                    , Model model){

        shareDiaryService.updateBio(diaryNum, diaryBio, user.getUsername());

        return "redirect:/share/infoUpdate?diaryNum=" + diaryNum;
    }

    //공유 다이어리 멤버 관리 페이지로 이동
    @GetMapping("viewMember")
    public String viewMember(@RequestParam("diaryNum") Integer diaryNum
                            ,@AuthenticationPrincipal AuthenticatedUser user
                            ,Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum, user.getUsername());
        model.addAttribute("diary", dto);

        return "share/ViewMember";
    }

    //공유 다이어리 멤버 리스트 출력
    @GetMapping("memberList")
    public String memberList(@RequestParam("diaryNum") Integer diaryNum
                            ,@AuthenticationPrincipal AuthenticatedUser user
                            ,Model model) {

        ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum, user.getUsername());
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

            ShareDiaryDTO dto = shareDiaryService.getDiary(diaryNum, user.getUsername());
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

    @GetMapping("getCoverTemplates")
    @ResponseBody
    public List<CoverTemplateDTO> getCoverTemplates() {
        return coverTemplateService.getCoverTemplates(); // 커버 템플릿 리스트 반환
    }

    // 노트 선택 페이지로 이동하는 메서드 추가
    @GetMapping("note")
    public String note(@RequestParam("diaryNum") Integer diaryNum, Model model) {

        model.addAttribute("shareDiaryNum", diaryNum);
        return "share/NoteSelect";  // 노트 템플릿 선택 페이지로 이동
    }

    // 노트 템플릿 데이터를 제공하는 API
    @GetMapping("getNoteTemplates")
    @ResponseBody
    public List<NoteTemplateDTO> getNoteTemplates() {
        return noteTemplateService.getNoteTemplates();  // 노트 템플릿 리스트 반환
    }

    @PostMapping("noteForm")
    public String createDiary(@ModelAttribute SelectedNoteDTO dto, Model model) {
        log.debug("선택된 노트정보:{}", dto);
        NoteTemplateDTO noteTemplate = noteTemplateService.getNoteTemplateById(dto.getNoteNum());

        if (noteTemplate == null || noteTemplate.getNoteImage() == null) {
            throw new RuntimeException("NoteTemplate 또는 이미지 경로가 존재하지 않습니다.");
        }

        model.addAttribute("noteTemplate", noteTemplate);
        model.addAttribute("diary", dto);
        return "share/NoteForm";  // 다이어리 작성 페이지로 이동
    }

    @PostMapping("saveNote")
    public String saveNote(@ModelAttribute ShareNoteDTO shareNoteDTO
                         , @AuthenticationPrincipal AuthenticatedUser user
                         , RedirectAttributes redirectAttributes) {



        return "share/main";
    }

    //전체 공유 다이어리 리스트를 출력하는 메서드
    @GetMapping("listAll")
    public String listAll(@AuthenticationPrincipal AuthenticatedUser user, Model model) {

        List<ShareDiaryDTO> diaryList = shareDiaryService.getDiaryList();
        model.addAttribute("diaryList", diaryList);

        log.debug("가져온 다이어리 리스트들 보여줘:{}", diaryList);

        return "share/shareMemberTest";
    }

}
