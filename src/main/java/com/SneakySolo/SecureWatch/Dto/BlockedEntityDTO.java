package com.SneakySolo.SecureWatch.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockedEntityDTO {

    private Long id;
    private String entityType;
    private String entityValue;
    private String reason;
    private String blockedBy;
    private LocalDateTime blockedAt;
}
