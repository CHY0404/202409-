package com.wealth.demo;

import com.wealth.demo.controller.WealthStatsController;
import com.wealth.demo.service.WealthStatsService;
import com.wealth.demo.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WealthStatsTest2 {

    private WealthStatsController wealthStatsController;
    private WealthStatsService wealthStatsService;
    private SessionUtils sessionUtils;
    private HttpSession session;
    private Model model;
    private final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // 初始化所有需要的 mock 對象
        wealthStatsService = mock(WealthStatsService.class);
        sessionUtils = mock(SessionUtils.class);
        session = mock(HttpSession.class);
        model = mock(Model.class);

        // 創建控制器實例
        wealthStatsController = new WealthStatsController(wealthStatsService, sessionUtils);

        // 設置基本的 mock 行為
        when(sessionUtils.getAuthenticatedUserId(any(HttpSession.class)))
                .thenReturn(Optional.of(TEST_USER_ID));
    }

    @Test
    @DisplayName("圖表數據測試")
    void getChartData_ShouldReturnCorrectData() {
        // 準備測試數據
        String period = "month";
        String startDate = "2024-01-01";
        String endDate = "2024-02-28";

        // 創建預期的返回數據
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("labels", Arrays.asList("1月", "2月", "3月"));
        expectedData.put("incomeData", Arrays.asList(3000, 4000, 3000));
        expectedData.put("expenseData", Arrays.asList(2500, 3000, 2500));
        expectedData.put("totalIncome", 10000);
        expectedData.put("totalExpense", 8000);
        expectedData.put("balance", 2000);

        // 設置 mock 行為
        when(wealthStatsService.prepareChartData(
                eq(TEST_USER_ID),
                eq(period),
                eq(LocalDate.parse(startDate)),
                eq(LocalDate.parse(endDate))))
                .thenReturn(expectedData);

        // 執行測試
        Map<String, Object> result = wealthStatsController.getChartData(
                period, startDate, endDate, session);

        // 輸出測試結果
        System.out.println("\n=== 圖表數據測試結果 ===");
        System.out.println("期望的數據: " + expectedData);
        System.out.println("實際的數據: " + result);

        // 驗證結果
        assertNotNull(result, "返回的數據不應為空");
        assertEquals(expectedData.get("totalIncome"), result.get("totalIncome"), "總收入應該相符");
        assertEquals(expectedData.get("totalExpense"), result.get("totalExpense"), "總支出應該相符");
        assertEquals(expectedData.get("balance"), result.get("balance"), "結餘應該相符");

        // 驗證圖表數據陣列
        assertEquals(expectedData.get("labels"), result.get("labels"), "時間標籤應該相符");
        assertEquals(expectedData.get("incomeData"), result.get("incomeData"), "收入數據應該相符");
        assertEquals(expectedData.get("expenseData"), result.get("expenseData"), "支出數據應該相符");
    }
}