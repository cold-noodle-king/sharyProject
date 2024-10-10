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


@Slf4j  // 로깅을 위한 Lombok 어노테이션, 로그를 출력할 때 사용됨
@RequiredArgsConstructor  // final 필드를 자동으로 생성자로 주입하는 Lombok 어노테이션
@Controller  // 이 클래스가 컨트롤러임을 나타내는 Spring 어노테이션
public class FollowController {

    // 의존성 주입을 위한 final 필드
    private final FollowService followService;  // 팔로우 관련 로직을 처리하는 서비스
    private final MemberService memberService;  // 회원 정보 관련 로직을 처리하는 서비스
    private final ProfileService profileService; // 프로필 정보 관련 로직을 처리하는 서비스

    /**
     * follow 페이지 렌더링
     * @return follow/follow - follow 페이지를 보여줌
     */
    @GetMapping("/follow")
    public String follow() {

        return "follow/follow";  // "follow/follow" 경로의 HTML 파일을 반환, 즉 follow 페이지를 표시
    }

    /**
     * 모든 사용자 팔로우/언팔로우 처리 기능
     * @return 리다이렉트 /followAll 페이지로 이동
     */
    @GetMapping("/insert")
    public String insert() {
        try {
            // 모든 사용자에 대해 팔로우 또는 언팔로우를 처리하는 메서드
            followService.insert();
        } catch (Exception e) {
            // 오류가 발생하면 로그에 오류 내용을 출력
            log.error("Error during insert operation", e);
        }
        // 처리 후 followAll 페이지로 리다이렉트 (팔로우 목록 페이지로 이동)
        return "redirect:/followAll";
    }

    /**
     * 현재 사용자의 팔로우 목록과 팔로워 목록 페이지로 이동
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @return follow/followAll 페이지
     */
    @GetMapping("/followAll")
    public String getFollowList(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        // 현재 사용자의 팔로워 목록을 가져옴
        List<FollowDTO> followers = followService.getFollowersForCurrentUser();
        // 현재 사용자가 팔로우하는 사람들의 목록을 가져옴
        List<FollowDTO> following = followService.getFollowingForCurrentUser();

        // 현재 로그인한 사용자의 정보를 가져옴
        MemberDTO memberDTO = memberService.getMember(user.getUsername());
        model.addAttribute("member", memberDTO);  // 사용자 정보를 모델에 추가

        log.info("개인정보 내용 : {}", memberDTO);  // 사용자 정보를 로그에 출력

        // 사용자 ID로 해당 사용자를 찾음
        MemberEntity member = memberService.findById(user.getMemberId())
                .orElseThrow(() -> new RuntimeException("사용자 (" + user + ")를 찾을 수 없습니다."));  // 사용자를 찾지 못했을 경우 예외 발생

        // 프로필 정보를 데이터베이스에서 가져오고, 없으면 기본 프로필을 생성하여 저장
        ProfileEntity profile = profileService.findByMember(member)
                .orElseGet(() -> {
                    // 프로필 정보가 없으면 기본 프로필을 생성하고 저장
                    ProfileEntity defaultProfile = ProfileEntity.builder()
                            .member(member)  // 회원 정보와 연결
                            .profilePicture("/images/profile.png")  // 기본 프로필 이미지 설정
                            .ment("")  // 기본 소개글 설정
                            .build();
                    profileService.saveProfile(defaultProfile);  // 기본 프로필을 데이터베이스에 저장
                    return defaultProfile;  // 생성된 프로필을 반환
                });

        // 모델에 프로필 정보를 추가
        model.addAttribute("profile", profile);
        model.addAttribute("followers", followers);  // 팔로워 목록을 모델에 추가
        model.addAttribute("following", following);  // 팔로잉 목록을 모델에 추가
        log.info("Followers: {}", followers);  // 팔로워 정보를 로그에 출력
        log.info("Following: {}", following);  // 팔로잉 정보를 로그에 출력

        // 팔로우/팔로워 목록을 보여주는 페이지로 이동
        return "follow/followAll";
    }

    /**
     * 특정 사용자를 팔로우하는 로직
     * @param followerId 팔로우하는 사람의 ID
     * @param followingId 팔로우 받을 사람의 ID
     * @return followAll 페이지로 리다이렉트
     */
    @PostMapping("/followUser")
    public String followUser(@RequestParam("followerId") String followerId,
                             @RequestParam("followingId") String followingId) {
        try {
            // 팔로우 서비스를 통해 팔로우 처리
            followService.follow(followerId, followingId);
        } catch (Exception e) {
            // 팔로우 처리 중 오류가 발생하면 로그에 오류 출력 후 followAll 페이지로 오류 메시지와 함께 리다이렉트
            log.error("Error during follow operation", e);
            return "redirect:/followAll?error=follow_error";  // 오류 메시지 전달
        }
        // 팔로우 처리 후 followAll 페이지로 리다이렉트
        return "redirect:/followAll";
    }

    /**
     * AJAX 요청을 통해 특정 사용자 팔로우 처리
     * @param followerId 팔로우할 대상의 ID
     * @param user 현재 로그인한 사용자 정보
     * @return true 또는 false (팔로우 성공 여부)
     */
    @ResponseBody  // 이 메서드는 뷰가 아닌 JSON 형식의 데이터를 반환함
    @PostMapping("/follow")
    public boolean followUser(@RequestParam("followerId") String followerId,
                              @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            // 현재 사용자와 팔로우할 대상 사용자 정보 확인
            log.info("팔로우 요청: followerId={}, currentUser={}", followerId, user.getUsername());

            // 팔로우 요청 처리
            followService.follow(user.getUsername(), followerId);
        } catch (Exception e) {
            // 팔로우 처리 중 오류가 발생하면 로그에 출력
            log.error("팔로우 처리 중 오류 발생: ", e);
            return false;  // 실패 시 false 반환
        }
        return true;  // 성공 시 true 반환
    }

    /**
     * 특정 사용자를 언팔로우 처리
     * @param followingId 언팔로우할 대상의 ID
     * @param user 현재 로그인한 사용자 정보
     * @return true 또는 false (언팔로우 성공 여부)
     */
    @ResponseBody
    @DeleteMapping("/follow/delete")  // DELETE 요청을 처리
    public boolean unfollowUser(@RequestParam("followingId") String followingId,
                                @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            // 현재 사용자가 해당 사용자를 언팔로우 처리
            followService.unfollow(followingId);
        } catch (Exception e) {
            // 언팔로우 처리 중 오류가 발생하면 로그에 출력
            log.error("언팔로우 처리 중 오류 발생: ", e);
            return false;  // 실패 시 false 반환
        }
        return true;  // 성공 시 true 반환
    }


    /**
     * 언팔로우하기
     * @param followingId 언팔로우할 대상의 ID
     * @return followAll 페이지로 리다이렉트
     */
    @PostMapping("/follow/delete")  // POST 요청을 처리
    public String deleteFollow(@RequestParam("followingId") String followingId) {
        try {
            // 언팔로우 처리
            followService.unfollow(followingId);
        } catch (Exception e) {
            // 언팔로우 처리 중 오류가 발생하면 로그에 출력
            log.error("언팔로우 중 오류 발생", e);
        }
        // 처리 후 followAll 페이지로 리다이렉트
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
     * @param query 검색어
     * @return 검색된 사용자 목록을 JSON 형태로 반환
     */
    @GetMapping("/searchUsers")
    @ResponseBody  // JSON 형태로 반환
    public List<MemberDTO> searchUsersAjax(@RequestParam("query") String query) {
        // 현재 사용자 ID를 가져옴
        String currentUserId = followService.getCurrentUserId();
        // 검색어에 맞는 사용자 목록을 검색
        List<MemberDTO> matchingUsers = followService.searchAllUsers(query, currentUserId);
        return matchingUsers;  // 검색된 사용자 목록을 반환
    }

    /**
     * 사용자 프로필 및 팔로우 상태 정보 반환
     * @param memberId 조회할 회원의 ID
     * @param user 현재 로그인한 사용자 정보
     * @return 프로필 정보와 팔로우 상태를 JSON으로 반환
     */
    @GetMapping("/member/profile/{memberId}")
    @ResponseBody  // JSON 형태로 반환
    public Map<String, Object> getMemberProfile(@PathVariable String memberId, @AuthenticationPrincipal AuthenticatedUser user) {
        // 반환할 데이터를 저장할 맵 객체 생성
        Map<String, Object> response = new HashMap<>();

        // 해당 사용자의 정보를 가져옴
        MemberDTO member = memberService.getMember(memberId);
        response.put("nickname", member.getNickname());  // 사용자 닉네임을 응답에 추가

        // 팔로우 상태 확인 (현재 사용자가 해당 사용자를 팔로우하고 있는지 확인)
        boolean isFollowing = followService.isFollowing(user.getUsername(), memberId);
        response.put("isFollowing", isFollowing);  // 팔로우 상태를 응답에 추가

        return response;  // 프로필 정보와 팔로우 상태를 반환 (JSON 형식)
    }

}
