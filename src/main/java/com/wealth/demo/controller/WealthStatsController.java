package com.wealth.demo.controller;

import com.wealth.demo.exception.ResourceNotFoundException;
import com.wealth.demo.service.WealthStatsService;
import com.wealth.demo.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/stats")
@RequiredArgsConstructor
public class WealthStatsController {

    private static final Logger logger = LoggerFactory.getLogger(WealthStatsController.class);

    private final WealthStatsService wealthStatsService;
    private final SessionUtils sessionUtils; // 注入 SessionUtils

    /**
     * 統計總收入
     */
    @GetMapping("/api/total-income")
    @ResponseBody
    public Integer getTotalIncome(HttpSession session) {
        Long userId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new ResourceNotFoundException("使用者未登入"));
        logger.info("取得的 User ID: {}", userId);
        return wealthStatsService.getTotalIncome(userId);
    }

    /**
     * 統計總支出
     */
    @GetMapping("/api/total-expense")
    @ResponseBody
    public Integer getTotalExpense(HttpSession session) {
        Long userId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new ResourceNotFoundException("使用者未登入"));
        logger.info("取得的 User ID: {}", userId);
        return wealthStatsService.getTotalExpense(userId);
    }

    /**
     * 計算結餘
     */
    @GetMapping("/api/balance")
    @ResponseBody
    public Integer getBalance(HttpSession session) {
        Long userId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new ResourceNotFoundException("使用者未登入"));
        return wealthStatsService.calculateBalance(userId);
    }

    /**
     * 渲染初始頁面
     */
    @GetMapping("/statistics")
    public String getStatistics(HttpSession session) {
        Long UserId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new ResourceNotFoundException("使用者未登入"));
        return "statistics";
    }

    /**
     * 近 7 天統計
     */
    @GetMapping("/api/last-7-days")
    @ResponseBody
    public Map<String, Map<String, Integer>> getStatsForLast7Days(HttpSession session) {
        Long userId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new ResourceNotFoundException("使用者未登入"));
        return wealthStatsService.getStatsForLast7Days(userId);
    }

    /**
     * 近 30 天統計
     */
    @GetMapping("/api/last-30-days")
    @ResponseBody
    public Map<String, Map<String, Integer>> getStatsForLast30Days(HttpSession session) {
        Long userId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new ResourceNotFoundException("使用者未登入"));
        return wealthStatsService.getStatsForLast30Days(userId);
    }

    /**
     * 按年度（每月）統計
     */
    @GetMapping("/api/yearly")
    @ResponseBody
    public Map<String, Map<String, Integer>> getStatsForYear(
            @RequestParam(required = false) Integer year,
            HttpSession session) {
        Long userId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new ResourceNotFoundException("使用者未登入"));
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        return wealthStatsService.getStatsForYear(userId, targetYear);
    }

    /**
     * 自訂範圍的統計
     */
    @GetMapping("/api/custom-range")
    @ResponseBody
    public Map<String, Map<String, Integer>> getStatsForCustomRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpSession session) {
        Long userId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new ResourceNotFoundException("使用者未登入"));
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return wealthStatsService.getDailyStatsInRange(userId, start, end);
    }
}
