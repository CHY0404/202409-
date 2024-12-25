package com.wealth.demo.service;

import com.wealth.demo.exception.ResourceNotFoundException;
import com.wealth.demo.model.entity.User;
import com.wealth.demo.model.entity.Wealth;
import com.wealth.demo.repository.WealthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WealthStatsService {

    private final UserService userService;
    private final WealthRepository wealthRepository;

    @Autowired
    public WealthStatsService(UserService userService, WealthRepository wealthRepository) {
        this.userService = userService;
        this.wealthRepository = wealthRepository;
    }

    // 取得總收入
    public Integer getTotalIncome(Long userId) {
        User user = getUserByIdOrThrow(userId);
        return wealthRepository.findAllByUser(user).stream()
                .filter(wealth -> "INCOME".equalsIgnoreCase(wealth.getType()))
                .mapToInt(Wealth::getAmount)
                .sum();
    }

    // 取得總支出
    public Integer getTotalExpense(Long userId) {
        User user = getUserByIdOrThrow(userId);
        return wealthRepository.findAllByUser(user).stream()
                .filter(wealth -> "EXPENSE".equalsIgnoreCase(wealth.getType()))
                .mapToInt(Wealth::getAmount)
                .sum();
    }

    // 計算結餘
    public Integer calculateBalance(Long userId) {
        return getTotalIncome(userId) - getTotalExpense(userId);
    }

    // 近 7 天統計
    public Map<String, Map<String, Integer>> getStatsForLast7Days(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        return getDailyStatsInRange(userId, sevenDaysAgo.toLocalDate(), now.toLocalDate());
    }

    // 近 30 天統計
    public Map<String, Map<String, Integer>> getStatsForLast30Days(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        return getDailyStatsInRange(userId, thirtyDaysAgo.toLocalDate(), now.toLocalDate());
    }

    // 年度按月統計
    public Map<String, Map<String, Integer>> getStatsForYear(Long userId, int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        List<Wealth> wealthList = wealthRepository.findByUserAndTimestampBetween(
                getUserByIdOrThrow(userId),
                startOfYear.atStartOfDay(),
                endOfYear.atTime(23, 59, 59)
        );

        return wealthList.stream()
                .collect(Collectors.groupingBy(
                        wealth -> String.valueOf(wealth.getTimestamp().getMonthValue()),
                        Collectors.groupingBy(
                                wealth -> "INCOME".equalsIgnoreCase(wealth.getType()) ? "income" : "expense",
                                Collectors.summingInt(Wealth::getAmount)
                        )
                ));
    }

    // 自訂範圍每日統計
    public Map<String, Map<String, Integer>> getDailyStatsInRange(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = getUserByIdOrThrow(userId);
        List<Wealth> wealthList = wealthRepository.findByUserAndTimestampBetween(
                user, startDate.atStartOfDay(), endDate.atTime(23, 59, 59)
        );

        return wealthList.stream()
                .collect(Collectors.groupingBy(
                        wealth -> wealth.getTimestamp().toLocalDate().toString(),
                        Collectors.groupingBy(
                                wealth -> "INCOME".equalsIgnoreCase(wealth.getType()) ? "income" : "expense",
                                Collectors.summingInt(Wealth::getAmount)
                        )
                ));
    }

    // 輔助方法：根據使用者ID取得使用者或拋出異常
    private User getUserByIdOrThrow(Long userId) {
        return userService.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到對應的用戶，ID: " + userId));
    }
}
