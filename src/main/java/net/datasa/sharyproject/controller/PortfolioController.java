package net.datasa.sharyproject.controller;

import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;
    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * 노트 번호를 기반으로 노트 세부 정보를 가져오는 엔드포인트
     * @param noteNum 노트 번호
     * @return PersonalNoteDTO - 노트 세부 정보
     */
    @GetMapping("/portfolio/viewNote/{noteNum}")
    public PersonalNoteDTO viewNoteDetails(@PathVariable("noteNum") Integer noteNum) {
        return portfolioService.getNoteByNum(noteNum);
    }

    /**
     * 회원 ID를 기반으로 회원의 프로필 정보를 가져오는 엔드포인트
     * @param memberId 회원 ID
     * @return ResponseEntity - 프로필 정보 또는 오류 메시지
     */
    @GetMapping("/portfolio/member/profile/{memberId}")
    public ResponseEntity<?> getMemberProfile(@PathVariable("memberId") String memberId) {
        try {
            ProfileDTO profile = portfolioService.getMemberProfileById(memberId);

            // 데이터가 제대로 반환되고 있는지 확인
            System.out.println("Profile data: " + profile);

            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            // 예외 메시지를 클라이언트로 전달
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
