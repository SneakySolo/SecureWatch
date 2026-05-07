package com.SneakySolo.SecureWatch.Controller;

import com.SneakySolo.SecureWatch.Dto.RegisterRequestDTO;
import com.SneakySolo.SecureWatch.Service.UserService;
import lombok.RequiredArgsConstructor;
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
}
