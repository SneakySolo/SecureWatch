package com.SneakySolo.SecureWatch.Service;

import com.SneakySolo.SecureWatch.Dto.AuthResponseDTO;
import com.SneakySolo.SecureWatch.Dto.LoginRequestDTO;
import com.SneakySolo.SecureWatch.Dto.RegisterRequestDTO;
import com.SneakySolo.SecureWatch.Entity.Role;
import com.SneakySolo.SecureWatch.Entity.User;
import com.SneakySolo.SecureWatch.Repository.UserRepository;
import com.SneakySolo.SecureWatch.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final DetectionService detectionService;

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

    public AuthResponseDTO login(LoginRequestDTO dto, String ip) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        } catch (AuthenticationException e) {
            detectionService.saveLoginAttempt(dto.getUsername(), ip, false);
            detectionService.checkBruteForce(dto.getUsername(), ip);
            throw new AuthenticationServiceException(e.getMessage());
        }

        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Invalid username and password");
        }
        detectionService.saveLoginAttempt(dto.getUsername(), ip, true);

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isBlocked()) {
            throw new RuntimeException("User is blocked");
        }

        detectionService.checkNewIp(user.getUsername(), ip);

        String token = jwtUtil.generateToken(dto.getUsername(), user.getRole().name());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        return response;
    }
}
