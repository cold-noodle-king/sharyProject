package net.datasa.sharyproject.controller.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.sharyproject.domain.dto.chat.ChatDTO;
import net.datasa.sharyproject.domain.dto.chat.ChatMessageDTO;
import net.datasa.sharyproject.domain.dto.follow.FollowDTO;
import net.datasa.sharyproject.service.chat.ChatService;
import net.datasa.sharyproject.service.follow.FollowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/room")
    public String getChatRoom(@RequestParam("participant1Id") String participant1Id,
                              @RequestParam("participant2Id") String participant2Id, Model model) {
        ChatDTO chat = chatService.findOrCreateChat(participant1Id, participant2Id);
        List<ChatMessageDTO> messages = chatService.getMessages(chat.getChatId());
        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);
        return "chat/chatRoom";
    }

    @GetMapping("/chatForm")
    public String showChatForm(Model model) {
        String currentUserId = followService.getCurrentUserId();
        List<FollowDTO> following = followService.getFollowingForCurrentUser();
        model.addAttribute("following", following);
        return "chat/chatForm";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam("chatId") int chatId,
                              @RequestParam("messageContent") String content, Model model) {
        String currentUserId = followService.getCurrentUserId();
        chatService.sendMessage(chatId, currentUserId, content);

        // 메시지 전송 후 해당 채팅방으로 다시 이동
        ChatDTO chat = chatService.getChatById(chatId);
        return "redirect:/chat/room?participant1Id=" + chat.getParticipant1Id() + "&participant2Id=" + chat.getParticipant2Id();
    }
}
