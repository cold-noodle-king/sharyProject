package net.datasa.sharyproject.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.chat.ChatDTO;
import net.datasa.sharyproject.domain.dto.chat.ChatMessageDTO;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.domain.dto.member.MemberDTO;
import net.datasa.sharyproject.domain.entity.member.MemberEntity;
import net.datasa.sharyproject.domain.entity.mypage.ProfileEntity;
import net.datasa.sharyproject.security.AuthenticatedUser;
import net.datasa.sharyproject.service.chat.ChatService;
import net.datasa.sharyproject.service.follow.FollowService;
import net.datasa.sharyproject.service.member.MemberService;
import net.datasa.sharyproject.service.mypage.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor  // 생성자를 자동으로 만들어주는 Lombok 어노테이션 (DI를 위한 필수 서비스들을 주입 받음)
@RequestMapping("/chat")  // 이 컨트롤러의 URL 기본 경로를 "/chat"으로 설정
@Slf4j  // 로그를 남길 수 있도록 해주는 Lombok 어노테이션
public class ChatController {


    // 필드 주입 대신 생성자를 통한 의존성 주입 방식
    private final ChatService chatService;      // 채팅 서비스
    private final FollowService followService; // 팔로우 서비스
    private final MemberService memberService; // 회원 서비스
    private final ProfileService profileService; // 프로필 서비스

    /**
     * 채팅 홈 화면을 보여주는 메서드 (팔로워들과의 모든 채팅 목록을 보여줌)
     * @param model - 모델에 채팅 목록을 담아 뷰로 전달
     * @return chat/chatHome - 채팅 홈 화면을 보여주는 뷰로 이동
     */
    @GetMapping
    public String showChatHome(Model model) {
        String currentUserId = followService.getCurrentUserId(); // 현재 사용자 ID 가져오기
        List<ChatDTO> chats = chatService.getAllChatsForUser(currentUserId); //사용자가 참여한 모든 채팅 가져오기
        model.addAttribute("chats", chats); // 채팅 목록을 모델에 추가
        return "chat/chatHome"; // 홈 화면 리턴
    }


    /**
     * 특정 채팅방에 대한 정보와 메시지를 보여주는 메서드
     * @param chatId - 채팅방 ID
     * @param participant1Id - 첫 번째 참여자 ID
     * @param participant2Id - 두 번째 참여자 ID
     * @param model - 모델에 채팅 정보와 메시지를 담아 뷰로 전달
     * @return chat/chatRoom - 채팅방 화면을 보여주는 뷰로 이동
     */
    @GetMapping("/room")
    public String chatRoom(
            @RequestParam(value = "chatId", required = false) Integer chatId,
            @RequestParam(value = "participant1Id", required = false) String participant1Id,
            @RequestParam(value = "participant2Id", required = false) String participant2Id,
            Model model) {

        ChatDTO chat; // 채팅방 정보를 담을 변수

        // chatId로 채팅방 조회, 없을 경우 참여자 ID로 채팅방 조회 또는 생성
        if (chatId != null) {
            // chatId로 채팅방 조회
            chat = chatService.getChatById(chatId);
        } else if (participant1Id != null && participant2Id != null) {
            // participantId로 채팅방 조회 또는 생성
            chat = chatService.findOrCreateChat(participant1Id, participant2Id);
        } else {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다.");
        }

        // 해당 채팅방의 메시지를 가져와서 모델에 추가
        List<ChatMessageDTO> messages = chatService.getMessages(chat.getChatId());
        model.addAttribute("chat", chat); // 채팅방 정보 추가
        model.addAttribute("messages", messages); // 채팅 메시지 목록 추가

        return "chat/chatRoom"; // 채팅방 화면을 리턴
    }


    /**
     * 채팅을 시작하는 폼을 보여주는 메서드 (팔로워, 팔로잉 목록을 함께 보여줌)
     * @param user - 현재 로그인한 사용자 정보
     * @param model - 모델에 팔로잉, 팔로워 목록과 사용자 정보를 담아 뷰로 전달
     * @return chat/chatForm - 채팅을 시작할 수 있는 폼 화면을 리턴
     */
    @GetMapping("/chatForm")
    public String showChatForm(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        String currentUserId = followService.getCurrentUserId();
        List<FollowDTO> following = followService.getFollowingForCurrentUser();
        List<FollowDTO> followers = followService.getFollowersForCurrentUser(); // 추가: 나를 팔로우하는 사용자 목록 가져오기

        // 팔로잉 및 팔로워 리스트를 모델에 추가하여 뷰로 전달
        model.addAttribute("following", following);
        model.addAttribute("followers", followers); // 추가: 팔로워 리스트 추가

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

        model.addAttribute("profile", profile); // 프로필 정보를 모델에 추가
        return "chat/chatForm"; // 채팅 시작 폼 화면으로 이동
    }

    /**
     * 채팅 메시지를 전송하는 메서드 (Ajax 요청을 처리)
     * @param chatId - 채팅방 ID
     * @param content - 전송할 메시지 내용
     * @return ResponseEntity<ChatMessageDTO> - 전송된 메시지를 담은 응답 객체
     */
    @PostMapping("/send")
    @ResponseBody // 이 메서드는 뷰가 아닌 JSON 형식의 데이터를 반환함
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestParam("chatId") int chatId,
                                                      @RequestParam("messageContent") String content) {
        String currentUserId = followService.getCurrentUserId(); // 현재 로그인한 사용자 ID 가져오기
        ChatMessageDTO messageDTO = chatService.sendMessage(chatId, currentUserId, content); // 메시지 전송

        return ResponseEntity.ok(messageDTO); // 성공적으로 전송된 메시지 반환
    }

    /**
     * 채팅방을 생성하거나 기존 채팅방으로 이동하는 메서드
     * @param participant1Id - 첫 번째 참여자 ID
     * @param participant2Id - 두 번째 참여자 ID
     * @param model - 모델에 채팅방 정보와 메시지를 담아 뷰로 전달
     * @return chat/chatRoom - 채팅방 화면으로 리다이렉트
     */
    @GetMapping("/chat/start")
    public String startChat(@RequestParam("participant1Id") String participant1Id,
                            @RequestParam("participant2Id") String participant2Id,
                            Model model) {
        // 참여자 ID로 채팅방 생성 또는 기존 채팅방 가져오기
        ChatDTO chat = chatService.findOrCreateChat(participant1Id, participant2Id);

        // 채팅 메시지를 모델에 추가하여 뷰로 전달
        model.addAttribute("chat", chat);
        model.addAttribute("messages", chatService.getMessages(chat.getChatId()));

        return "chat/chatRoom"; // 채팅방으로 리다이렉트
    }

}
