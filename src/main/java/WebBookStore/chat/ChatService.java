package WebBookStore.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private ChatMessageDAO dao;

    public void saveMessage(ChatMessageVO message) {
        dao.insert(message);
    }

    public List<ChatMessageVO> getMessages(String roomId) {
        return dao.findByRoomId(roomId);
    }

    public List<String> getAllRoomIds() {
        return dao.findAllRoomIds();
    }

    public ChatMessageVO getLastMessage(String roomId) {
        return dao.findLastMessage(roomId);
    }
}