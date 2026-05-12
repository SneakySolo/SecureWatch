package com.SneakySolo.SecureWatch.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private Integer id;
    private String username;
    private String email;
    private Integer riskScore;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private Integer totalSuspiciousEvents;
}
