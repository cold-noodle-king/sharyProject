package net.datasa.sharyproject.controller;

import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/portfolio/viewNote/{noteNum}")
    public PersonalNoteDTO viewNoteDetails(@PathVariable("noteNum") Integer noteNum) {
        // 노트 번호로 노트 정보를 가져옴
        return portfolioService.getNoteByNum(noteNum);
    }
}