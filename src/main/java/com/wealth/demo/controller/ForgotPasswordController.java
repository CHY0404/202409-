package com.wealth.demo.controller;

import com.wealth.demo.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);
    private final PasswordService passwordService;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 渲染初始的忘記密碼頁面。
     */
    @GetMapping
    public String renderForgotPasswordPage() {
        return "forgot-password";
    }

    /**
     * 發送驗證碼郵件至用戶提供的電子郵件地址，並返回 reset-password.jsp 頁面。
     *
     * @param username 用戶名
     * @param email    電子郵件地址
     * @param session  HttpSession 物件
     * @param model    Model 物件用於傳遞數據到 JSP
     * @return 返回 reset-password.jsp 或 forgot-password.jsp 頁面
     */
    @PostMapping("/send-code")
    public String sendVerificationCode(
            @RequestParam String username,
            @RequestParam String email,
            HttpSession session,
            Model model) {

        try {
            // 調用服務層方法來發送驗證碼
            passwordService.sendVerificationCode(username, email, session);

            // 添加成功訊息到模型
            model.addAttribute("success", "驗證碼已成功發送至 " + email + "。請檢查您的郵件收件箱。");

            // 返回 reset-password.jsp 頁面
            return "reset-password";
        } catch (IllegalArgumentException e) {
            // 添加用戶輸入相關的錯誤訊息到模型
            model.addAttribute("error", e.getMessage());
            return "forgot-password";
        } catch (RuntimeException e) {
            // 添加系統錯誤訊息到模型
            logger.error("無法發送驗證郵件", e);
            model.addAttribute("error", "系統無法發送驗證郵件，請稍後再試。");
            return "forgot-password";
        }
    }
}
