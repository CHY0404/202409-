package com.wealth.demo.repository;

import com.wealth.demo.model.entity.User;
import com.wealth.demo.model.entity.Wealth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * WealthRepository 提供對 Wealth 實體的數據庫操作接口。
 */
@Repository
public interface WealthRepository extends JpaRepository<Wealth, Long> {

    /**
     * 查詢特定用戶的所有收支記錄。
     *
     * @param user 用戶實體
     * @return 收支記錄列表
     */
    List<Wealth> findAllByUser(User user);

    /**
     * 查詢指定用戶在特定日期範圍內的收支記錄。
     *
     * @param user          用戶實體
     * @param startDateTime 起始時間
     * @param endDateTime   結束時間
     * @return 收支記錄列表
     */
    List<Wealth> findByUserAndTimestampBetween(User user, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 查詢指定用戶的分頁收支記錄。
     *
     * @param user     用戶實體
     * @param pageable 分頁參數
     * @return 分頁收支記錄
     */
    Page<Wealth> findByUser(User user, Pageable pageable);
    // 帶搜索條件的分頁查詢
    @Query("SELECT w FROM Wealth w WHERE w.user = :user " +
            "AND (CAST(w.amount AS string) LIKE %:search% " +
            "OR LOWER(w.type) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(w.note) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Wealth> findByUserAndSearch(
            @Param("user") User user,
            @Param("search") String search,
            Pageable pageable);

    /**
     * 查詢指定用戶在特定日期範圍內的分頁收支記錄。
     *
     * @param user          用戶實體
     * @param startDateTime 起始時間
     * @param endDateTime   結束時間
     * @param pageable      分頁參數
     * @return 分頁收支記錄
     */
    Page<Wealth> findByUserAndTimestampBetween(User user, LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    /**
     * 計算指定用戶的總收入或總支出。
     *
     * @param type 收支類型（"INCOME" 或 "EXPENSE"）
     * @param user 用戶實體
     * @return 總金額
     */

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Wealth w WHERE w.type = :type AND w.user = :user")
    BigDecimal sumByTypeAndUser(@Param("type") String type, @Param("user") User user);

    Long countByUser(User user);

    @Query("SELECT SUM(w.amount) FROM Wealth w WHERE w.type = :type AND w.user = :user AND w.timestamp BETWEEN :startDate AND :endDate")
    BigDecimal sumByTypeAndUserAndDateRange(@Param("type") String type,
                                            @Param("user") User user,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

}
