package com.SneakySolo.SecureWatch.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousEventDTO {

    private Long id;
    private String username;
    private String ipAddress;
    private String eventType;
    private String description;
    private LocalDateTime timestamp;
}
