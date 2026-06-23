package WebBookStore.chat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminChat(HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (!"admin".equals(loginUser)) {
            return "redirect:/book/list";
        }

        List<String> roomIds = chatService.getAllRoomIds();
        Map<String, ChatMessageVO> lastMessageMap = new LinkedHashMap<>();
        for (String roomId : roomIds) {
            lastMessageMap.put(roomId, chatService.getLastMessage(roomId));
        }

        model.addAttribute("roomIds", roomIds);
        model.addAttribute("lastMessageMap", lastMessageMap);
        model.addAttribute("contentPage", "/WEB-INF/views/chat/admin_chat.jsp");
        return "layout/layout";
    }

    @RequestMapping(value = "/admin/room", method = RequestMethod.GET)
    public String adminRoom(@RequestParam("roomId") String roomId, HttpSession session, Model model) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (!"admin".equals(loginUser)) {
            return "redirect:/book/list";
        }

        model.addAttribute("roomId", roomId);
        model.addAttribute("messages", chatService.getMessages(roomId));
        model.addAttribute("contentPage", "/WEB-INF/views/chat/admin_room.jsp");
        return "layout/layout";
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @ResponseBody
    public List<ChatMessageVO> history(@RequestParam("roomId") String roomId, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return java.util.Collections.emptyList();
        }
        if (!"admin".equals(loginUser) && !loginUser.equals(roomId)) {
            return java.util.Collections.emptyList();
        }
        return chatService.getMessages(roomId);
    }
}