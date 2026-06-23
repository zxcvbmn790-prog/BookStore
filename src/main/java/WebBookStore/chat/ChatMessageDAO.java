package WebBookStore.chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChatMessageDAO {

    @Autowired
    private DataSource ds;

    @PostConstruct
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS chat_message ("
                + "message_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "room_id VARCHAR(100) NOT NULL, "
                + "sender VARCHAR(100) NOT NULL, "
                + "content VARCHAR(2000) NOT NULL, "
                + "sent_at VARCHAR(30) NOT NULL, "
                + "is_admin BOOLEAN DEFAULT FALSE"
                + ")";

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(ChatMessageVO msg) {
        String sql = "INSERT INTO chat_message (room_id, sender, content, sent_at, is_admin) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, msg.getRoomId());
            ps.setString(2, msg.getSender());
            ps.setString(3, msg.getContent());
            ps.setString(4, msg.getSentAt());
            ps.setBoolean(5, msg.isAdmin());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ChatMessageVO> findByRoomId(String roomId) {
        List<ChatMessageVO> list = new ArrayList<>();
        String sql = "SELECT * FROM chat_message WHERE room_id = ? ORDER BY message_id ASC LIMIT 100";

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(toVO(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> findAllRoomIds() {
        List<String> roomIds = new ArrayList<>();
        String sql = "SELECT room_id FROM chat_message GROUP BY room_id ORDER BY MAX(message_id) DESC";

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roomIds.add(rs.getString("room_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomIds;
    }

    public ChatMessageVO findLastMessage(String roomId) {
        String sql = "SELECT * FROM chat_message WHERE room_id = ? ORDER BY message_id DESC LIMIT 1";

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return toVO(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ChatMessageVO toVO(ResultSet rs) throws Exception {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setMessageId(rs.getInt("message_id"));
        vo.setRoomId(rs.getString("room_id"));
        vo.setSender(rs.getString("sender"));
        vo.setContent(rs.getString("content"));
        vo.setSentAt(rs.getString("sent_at"));
        vo.setAdmin(rs.getBoolean("is_admin"));
        return vo;
    }
}