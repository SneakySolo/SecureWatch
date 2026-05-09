package com.SneakySolo.SecureWatch.Controller;

import com.SneakySolo.SecureWatch.Dto.AuthResponseDTO;
import com.SneakySolo.SecureWatch.Dto.LoginRequestDTO;
import com.SneakySolo.SecureWatch.Dto.RegisterRequestDTO;
import com.SneakySolo.SecureWatch.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/api/auth/register")
    public void register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        userService.register(registerRequestDTO);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        AuthResponseDTO authResponse = userService.login(dto);
        return ResponseEntity.ok(authResponse);
    }
}
