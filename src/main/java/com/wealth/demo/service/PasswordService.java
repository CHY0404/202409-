package com.wealth.demo.service;

import com.wealth.demo.exception.*;
import com.wealth.demo.model.entity.User;
import com.wealth.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;

import java.util.Random;

@Service
public class PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    public PasswordService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    /**
     * 發送驗證碼至用戶電子郵件。
     *
     * @param username 用戶名
     * @param email    電子郵件地址
     * @param session  HttpSession 物件
     */
    public void sendVerificationCode(String username, String email, HttpSession session) {
        // 驗證用戶名與電子郵件是否匹配
        User user = validateUsernameAndEmail(username, email);

        // 生成6位數字驗證碼
        String verificationCode = generateVerificationCode();

        // 將驗證碼保存到Session中
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("email", email);
        session.setMaxInactiveInterval(300); // 設置驗證碼有效期5分鐘

        // 創建郵件消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("收支管家 <your-email@gmail.com>"); // 確保使用配置中的發件人地址
        message.setTo("jkl9420@gmail.com");
        message.setSubject("您的驗證碼");
        message.setText("您的驗證碼是：" + verificationCode + "，請在5分鐘內完成驗證。");

        try {
            // 使用 JavaMailSender 發送郵件
            mailSender.send(message);
            logger.info("已成功發送驗證碼至 {}", email);
        } catch (MailException e) {
            logger.error("發送驗證碼失敗: ", e);
            throw new RuntimeException("無法發送驗證碼，請稍後再試。", e);
        }
    }

    /**
     * 重設密碼。
     *
     * @param email            用戶的電子郵件地址
     * @param verificationCode 驗證碼
     * @param newPassword      新密碼
     * @param session          HttpSession 物件
     */
    @Transactional
    public void resetPassword(String email, String verificationCode, String newPassword, HttpSession session) {
        // 獲取 Session 中的驗證碼
        String storedCode = (String) session.getAttribute("verificationCode");
        String storedEmail = (String) session.getAttribute("email");

        if (storedCode == null || !storedCode.equals(verificationCode)) {
            throw new IllegalArgumentException("驗證碼錯誤或已過期");
        }

        if (!storedEmail.equals(email)) {
            throw new IllegalArgumentException("電子郵件不匹配");
        }

        // 獲取用戶並重設密碼
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 清除 Session
        session.removeAttribute("verificationCode");
        session.removeAttribute("email");

        logger.info("密碼已成功重設，用戶: {}", user.getUsername());
    }

    /**
     * 驗證用戶名和電子郵件是否匹配。
     *
     * @param username 用戶名
     * @param email    電子郵件地址
     * @return 匹配的用戶
     */
    private User validateUsernameAndEmail(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email)
                .orElseThrow(() -> new IllegalArgumentException("用戶名與電子郵件不匹配或用戶不存在"));
    }

    /**
     * 生成6位數字驗證碼。
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000到999999之間的隨機數
        return String.valueOf(code);
    }
}