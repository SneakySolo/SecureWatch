package com.SneakySolo.SecureWatch.Service;

import com.SneakySolo.SecureWatch.Dto.AuthResponseDTO;
import com.SneakySolo.SecureWatch.Dto.LoginRequestDTO;
import com.SneakySolo.SecureWatch.Dto.RegisterRequestDTO;
import com.SneakySolo.SecureWatch.Entity.Role;
import com.SneakySolo.SecureWatch.Entity.User;
import com.SneakySolo.SecureWatch.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        user.setUsername(dto.getUsername());
        userRepository.save(user);
    }

    // login via authManager thing
}
