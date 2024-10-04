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
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final FollowService followService;
    private final MemberService memberService;
    private final ProfileService profileService;

    @GetMapping
    public String showChatHome(Model model) {
        String currentUserId = followService.getCurrentUserId();
        List<ChatDTO> chats = chatService.getAllChatsForUser(currentUserId);
        model.addAttribute("chats", chats);
        return "chat/chatHome";
    }

/*  @GetMapping("/room")
    public String getChatRoom(@RequestParam("participant1Id") String participant1Id,
                              @RequestParam("participant2Id") String participant2Id, Model model) {
        ChatDTO chat = chatService.findOrCreateChat(participant1Id, participant2Id);
        List<ChatMessageDTO> messages = chatService.getMessages(chat.getChatId());
        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);
        return "chat/chatRoom";
    }*/

/*    @GetMapping("/room")
    public String getChatRoom(@RequestParam("participant1Id") String participant1Id,
                              @RequestParam("participant2Id") String participant2Id, Model model) {
        // 두 사용자의 관계에 해당하는 채팅방을 찾거나 생성
        ChatDTO chat = chatService.findOrCreateChat(participant1Id, participant2Id);

        // 해당 채팅방의 메시지들을 가져오기
        List<ChatMessageDTO> messages = chatService.getMessages(chat.getChatId());

        // 모델에 채팅방과 메시지 추가
        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);

        return "chat/chatRoom";
    }*/

    //
    @GetMapping("/room")
    public String chatRoom(
            @RequestParam(value = "chatId", required = false) Integer chatId,
            @RequestParam(value = "participant1Id", required = false) String participant1Id,
            @RequestParam(value = "participant2Id", required = false) String participant2Id,
            Model model) {

        ChatDTO chat;

        if (chatId != null) {
            // chatId로 채팅방 조회
            chat = chatService.getChatById(chatId);
        } else if (participant1Id != null && participant2Id != null) {
            // participantId로 채팅방 조회 또는 생성
            chat = chatService.findOrCreateChat(participant1Id, participant2Id);
        } else {
            throw new IllegalArgumentException("채팅방을 찾을 수 없습니다.");
        }

        List<ChatMessageDTO> messages = chatService.getMessages(chat.getChatId());

        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);

        return "chat/chatRoom";
    }


    @GetMapping("/chatForm")
    public String showChatForm(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        String currentUserId = followService.getCurrentUserId();
        List<FollowDTO> following = followService.getFollowingForCurrentUser();
        List<FollowDTO> followers = followService.getFollowersForCurrentUser(); // 추가: 나를 팔로우하는 사용자 목록 가져오기
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

        model.addAttribute("profile", profile);
        return "chat/chatForm";
    }

    /*@PostMapping("/send")
    public String sendMessage(@RequestParam("chatId") int chatId,
                              @RequestParam("messageContent") String content, Model model) {
        String currentUserId = followService.getCurrentUserId();
        chatService.sendMessage(chatId, currentUserId, content);

        // 메시지 전송 후 해당 채팅방으로 다시 이동
        ChatDTO chat = chatService.getChatById(chatId);
        return "redirect:/chat/room?participant1Id=" + chat.getParticipant1Id() + "&participant2Id=" + chat.getParticipant2Id();
    }*/

    @PostMapping("/send")
    @ResponseBody
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestParam("chatId") int chatId,
                                                      @RequestParam("messageContent") String content) {
        String currentUserId = followService.getCurrentUserId();
        ChatMessageDTO messageDTO = chatService.sendMessage(chatId, currentUserId, content);

        // 메시지 DTO 반환
        return ResponseEntity.ok(messageDTO);
    }

    @GetMapping("/chat/start")
    public String startChat(@RequestParam("participant1Id") String participant1Id,
                            @RequestParam("participant2Id") String participant2Id,
                            Model model) {
        // 채팅방 생성 또는 기존 채팅방 가져오기
        ChatDTO chat = chatService.findOrCreateChat(participant1Id, participant2Id);

        // 채팅 메시지 가져오기
        model.addAttribute("chat", chat);
        model.addAttribute("messages", chatService.getMessages(chat.getChatId()));

        return "chat/chatRoom"; // 채팅방으로 리다이렉트
    }


}
