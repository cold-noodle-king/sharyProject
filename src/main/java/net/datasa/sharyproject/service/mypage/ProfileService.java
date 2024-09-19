package net.datasa.sharyproject.service.mypage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.mypage.ProfileDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.repository.mypage.ProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProfileService {
    private final ProfileRepository profileRepository;

    // 파일 업로드 경로 주입
    @Value("${file.upload-dir}")
    private String uploadDir;

    public ProfileDTO updateProfile(MultipartFile profileImage, String ment, ProfileEntity profile) {
        // 기존 프로필이 있는지 확인
      //  ProfileEntity profile = profileRepository.findByMember(member).orElse(null);

        // 기존 프로필이 없으면 새로 생성
        /*if (profile == null) {
            profile = ProfileEntity.builder()
                    .member(member)
                    .profilePicture("/images/default.png")  // 기본 이미지 설정
                    .ment("")  // 기본 소개글
                    .build();
        }
*/
        // 이미지 파일 저장 처리
        if (!profileImage.isEmpty()) {
            String originalFilename = profileImage.getOriginalFilename();
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;
//            String saveDirectory = "/path/to/save/images/";

            // 저장할 디렉토리 경로 설정
            String saveDirectory = uploadDir + "/profile/";
            File directory = new File(saveDirectory);
            if (!directory.exists()) {
                directory.mkdirs();  // 디렉토리가 없으면 생성
            }

            try {
                File destinationFile = new File(directory, savedFilename);
                profileImage.transferTo(destinationFile);
                // 웹에서 접근 가능한 경로로 설정
                profile.setProfilePicture("/uploads/profile/" + savedFilename);
                profile.setProfileOriginalName(originalFilename);
//                File destinationFile = new File(saveDirectory + savedFilename);
//                profileImage.transferTo(destinationFile);
//                profile.setProfilePicture(savedFilename);
//                profile.setProfileOriginalName(originalFilename);
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }
        }

        // 소개글 업데이트
        profile.setMent(ment);

        // 저장 후 DTO로 반환
        ProfileEntity updatedProfile = profileRepository.save(profile);
        return ProfileDTO.builder()
                .profileNum(updatedProfile.getProfileNum())
                .profilePicture(updatedProfile.getProfilePicture())
                .profileOriginalName(updatedProfile.getProfileOriginalName())
                .ment(updatedProfile.getMent())
                .member(updatedProfile.getMember())
                .build();
    }

    public ProfileEntity findByMember(MemberEntity member) {
        return profileRepository.findByMember(member).orElse(null);
    }

    public void saveProfile(ProfileEntity profile) {
        profileRepository.save(profile);
    }
}