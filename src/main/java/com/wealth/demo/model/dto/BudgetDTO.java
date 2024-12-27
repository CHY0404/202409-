package com.wealth.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDTO {

    private Long id;
    private Integer amount;
    private Long userId;
    private Long categoryId;
}