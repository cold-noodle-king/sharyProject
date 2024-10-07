package net.datasa.sharyproject.controller;

import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.service.member.MemberService;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.mypage.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/header")
@ControllerAdvice
public class ProfileController {

    private final ProfileService profileService;
    private final MemberService memberService;

    // 생성자 추가
    public ProfileController(ProfileService profileService, MemberService memberService) {
        this.profileService = profileService;
        this.memberService = memberService;
    }

    @GetMapping("/profile-image")
    public ResponseEntity<ProfileDTO> getProfileImage(@AuthenticationPrincipal AuthenticatedUser user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MemberEntity member = memberService.findById(user.getUsername())
                .orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ProfileEntity profile = profileService.findByMember(member)
                .orElseGet(() -> ProfileEntity.builder()
                        .profilePicture("/images/default_profile.png")
                        .build());

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfilePicture(profile.getProfilePicture());

        return ResponseEntity.ok(profileDTO);
    }
}
