package com.SneakySolo.SecureWatch.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank @Size(min=3, max=30)
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min=4)
    private String password;
}
