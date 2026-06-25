package WebBookStore.member;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.smtp.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String nickname, String otp) {
        String subject = "[BOOK FOREST] 회원가입 인증번호 안내";

        String body = "<div style='max-width:420px;margin:0 auto;font-family:sans-serif;text-align:center;'>"
                + "<h2 style='color:#6366f1;'>BOOK FOREST</h2>"
                + "<p>안녕하세요"
                + (nickname != null && !nickname.isEmpty() ? ", <b>" + escapeHtml(nickname) + "</b>님" : "")
                + "!</p>"
                + "<p>아래 인증번호를 입력하여 회원가입을 완료해주세요.</p>"
                + "<div style='margin:24px 0;padding:20px;background:#f3f4f6;border-radius:12px;'>"
                + "<span style='font-size:32px;font-weight:bold;letter-spacing:8px;color:#6366f1;'>" + otp + "</span>"
                + "</div>"
                + "<p style='color:#ef4444;font-size:13px;'>이 인증번호는 <b>3분</b> 동안 유효합니다.</p>"
                + "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

            System.out.println("[EmailService] OTP 발송 완료: " + toEmail);
        } catch (Exception e) {
            System.out.println("[EmailService] OTP 발송 실패: " + toEmail);
            e.printStackTrace();
            throw new RuntimeException("이메일 발송에 실패했습니다. Gmail 앱 비밀번호 또는 SMTP 설정을 확인해주세요.");
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}