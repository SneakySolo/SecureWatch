package com.SneakySolo.SecureWatch.Config;

import com.SneakySolo.SecureWatch.Entity.Role;
import com.SneakySolo.SecureWatch.Entity.User;
import com.SneakySolo.SecureWatch.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminCreatorConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("Ansh").isEmpty()) {
                User admin = new User();
                admin.setRole(Role.ADMIN);
                admin.setEmail("ansh@gmail.com");
                admin.setUsername("Ansh");
                admin.setPassword(passwordEncoder.encode("2285"));

                userRepository.save(admin);
            }
        };
    }
}
