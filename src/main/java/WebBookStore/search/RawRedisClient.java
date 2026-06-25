package WebBookStore.search;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RawRedisClient {

    private final String host = System.getProperty("redis.host", "127.0.0.1");
    private final int port = Integer.parseInt(System.getProperty("redis.port", "6379"));
    private final int timeout = Integer.parseInt(System.getProperty("redis.timeout", "300"));

    public String get(String key) {
        Object reply = command("GET", key);
        return reply instanceof String ? (String) reply : null;
    }

    public void setex(String key, int seconds, String value) {
        command("SETEX", key, String.valueOf(seconds), value == null ? "" : value);
    }

    public void zincrby(String key, double score, String member) {
        command("ZINCRBY", key, String.valueOf(score), member);
    }

    @SuppressWarnings("unchecked")
    public List<String> zrevrange(String key, int start, int stop) {
        Object reply = command("ZREVRANGE", key, String.valueOf(start), String.valueOf(stop));
        if (reply instanceof List) return (List<String>) reply;
        return Collections.emptyList();
    }

    private Object command(String... args) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.setSoTimeout(timeout);

            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

            writeCommand(out, args);
            out.flush();
            return readReply(in);
        } catch (Exception e) {
            return null;
        }
    }

    private void writeCommand(BufferedOutputStream out, String... args) throws Exception {
        out.write(("*" + args.length + "\r\n").getBytes(StandardCharsets.UTF_8));
        for (String arg : args) {
            byte[] bytes = (arg == null ? "" : arg).getBytes(StandardCharsets.UTF_8);
            out.write(("$" + bytes.length + "\r\n").getBytes(StandardCharsets.UTF_8));
            out.write(bytes);
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));
        }
    }

    private Object readReply(BufferedInputStream in) throws Exception {
        int type = in.read();
        if (type == -1) return null;

        String line = readLine(in);

        if (type == '+') return line;
        if (type == '-') return null;
        if (type == ':') return Long.parseLong(line);
        if (type == '$') {
            int length = Integer.parseInt(line);
            if (length < 0) return null;

            byte[] bytes = new byte[length];
            int read = 0;
            while (read < length) {
                int n = in.read(bytes, read, length - read);
                if (n == -1) return null;
                read += n;
            }
            in.read();
            in.read();
            return new String(bytes, StandardCharsets.UTF_8);
        }
        if (type == '*') {
            int count = Integer.parseInt(line);
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < count; i++) {
                Object item = readReply(in);
                if (item != null) list.add(String.valueOf(item));
            }
            return list;
        }
        return null;
    }

    private String readLine(BufferedInputStream in) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int prev = -1;
        int cur;
        while ((cur = in.read()) != -1) {
            if (prev == '\r' && cur == '\n') {
                byte[] arr = baos.toByteArray();
                return new String(arr, 0, arr.length - 1, StandardCharsets.UTF_8);
            }
            baos.write(cur);
            prev = cur;
        }
        return "";
    }
}
