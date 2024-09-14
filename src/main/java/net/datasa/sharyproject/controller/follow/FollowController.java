package net.datasa.sharyproject.controller.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import net.datasa.sharyproject.service.follow.FollowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FollowController {

    private final FollowService followService;

    /**
     * follow 기능
     * @return
     */
    @GetMapping("/follow")
    public String follow() {
        return "follow/follow";
    }

    /**
     * insert 기능
     * @return
     */
    @GetMapping("/insert")
    public String insert() {
        try {
            followService.insert();
        } catch (Exception e) {
            log.error("Error during insert operation", e);
        }
        return "redirect:/followAll";
    }

    /**
     * followUser 기능
     * @param followerId
     * @param followingId
     * @return
     */
    @PostMapping("/followUser")
    public String followUser(@RequestParam("followerId") String followerId,
                             @RequestParam("followingId") String followingId) {
        try {
            followService.follow(followerId, followingId);
        } catch (Exception e) {
            log.error("Error during follow operation", e);
            return "redirect:/followAll?error=follow_error"; // 오류 메시지 전달
        }
        return "redirect:/followAll";
    }

    /**
     * followAll 기능
     * @param model
     * @return
     */
    @GetMapping("/followAll")
    public String getFollowList(Model model) {
        List<FollowDTO> followers = followService.getFollowersForCurrentUser();
        List<FollowDTO> following = followService.getFollowingForCurrentUser();

        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
        log.info("Followers: {}", followers);
        log.info("Following: {}", following);

        return "follow/followAll";
    }

    /**
     * followingId 기능
     * @param followingId
     * @return
     */
    @PostMapping("/follow/delete")
    public String deleteFollow(@RequestParam("followingId") String followingId) {
        try {
            followService.unfollow(followingId);
        } catch (Exception e) {
            log.error("Error during delete operation", e);
        }
        return "redirect:/followAll";
    }

    /**
     * allUsers 기능
     * @param model
     * @return
     */
    @GetMapping("/allUsers")
    public String getAllUsers(Model model) {
        String currentUserId = followService.getCurrentUserId(); // 현재 로그인한 사용자 ID 가져오기
        List<MemberDTO> allUsers = followService.getAllUsersExceptCurrentUser(currentUserId); // 현재 사용자 제외한 모든 사용자 가져오기

        model.addAttribute("allUsers", allUsers);
        return "follow/allUsers"; // 전체 사용자 목록을 보여주는 HTML 페이지
    }
}
