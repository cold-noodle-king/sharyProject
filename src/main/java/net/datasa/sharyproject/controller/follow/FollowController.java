package net.datasa.sharyproject.controller.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
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

    @GetMapping("/follow")
    public String follow() {
        return "follow/follow";
    }

    @GetMapping("/insert")
    public String insert() {
        try {
            followService.insert();
        } catch (Exception e) {
            log.error("Error during insert operation", e);
        }
        return "redirect:/followAll";
    }


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


    @PostMapping("/follow/delete")
    public String deleteFollow(@RequestParam("followingId") String followingId) {
        try {
            followService.unfollow(followingId);
        } catch (Exception e) {
            log.error("Error during delete operation", e);
        }
        return "redirect:/followAll";
    }
}
