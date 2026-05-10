package com.SneakySolo.SecureWatch.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecoyAccessLogDTO {

    private Long id;
    private String username;
    private String ipAddress;
    private String endpoint;
    private LocalDateTime timestamp;
}
