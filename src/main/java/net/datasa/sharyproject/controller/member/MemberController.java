package net.datasa.sharyproject.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.service.member.MemberService;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("member")
@Controller
public class MemberController {

    private final MemberService memberService;
    /**
     * 로그인
     *
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

//
//    /**
//     * 회원가입페이지에서 "ID중복확인" 버튼을 클릭하면 새창으로 보여줄 검색 페이지로 이동
//     * @return ID검색 HTML파일 경로
//     */
//    @GetMapping("idCheck")
//    public String idCheck() {
//        return "memberView/idCheck";
//    }
//
//    /**
//     * ID중복확인 페이지에서 검색 요청했을때 처리
//     * @param searchId 검색할 아이디
//     * @return ID검색 HTML파일 경로
//     */
//    @PostMapping("idCheck")//이름이 같지만 get과 다른경로임 다른이름으로 해도됨 대신 html form에서 action에 정한 이름과 같아야함
//    public String idCheck(@RequestParam("searchId") String searchId, Model model) {
//
//        //검색할 아이디를 서비스로 보내서 사용 가능한지 조회 (ture이면 사용가능)
//        boolean result = service.findId(searchId); //스트링 옵셔널객체로 나옴 널이냐아니냐 널일땐 써도된다 아닐땐 false리턴하게
//        //검색할 아이디와 결과를 모델에 저장, 모델로 담으면 리다이렉트할때 값이 사라짐!
//        model.addAttribute("searchId", searchId);
//        model.addAttribute("result", result);
//
//        //ID검색 페이지로 포워딩
//        return "memberView/idCheck";
//    }
//

}
