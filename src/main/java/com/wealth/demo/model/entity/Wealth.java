package com.wealth.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Wealth 實體類，用於記錄每筆收支記錄。
 * 每條記錄包含金額、時間戳和備註等信息。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wealth_records")
public class Wealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "ENUM('INCOME', 'EXPENSE')")
    private String type;

    @Column(nullable = false)
    @Min(value = 0, message = "金額不可為負數")
    private Integer amount; // 金額，例如 5000 表示 5000 元

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user; // 所屬用戶

    @Column(nullable = false)
    private LocalDateTime timestamp; // 記錄的時間戳

    @Column(length = 255)
    private String note; // 備註，例如 "這是額外的收入" 或 "購買了辦公用品"

}
