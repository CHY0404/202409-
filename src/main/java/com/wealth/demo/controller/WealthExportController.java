package com.wealth.demo.controller;

import com.wealth.demo.exception.ResourceNotFoundException;
import com.wealth.demo.model.dto.WealthDTO;
import com.wealth.demo.model.entity.User;
import com.wealth.demo.service.UserService;
import com.wealth.demo.service.WealthService;
import com.wealth.demo.util.ExcelExporter;
import com.wealth.demo.util.SessionUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/wealth")
public class WealthExportController {

    private final WealthService wealthService;
    private final UserService userService;
    private final SessionUtils sessionUtils;

    public WealthExportController(WealthService wealthService, UserService userService, SessionUtils sessionUtils) {
        this.wealthService = wealthService;
        this.userService = userService;
        this.sessionUtils = sessionUtils;
    }

    @GetMapping("/records/export-excel")
    public void exportRecordsToExcel(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletResponse response,
            HttpSession session) throws IOException {

        // 驗證用戶身份
        Long userId = sessionUtils.getAuthenticatedUserId(session)
                .orElseThrow(() -> new RuntimeException("未登入用戶，請先登入"));
        User user = validateAuthenticatedUser(userId);

        // 處理日期範圍
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // 查詢篩選條件內的記錄
        List<WealthDTO> records = wealthService.findByDateRange(
                start, end, user);

        // 使用 ExcelExporter 將數據導出為 Excel
        byte[] excelData = ExcelExporter.exportToExcel(records);

        // 設置響應頭並返回 Excel 文件
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"records.xlsx\"");
        response.getOutputStream().write(excelData);
    }

    private User validateAuthenticatedUser(Long userId) {
        return userService.getUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用戶不存在，ID: " + userId));
    }
}
