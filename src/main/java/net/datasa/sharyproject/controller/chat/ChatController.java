package net.datasa.sharyproject.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.chat.ChatDTO;
import net.datasa.sharyproject.domain.dto.chat.ChatMessageDTO;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.service.chat.ChatService;
import net.datasa.sharyproject.service.follow.FollowService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/room")
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
    }


    @GetMapping("/chatForm")
    public String showChatForm(Model model) {
        String currentUserId = followService.getCurrentUserId();
        List<FollowDTO> following = followService.getFollowingForCurrentUser();
        List<FollowDTO> followers = followService.getFollowersForCurrentUser(); // 추가: 나를 팔로우하는 사용자 목록 가져오기
        model.addAttribute("following", following);
        model.addAttribute("followers", followers); // 추가: 팔로워 리스트 추가
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


}
