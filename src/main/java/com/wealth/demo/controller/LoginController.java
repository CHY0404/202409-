package com.wealth.demo.controller;

import com.wealth.demo.exception.PasswordMismatchException;
import com.wealth.demo.exception.UsernameNotFoundException;
import com.wealth.demo.model.dto.UserLoginDTO;
import com.wealth.demo.model.entity.User;
import com.wealth.demo.service.UserService;
import com.wealth.demo.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class LoginController {

    private final UserService userService;
    private final SessionUtils sessionUtils;

    /**
     * 處理用戶登錄請求。
     *
     * @param userLoginDTO 用戶提交的登錄數據
     * @param session      HttpSession 用於管理用戶會話
     * @param model        用於返回的 Model
     * @return 成功返回後台頁面，失敗返回登錄頁面
     */
    @PostMapping("/login")
    public String login(
            @ModelAttribute("userLoginDTO") UserLoginDTO userLoginDTO,
            HttpSession session,
            Model model) {

        try {
            // Step 1: 驗證 CAPTCHA
            String sessionCaptcha = (String) session.getAttribute("captcha");
            String inputCaptcha = userLoginDTO.getCaptcha();

            if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(inputCaptcha)) {
                return handleLoginError("login", "captcha", "驗證碼錯誤，請在試一次!", model);
            }

            // Step 2: 認證用戶
            User authenticatedUser = userService.validateUser(
                    userLoginDTO.getUsername(),
                    userLoginDTO.getPassword()
            );

            // Step 3: 將認證用戶存入 Session
            sessionUtils.setAuthenticatedUser(session, authenticatedUser);

            return "redirect:/income-expense"; // 登錄成功，跳轉到收支管理頁面

        } catch (UsernameNotFoundException e) {
            return handleLoginError("login", "username", e.getMessage(), model);
        } catch (PasswordMismatchException e) {
            return handleLoginError("login", "password", e.getMessage(), model);
        }
    }

    @GetMapping("/charts")
    public String charts() {
        return "redirect:/charts";
    }

    /**
     * 驗證用戶是否已登錄。
     *
     * @param session HttpSession 用於檢查當前用戶的狀態
     * @param model   用於傳遞數據到 JSP 頁面
     * @return 登錄成功返回後台頁面，否則跳轉到登錄頁面
     */
    @GetMapping("/income-expense")
    public String validateSession(HttpSession session, Model model) {
        // 使用 Optional 獲取用戶名，避免硬編碼
        String username = sessionUtils.getAuthenticatedUsername(session)
                .orElse(null);

        if (username == null) {
            return "redirect:/"; // 未登錄，跳轉到登錄頁面
        }

        model.addAttribute("username", username);
        return "income-expense"; // 返回收支管理頁面
    }

    /**
     * 用戶登出並清除會話。
     *
     * @param session HttpSession 用於移除當前用戶的會話信息
     * @return 登出後跳轉到登錄頁面
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        sessionUtils.clearSession(session); // 清除 Session
        return "redirect:/"; // 返回登錄頁面
    }

    /**
     * 處理登錄失敗的異常邏輯。
     *
     * @param errorSource  錯誤來源（如 login）
     * @param error        錯誤類型（如 username/password）
     * @param errorMessage 錯誤信息
     * @param model        用於返回的 Model
     * @return 返回登錄頁面並顯示錯誤
     */
    private String handleLoginError(String errorSource, String error, String errorMessage, Model model) {
        model.addAttribute("errorSource", errorSource);
        model.addAttribute("error", error);
        model.addAttribute("errorMessage", errorMessage);
        return "index"; // 返回登錄頁面
    }
}
