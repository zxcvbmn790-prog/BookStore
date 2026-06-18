package WebBookStore.common;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import WebBookStore.chat.ChatMessageVO;
import WebBookStore.chat.ChatService;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // roomId -> (sessionId -> session)
    private static final Map<String, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Autowired
    private ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String loginUser = (String) session.getAttributes().get("loginUser");
        String roomId = getRoomId(session);

        if (loginUser == null || roomId == null || roomId.trim().isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("invalid session"));
            return;
        }

        boolean isAdmin = "admin".equals(loginUser);
        if (!isAdmin && !loginUser.equals(roomId)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("forbidden room"));
            return;
        }

        rooms.computeIfAbsent(roomId, key -> new ConcurrentHashMap<>())
             .put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String loginUser = (String) session.getAttributes().get("loginUser");
        String roomId = getRoomId(session);

        if (loginUser == null || roomId == null) {
            return;
        }

        ChatMessageVO request = objectMapper.readValue(message.getPayload(), ChatMessageVO.class);
        String content = request.getContent();

        if (content == null || content.trim().isEmpty()) {
            return;
        }

        boolean isAdmin = "admin".equals(loginUser);
        String sentAt = LocalDateTime.now().format(formatter);

        ChatMessageVO saved = new ChatMessageVO();
        saved.setRoomId(roomId);
        saved.setSender(loginUser);
        saved.setContent(content.trim());
        saved.setAdmin(isAdmin);
        saved.setSentAt(sentAt);

        chatService.saveMessage(saved);

        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions == null) {
            return;
        }

        TextMessage broadcast = new TextMessage(objectMapper.writeValueAsString(saved));
        for (WebSocketSession client : roomSessions.values()) {
            if (client.isOpen()) {
                synchronized (client) {
                    client.sendMessage(broadcast);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomId(session);
        if (roomId == null) {
            return;
        }

        Map<String, WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            roomSessions.remove(session.getId());
            if (roomSessions.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    private String getRoomId(WebSocketSession session) {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            return null;
        }

        String query = session.getUri().getQuery();

        for (String token : query.split("&")) {
            String[] pair = token.split("=", 2);

            if (pair.length == 2 && "roomId".equals(pair[0])) {
                try {
                    return URLDecoder.decode(pair[1], "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    return pair[1];
                }
            }
        }
        return null;
    }
}