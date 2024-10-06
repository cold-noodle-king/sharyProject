package net.datasa.sharyproject.controller;

import net.datasa.sharyproject.service.member.MemberService;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.mypage.ProfileService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ProfileController {

    private final ProfileService profileService;
    private final MemberService memberService;

    // 클래스 이름과 생성자 이름을 일치시킴
    public ProfileController(ProfileService profileService, MemberService memberService) {
        this.profileService = profileService;
        this.memberService = memberService;
    }

    @ModelAttribute
    public void addProfileToModel(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        if (user != null) {
            MemberEntity member = memberService.findById(user.getUsername())
                    .orElse(null);
            if (member != null) {
                ProfileEntity profile = profileService.findByMember(member)
                        .orElseGet(() -> ProfileEntity.builder()
                                .profilePicture("/images/default_profile.png")
                                .build());
                model.addAttribute("profile", profile);
            }
        }
    }
}
