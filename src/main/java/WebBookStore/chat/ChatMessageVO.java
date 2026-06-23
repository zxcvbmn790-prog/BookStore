package WebBookStore.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVO {
    private int messageId;
    private String roomId;
    private String sender;
    private String content;
    private String sentAt;
    private boolean isAdmin;
}