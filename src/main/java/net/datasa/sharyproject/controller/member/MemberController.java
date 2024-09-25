package net.datasa.sharyproject.controller.member;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.member.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("member")
@Controller
public class MemberController {

    private final MemberService memberService;
    /**
     * 로그인
     * @return 로그인페이지로 이동 html
     */
    @GetMapping("loginForm")
    public String loginForm() {
        return "member/loginForm";
    }

    /**
     * 회원가입
     *
     * @return 회원가입페이지로 이동
     */
    @GetMapping("joinForm")
    public String joinForm() {
        return "member/joinForm";
    }

    /**
     * 회원가입
     *
     * @return 로그인 폼으로 이동
     */
    @PostMapping("join")
    public String join(@ModelAttribute MemberDTO memberDTO) {
        log.debug("전달된 회원정보: {}", memberDTO); // 어떤 값이 넘어갔는지 찍어놓기

        memberService.join(memberDTO);
        return "member/loginForm";
    }


    /**
     * 회원가입페이지에서 "ID중복확인" 버튼을 클릭하면 새창으로 보여줄 검색 페이지로 이동
     * @return ID검색 HTML파일 경로
     */
    @GetMapping("idCheck")
    public String idCheck() {
        return "member/idCheck";
    }

    /**
     * ID중복확인 페이지에서 검색 요청했을때 처리
     * @param searchId 검색할 아이디
     * @return ID검색 HTML파일 경로
     */
    @PostMapping("idCheck")//html form에서 action에 정한 이름과 같아야함
    public String idCheck(@RequestParam("searchId") String searchId, Model model) {

        //검색할 아이디를 서비스로 보내서 사용 가능한지 조회 (ture이면 사용가능)
        boolean result = memberService.findId(searchId); //스트링 옵셔널객체로 나옴 널이냐아니냐 널일땐 써도된다 아닐땐 false리턴하게
        //검색할 아이디와 결과를 모델에 저장, 모델로 담으면 리다이렉트할때 값이 사라짐!
        model.addAttribute("searchId", searchId);
        model.addAttribute("result", result);

        //ID검색 페이지로 포워딩
        return "member/idCheck";
    }

    @GetMapping("nickCheck")
    public String nickCheck() {
        return "member/nickCheck";
    }

    @PostMapping("nickCheck")//html form에서 action에 정한 이름과 같아야함
    public String nickCheck(@RequestParam("searchNick") String searchNick, Model model) {

        //검색할 아이디를 서비스로 보내서 사용 가능한지 조회 (ture이면 사용가능)
        boolean result = memberService.findNick(searchNick); //스트링 옵셔널객체로 나옴 널이냐아니냐 널일땐 써도된다 아닐땐 false리턴하게
        //검색할 아이디와 결과를 모델에 저장, 모델로 담으면 리다이렉트할때 값이 사라짐!
        model.addAttribute("searchNick", searchNick);
        model.addAttribute("result", result);

        //ID검색 페이지로 포워딩
        return "member/nickCheck";
    }

    @GetMapping("category")
    public String category(@AuthenticationPrincipal AuthenticatedUser user, Model model
            , @ModelAttribute MemberDTO memberDTO) {
        // 로그인된 사용자 정보에서 memberId 가져오기
        memberDTO.setMemberId(user.getMemberId());
//       String memberId = user.getMemberId();

        // memberId를 모델에 추가하여 뷰로 전달
        model.addAttribute("member", memberDTO);

        // 디버깅용 로그 출력
        log.debug("넘어오냐구우우우우ㅜ 로그인된 사용자 memberId: {}", memberDTO.getMemberId());
        return "member/category";
    }

    @PostMapping("category")
    public String category(@AuthenticationPrincipal AuthenticatedUser user,
                           @RequestParam("selectedCategory") List<String> selectedCategories) {
// 유저 카테고리 테이블에 memberId와 선택된 카테고리를 저장하는 로직 구현
        // 예: userCategoryService.saveCategory(memberId, selectedCategory);
        String memberId = user.getMemberId();
        // List<String>으로 바로 카테고리 ID들을 받아 처리
        memberService.selectedCategories(memberId, selectedCategories);
        // 선택된 카테고리 리스트를 서비스로 전달하여 저장
        log.debug("로그인된 사용자 memberId: {}", memberId);
        System.out.println("아아아ㅏ 전달된 Member ID: " + memberId);
        System.out.println("제바아아알 전달된 Selected Category: " + selectedCategories);
        // 콤마로 구분된 카테고리 ID 리스트를 배열로 변환
       // memberService.saveCategory(memberId, categoryIds);
        return "redirect:/home";
    }
}
