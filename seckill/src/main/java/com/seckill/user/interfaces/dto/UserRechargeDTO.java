package com.seckill.user.interfaces.dto;

import lombok.Data;

import java.math.BigDecimal;

// RechargeDTO.java
@Data
public class UserRechargeDTO {
    private Long userId;
    private BigDecimal amount;
}