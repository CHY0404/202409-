package com.wealth.demo.controller;

import com.wealth.demo.service.PasswordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class ResetPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);
    private final PasswordService passwordService;

    /**
     * 處理密碼重設請求。
     *
     * @param email            用戶的電子郵件地址
     * @param verificationCode 驗證碼
     * @param newPassword      新密碼
     * @param session          HttpSession 物件
     * @param model            Model 物件用於傳遞數據到 JSP
     * @return 成功時返回登入頁面，失敗時返回錯誤信息
     */
    @PostMapping("/reset")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String verificationCode,
            @RequestParam String newPassword,
            HttpSession session,
            Model model) {

        try {
            // 調用服務層方法來重設密碼
            passwordService.resetPassword(email, verificationCode, newPassword, session);

            // 返回登入頁面並附帶成功消息
            model.addAttribute("message", "密碼已成功重設");
            return "redirect:/";
        } catch (IllegalArgumentException e) {

            logger.error("密碼重設失敗: ", e);
            model.addAttribute("error", e.getMessage());
            return "forgot-password";
        }
    }
}