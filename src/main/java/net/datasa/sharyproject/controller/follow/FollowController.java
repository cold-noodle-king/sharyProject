package net.datasa.sharyproject.controller.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.service.follow.FollowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FollowController {

    private final FollowService followService;

    /**
     * 메인화면으로 이동
     */
    @GetMapping("/follow")
    public String follow() {
        return "follow/follow";
    }

    /**
     * 저장 테스트
     */
    @GetMapping("insert")
    public String insert() {
        try {
            followService.insert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/followAll";
    }

    /**
     * 팔로우 버튼 클릭 시 호출되는 메서드
     */
    @GetMapping("/followUser")
    public String followUser(@RequestParam("followerId") String followerId,
                             @RequestParam("followingId") String followingId) {
        try {
            followService.follow(followerId, followingId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/followAll";
    }


    /**
     * 전체 팔로우 목록 조회
     */
    @GetMapping("/followAll")
    public String getFollowList(Model model) {
        List<FollowDTO> followList = followService.followAll();
        model.addAttribute("followList", followList);
        return "follow/followAll";
    }

    /**
     * 팔로우 관계 삭제
     */
    @GetMapping("/follow/delete")
    public String deleteFollow(@RequestParam("followerId") String followerId,
                               @RequestParam("followingId") String followingId) {
        followService.delete(followerId, followingId);
        return "redirect:/followAll"; // 삭제 후 전체 목록 페이지로 리다이렉트
    }
}
