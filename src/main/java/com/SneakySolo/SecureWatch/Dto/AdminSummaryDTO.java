package com.SneakySolo.SecureWatch.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSummaryDTO {

    private int totalEventsToday;
    private int totalBlockedEntities;
    private List<String> topRiskyUsers;
}
