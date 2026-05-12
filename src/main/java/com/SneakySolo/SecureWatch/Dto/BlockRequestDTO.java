package com.SneakySolo.SecureWatch.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestDTO {

    @NotBlank @Pattern(regexp = "USER|IP")
    private String entityType;

    @NotBlank
    private String entityValue;

    @NotBlank
    private String reason;
}
