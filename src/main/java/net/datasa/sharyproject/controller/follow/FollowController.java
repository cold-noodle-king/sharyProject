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
import org.springframework.web.bind.annotation.ResponseBody;

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
     * 팔로우 전체 목록 페이지로 이동
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
     * 팔로우하기
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
     * 언팔로우하기
     * @param followingId
     * @return
     */
    @PostMapping("/follow/delete")
    public String deleteFollow(@RequestParam("followingId") String followingId) {
        try {
            followService.unfollow(followingId);
        } catch (Exception e) {
            log.error("언팔로우 중 오류 발생", e);
        }
        return "redirect:/followAll";
    }

    /**
     * 전체 사용자 목록을 검색하는 메서드
     * @param query 검색어
     * @param model 모델에 검색 결과 담기
     * @return 전체 사용자 목록 페이지
     */
/*    @GetMapping("/allUsers")
    public String getAllUsers(@RequestParam(value = "query", required = false) String query, Model model) {
        String currentUserId = followService.getCurrentUserId();
        List<MemberDTO> allUsers;

        if (query != null && !query.isEmpty()) {
            allUsers = followService.searchAllUsers(query, currentUserId);
        } else {
            allUsers = followService.getAllUsersExceptCurrentUser(currentUserId);
        }

        model.addAttribute("allUsers", allUsers);
        return "follow/allUsers"; // 전체 사용자 목록을 보여주는 HTML 페이지
    }*/


    /**
     * 전체 사용자 검색(ajax 요청)
     * @param query
     * @return
     */
    @GetMapping("/searchUsers")
    @ResponseBody
    public List<MemberDTO> searchUsersAjax(@RequestParam("query") String query) {
        String currentUserId = followService.getCurrentUserId();
        List<MemberDTO> matchingUsers = followService.searchAllUsers(query, currentUserId);
        return matchingUsers;
    }
}
