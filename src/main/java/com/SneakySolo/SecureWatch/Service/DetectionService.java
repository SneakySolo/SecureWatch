package com.SneakySolo.SecureWatch.Service;

import com.SneakySolo.SecureWatch.Entity.*;
import com.SneakySolo.SecureWatch.Repository.BlockedEntityRepository;
import com.SneakySolo.SecureWatch.Repository.LoginAttemptRepository;
import com.SneakySolo.SecureWatch.Repository.SuspiciousEventRepository;
import com.SneakySolo.SecureWatch.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DetectionService {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final SuspiciousEventRepository suspiciousEventRepository;
    private final BlockedEntityRepository blockedEntityRepository;

    public void saveLoginAttempt(String username, String ipAddress, boolean success) {
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setUsername(username);
        loginAttempt.setIpAddress(ipAddress);
        loginAttempt.setSuccess(success);
        loginAttemptRepository.save(loginAttempt);
    }

    public void checkBruteForce(String username, String ipAddress) {

        int count = loginAttemptRepository
                .countByUsernameAndSuccessAndTimestampAfter(username, false, LocalDateTime.now().minusMinutes(5));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found"));

        if (count >= 5) {
            SuspiciousEvent suspiciousEvent = new SuspiciousEvent();
            suspiciousEvent.setIpAddress(ipAddress);
            suspiciousEvent.setEventType(EventType.BRUTE_FORCE_ATTEMPT);
            suspiciousEvent.setDescription("Multiple failed login attempts");
            suspiciousEvent.setUser(user);
        }
        addRisk(user, count*2);
    }

    public void checkNewIp(String username, String ipAddress) {

        if (loginAttemptRepository
                .existsByUsernameAndIpAddressAndSuccess(username, ipAddress, true)) {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Username not found"));

            SuspiciousEvent suspiciousEvent = new SuspiciousEvent();
            suspiciousEvent.setIpAddress(ipAddress);
            suspiciousEvent.setEventType(EventType.NEW_IP_LOGIN);
            suspiciousEvent.setDescription("New IP detected");
            suspiciousEvent.setUser(user);

            addRisk(user, 3);
        }
    }

    private void addRisk(User user, int points) {
        User currentUser = user;
        int currentPoints = user.getRiskScore();
        currentUser.setRiskScore(currentPoints + points);

        if (currentPoints >= 15) {
            user.setBlocked(true);

            BlockedEntity blockedEntity = new BlockedEntity();
            blockedEntity.setBlockedAt(LocalDateTime.now());
            blockedEntity.setEntityType(EntityType.USER);
            blockedEntity.setEntityValue(currentUser.getUsername());
            blockedEntity.setReason("Auto-blocked: Risk score exceeded threshold (" + currentPoints + ")");
            blockedEntity.setBlockedBy("SYSTEM");
        }
        userRepository.save(currentUser);
    }
}
