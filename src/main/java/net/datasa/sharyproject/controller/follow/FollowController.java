package net.datasa.sharyproject.controller.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.follow.FollowService;
import net.datasa.sharyproject.service.member.MemberService;
import net.datasa.sharyproject.service.mypage.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FollowController {

    private final FollowService followService;
    private final MemberService memberService;
    private final ProfileService profileService;

    /**
     * follow 페이지 렌더링
     * @return
     */
    @GetMapping("/follow")
    public String follow() {

        return "follow/follow";
    }

    /**
     * 모든 사용자 팔로우/언팔로우 처리 기능
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
     * 현재 사용자의 팔로우 목록과 팔로워 목록 페이지로 이동
     * @param model
     * @return
     */
    @GetMapping("/followAll")
    public String getFollowList(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        List<FollowDTO> followers = followService.getFollowersForCurrentUser();
        List<FollowDTO> following = followService.getFollowingForCurrentUser();

        MemberDTO memberDTO = memberService.getMember(user.getUsername());
        model.addAttribute("member", memberDTO);

        log.info("개인정보 내용 : {}", memberDTO);

        MemberEntity member = memberService.findById(user.getMemberId())
                .orElseThrow(() -> new RuntimeException("사용자 (" + user + ")를 찾을 수 없습니다."));
        // 프로필 정보를 데이터베이스에서 가져옴
        ProfileEntity profile = profileService.findByMember(member)
                .orElseGet(() -> {
                    // 프로필 정보가 없으면 기본 프로필을 생성하여 반환
                    ProfileEntity defaultProfile = ProfileEntity.builder()
                            .member(member)
                            .profilePicture("/images/profile.png")  // 기본 이미지 설정
                            .ment("")  // 기본 소개글 설정
                            .build();
                    profileService.saveProfile(defaultProfile);  // 생성한 기본 프로필을 저장
                    return defaultProfile;
                });

        model.addAttribute("profile", profile);
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);
        log.info("Followers: {}", followers);
        log.info("Following: {}", following);

        return "follow/followAll";
    }

    /**
     * 특정 사용자를 팔로우하는 로직
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
     * AJAX 요청을 통해 특정 사용자 팔로우 처리
     * @param followerId
     * @param user
     * @return
     */
    @ResponseBody
    @PostMapping("/follow")
    public boolean followUser(@RequestParam("followerId") String followerId,
                              @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            // 현재 사용자와 팔로우할 대상 사용자 정보 확인
            log.info("팔로우 요청: followerId={}, currentUser={}", followerId, user.getUsername());

            // 팔로우 요청 처리
            followService.follow(user.getUsername(), followerId);
        } catch (Exception e) {
            // 예외 발생 시 로그에 출력
            log.error("팔로우 처리 중 오류 발생: ", e);
            return false; // 실패 시 false 반환
        }
        return true; // 성공 시 true 반환
    }

    /**
     * 특정 사용자를 언팔로우 처리
     * @param followingId
     * @param user
     * @return
     */
    @ResponseBody
    @DeleteMapping("/follow/delete")
    public boolean unfollowUser(@RequestParam("followingId") String followingId,
                                @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            // 현재 사용자가 해당 유저를 언팔로우
            followService.unfollow(followingId);
        } catch (Exception e) {
            log.error("언팔로우 처리 중 오류 발생: ", e);
            return false;
        }
        return true;
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

    /**
     * 사용자 프로필 및 팔로우 상태 정보 반환
     * @param memberId
     * @param user
     * @return
     */
    @GetMapping("/member/profile/{memberId}")
    @ResponseBody
    public Map<String, Object> getMemberProfile(@PathVariable String memberId, @AuthenticationPrincipal AuthenticatedUser user) {
        Map<String, Object> response = new HashMap<>();

        // 사용자 정보 불러오기
        MemberDTO member = memberService.getMember(memberId);
        response.put("nickname", member.getNickname());
        //response.put("ment", member.getMent());

        // 팔로우 상태 확인
        boolean isFollowing = followService.isFollowing(user.getUsername(), memberId);
        response.put("isFollowing", isFollowing);

        return response;  // JSON 형태로 반환
    }

}
