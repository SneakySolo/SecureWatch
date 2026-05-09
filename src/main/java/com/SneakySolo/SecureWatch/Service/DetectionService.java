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
            suspiciousEventRepository.save(suspiciousEvent);
        }
        addRisk(user, count*2);
    }

    public void checkNewIp(String username, String ipAddress) {

        if (!loginAttemptRepository
                .existsByUsernameAndIpAddressAndSuccess(username, ipAddress, true)) {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Username not found"));

            SuspiciousEvent suspiciousEvent = new SuspiciousEvent();
            suspiciousEvent.setIpAddress(ipAddress);
            suspiciousEvent.setEventType(EventType.NEW_IP_LOGIN);
            suspiciousEvent.setDescription("New IP detected");
            suspiciousEvent.setUser(user);
            suspiciousEventRepository.save(suspiciousEvent);

            addRisk(user, 3);
        }
    }

    public void checkForRapidRequests(String username, String ipAddress) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found"));

        int count = loginAttemptRepository.countRecentByUsernameOrIp(username, ipAddress, LocalDateTime.now().minusMinutes(1));

        if (count > 30) {
            SuspiciousEvent suspiciousEvent = new SuspiciousEvent();
            suspiciousEvent.setIpAddress(ipAddress);
            suspiciousEvent.setEventType(EventType.RAPID_REQUESTS);
            suspiciousEvent.setDescription("Too many requests in a short span of time");
            suspiciousEvent.setUser(user);
            suspiciousEventRepository.save(suspiciousEvent);

            addRisk(user, 8);
        }
    }

    private void addRisk(User user, int points) {
        int newPoints = user.getRiskScore() + points;
        user.setRiskScore(newPoints);

        if (newPoints >= 15) {
            user.setBlocked(true);

            BlockedEntity blockedEntity = new BlockedEntity();
            blockedEntity.setBlockedAt(LocalDateTime.now());
            blockedEntity.setEntityType(EntityType.USER);
            blockedEntity.setEntityValue(user.getUsername());
            blockedEntity.setReason("Auto-blocked: Risk score exceeded threshold (" + newPoints + ")");
            blockedEntity.setBlockedBy("SYSTEM");
            blockedEntityRepository.save(blockedEntity);
        }
        userRepository.save(user);
    }
}
