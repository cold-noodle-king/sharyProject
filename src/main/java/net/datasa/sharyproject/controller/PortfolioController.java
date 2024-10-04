package net.datasa.sharyproject.controller;

import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalLikeDTO;
import net.datasa.sharyproject.domain.dto.personal.PersonalNoteDTO;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.service.PortfolioService;
import net.datasa.sharyproject.service.follow.FollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final FollowService followService; // FollowService 의존성 추가
    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    // 생성자를 통한 의존성 주입 (FollowService 추가)
    public PortfolioController(PortfolioService portfolioService, FollowService followService) {
        this.portfolioService = portfolioService;
        this.followService = followService;
    }

    /**
     * 노트 번호를 기반으로 노트 세부 정보를 가져오는 엔드포인트
     * @param noteNum 노트 번호
     * @return PersonalNoteDTO - 노트 세부 정보
     */
    @GetMapping("/portfolio/viewNote/{noteNum}")
    public PersonalNoteDTO viewNoteDetails(@PathVariable("noteNum") Integer noteNum) {
        // 노트 번호가 제대로 전달되는지 로그 확인
        logger.info("컨트롤러에서 전달된 노트 번호: " + noteNum);

        // 서비스에서 노트 정보를 가져와서 반환
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
            // 서비스에서 회원의 프로필 정보를 가져옴
            ProfileDTO profile = portfolioService.getMemberProfileById(memberId);

            // 프로필 정보를 로그로 확인
            logger.info("Profile data: " + profile);

            // 성공 시 프로필 정보를 클라이언트에 반환
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            // 예외 발생 시 로그를 남기고, 404 상태로 클라이언트에 메시지 전달
            logger.error("프로필 정보를 가져오는 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ========== 추가된 좋아요 관련 엔드포인트 ==========

    /**
     * 좋아요 상태를 토글하는 엔드포인트
     * 사용자가 노트에 좋아요를 누르거나 취소할 수 있는 기능
     * @param personalNoteNum 좋아요를 누를 노트 번호
     * @param authentication 인증 정보 (로그인한 사용자)
     * @return ResponseEntity<PersonalLikeDTO> - 좋아요 상태 정보 반환
     */
    @PostMapping("/portfolio/like/{personalNoteNum}")
    public ResponseEntity<PersonalLikeDTO> toggleLike(
            @PathVariable Integer personalNoteNum,
            Authentication authentication) {
        try {
            String memberId = authentication.getName(); // 로그인한 사용자의 ID 가져오기

            // 좋아요 상태를 토글하고, 결과를 DTO로 반환
            PersonalLikeDTO likeDTO = portfolioService.toggleLike(personalNoteNum, memberId);

            // 성공적으로 좋아요 상태를 변경한 경우, 새로운 상태를 클라이언트에 반환
            return ResponseEntity.ok(likeDTO);
        } catch (RuntimeException e) {
            // 오류가 발생한 경우 로그를 남기고, 500 에러 상태로 클라이언트에 반환
            logger.error("좋아요 상태 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

