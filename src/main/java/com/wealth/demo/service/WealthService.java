package com.wealth.demo.service;

import com.wealth.demo.exception.ResourceNotFoundException;
import com.wealth.demo.mapper.WealthMapper;
import com.wealth.demo.model.dto.WealthDTO;
import com.wealth.demo.model.entity.User;
import com.wealth.demo.model.entity.Wealth;
import com.wealth.demo.repository.UserRepository;
import com.wealth.demo.repository.WealthRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WealthService {

    private final WealthRepository wealthRepository;
    private final UserRepository userRepository;
    private final WealthMapper wealthMapper; // 注入 WealthMapper

    private static final Logger logger = LoggerFactory.getLogger(WealthService.class);

    /**
     * 創建收支記錄
     */
    @Transactional
    public WealthDTO createWealth(WealthDTO wealthDTO, User user) {
        logger.info("創建收支記錄請求: {}", wealthDTO);

        // 驗證 type 欄位值
        validateWealthType(wealthDTO.getType());

        Wealth wealth = wealthMapper.toEntity(wealthDTO);
        wealth.setUser(user);

        // 保存實體到資料庫
        wealth = wealthRepository.save(wealth);

        logger.info("成功創建收支記錄: {}", wealth);
        return wealthMapper.toDto(wealth); // 使用 Mapper 轉換為 DTO
    }

    /**
     * 驗證 type 欄位值
     */
    private void validateWealthType(String type) {
        if (!"INCOME".equalsIgnoreCase(type) && !"EXPENSE".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("無效的收支類型: " + type);
        }
    }

    /**
     * 更新收支記錄
     */
    @Transactional
    public WealthDTO updateWealth(Long id, WealthDTO wealthDTO, User user) {
        logger.info("更新收支記錄請求: ID={}, DTO={}", id, wealthDTO);

        Wealth existingWealth = wealthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到對應的收支記錄，ID: " + id));

        if (!existingWealth.getUser().getId().equals(user.getId())) {
            throw new SecurityException("無權更新該記錄");
        }

        existingWealth.setAmount(wealthDTO.getAmount());
        existingWealth.setNote(wealthDTO.getNote());
        existingWealth.setType(wealthDTO.getType());
        wealthRepository.save(existingWealth);

        logger.info("成功更新收支記錄: {}", existingWealth);
        return wealthMapper.toDto(existingWealth); // 使用 Mapper 轉換為 DTO
    }

    /**
     * 根據 ID 獲取收支記錄
     */
    public WealthDTO getWealthById(Long id) {
        logger.info("查詢收支記錄請求: ID={}", id);

        Wealth wealth = wealthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到對應的收支記錄，ID: " + id));

        return wealthMapper.toDto(wealth); // 使用 Mapper 轉換為 DTO
    }

    /**
     * 分頁查詢收支記錄
     */
    public List<WealthDTO> getWealths(int page, int size, User user) {
        logger.info("查詢分頁收支記錄請求: Page={}, Size={}", page, size);

        Page<Wealth> wealthPage = wealthRepository.findByUser(user, PageRequest.of(page, size));
        logger.info("查詢結果總數量: {}", wealthPage.getTotalElements());
        logger.info("當前頁面數據: {}", wealthPage.getContent());
        return wealthPage.stream()
                .map(wealthMapper::toDto) // 使用 Mapper 轉換
                .collect(Collectors.toList());
    }

    // 獲取所有記錄總數
    public long countByUser(User user) {
        return wealthRepository.countByUser(user);
    }

    /**
     * 查詢指定日期範圍內的收支記錄
     */
    public List<WealthDTO> findByDateRange(LocalDate startDate, LocalDate endDate, User user) {
        logger.info("查詢日期範圍收支記錄請求: StartDate={}, EndDate={}", startDate, endDate);

        List<Wealth> wealthList = wealthRepository.findByUserAndTimestampBetween(
                user, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return wealthList.stream()
                .map(wealthMapper::toDto) // 使用 Mapper 轉換
                .collect(Collectors.toList());
    }

    /**
     * 刪除收支記錄
     */
    @Transactional
    public void deleteWealth(Long id, User user) {
        logger.info("刪除收支記錄請求: ID={}", id);

        Wealth wealth = wealthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到對應的收支記錄，ID: " + id));

        if (!wealth.getUser().getId().equals(user.getId())) {
            throw new SecurityException("無權刪除該記錄");
        }

        wealthRepository.delete(wealth);
        logger.info("成功刪除收支記錄: ID={}", id);
    }

    public Map<String, BigDecimal> getFinancialSummary(User user, LocalDate startDate, LocalDate endDate) {
        logger.info("開始計算用戶統計數據，User ID: {}", user.getId());

        BigDecimal totalIncome;
        BigDecimal totalExpense;

        if (startDate != null && endDate != null) {
            totalIncome = wealthRepository.sumByTypeAndUserAndDateRange("INCOME", user, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
            totalExpense = wealthRepository.sumByTypeAndUserAndDateRange("EXPENSE", user, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        } else {
            totalIncome = wealthRepository.sumByTypeAndUser("INCOME", user);
            totalExpense = wealthRepository.sumByTypeAndUser("EXPENSE", user);
        }
        BigDecimal balance = totalIncome.subtract(totalExpense);

        logger.info("統計完成 - 總收入: {}, 總支出: {}, 結餘: {}", totalIncome, totalExpense, balance);

        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("balance", balance);

        return summary;
    }
    public Page<WealthDTO> getWealthsPage(int page, int size, User user,
                                          String search, String sortColumn, String sortDirection) {

        // 創建排序對象
        Sort sort = Sort.unsorted();
        if (sortColumn != null && sortDirection != null) {
            sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ?
                    Sort.Direction.ASC : Sort.Direction.DESC, sortColumn);
        }

        // 創建分頁請求
        Pageable pageable = PageRequest.of(page, size, sort);

        // 根據是否有搜索條件決定使用哪個查詢方法
        Page<Wealth> wealthPage;
        if (StringUtils.hasText(search)) {
            wealthPage = wealthRepository.findByUserAndSearch(user, search, pageable);
        } else {
            wealthPage = wealthRepository.findByUser(user, pageable);
        }

        return wealthPage.map(wealthMapper::toDto);
    }

    /**
     * 根據日期範圍和分頁查詢收支記錄
     */
    public Page<WealthDTO> getWealthsPageWithDateRange(int page, int size, User user,
                                                       String search, String sortColumn, String sortDirection,
                                                       LocalDate startDate, LocalDate endDate) {

        // 創建排序對象
        Sort sort = Sort.unsorted();
        if (sortColumn != null && sortDirection != null) {
            sort = Sort.by(sortDirection.equalsIgnoreCase("asc") ?
                    Sort.Direction.ASC : Sort.Direction.DESC, sortColumn);
        }

        // 創建分頁請求
        Pageable pageable = PageRequest.of(page, size, sort);

        // 根據日期範圍篩選數據
        /*Page<Wealth> wealthPage;
        if (startDate != null && endDate != null) {
            wealthPage = wealthRepository.findByUserAndTimestampBetween(
                    user,
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59),
                    pageable
            );
        } else {
            wealthPage = wealthRepository.findByUser(user, pageable);
        }*/

        // 根據是否有 search 和日期範圍條件篩選數據
        Page<Wealth> wealthPage;
        if (StringUtils.hasText(search)) {
            // 處理關鍵字搜尋
            wealthPage = wealthRepository.findByUserAndSearch(user, search, pageable);
        } else if (startDate != null && endDate != null) {
            // 處理日期篩選
            wealthPage = wealthRepository.findByUserAndTimestampBetween(
                    user,
                    startDate.atStartOfDay(),
                    endDate.atTime(23, 59, 59),
                    pageable
            );
        } else {
            // 沒有篩選條件，返回全部數據
            wealthPage = wealthRepository.findByUser(user, pageable);
        }

        return wealthPage.map(wealthMapper::toDto);
    }
}
