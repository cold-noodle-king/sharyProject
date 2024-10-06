package net.datasa.sharyproject.controller;

import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.PortfolioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final PortfolioService portfolioService;

    public HomeController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("home")   //info 컨트롤러와 경로 바꾸기 ("home")
    public String home(Model model, @AuthenticationPrincipal AuthenticatedUser user) {


        // PortfolioService를 통해 전체 공개된 노트 데이터를 가져옴
        List<PersonalNoteDTO> publicNotes = portfolioService.getPublicNotes();
        model.addAttribute("publicNotes", publicNotes);  // publicNotes 데이터를 뷰로 전달
        return "home";  // home.html로 이동
    }

    @GetMapping("/emotion-popup")
    public String emotion_popup() {
        return "fragments/emotion_popup";
    }

}