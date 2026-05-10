package com.SneakySolo.SecureWatch.Service;

import com.SneakySolo.SecureWatch.Entity.*;
import com.SneakySolo.SecureWatch.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DetectionService {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final SuspiciousEventRepository suspiciousEventRepository;
    private final BlockedEntityRepository blockedEntityRepository;
    private final DecoyAccessLogRepository decoyAccessLogRepository;

    public void saveLoginAttempt(String username, String ipAddress, boolean success) {
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setUsername(username);
        loginAttempt.setIpAddress(ipAddress);
        loginAttempt.setSuccess(success);
        loginAttemptRepository.save(loginAttempt);
    }

    public void registerDecoyAccess(String username, String ipAddress, String endpoint) {

        User user = null;
        if (!username.equals("Anonymous")) {
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Username not found"));
        }

        DecoyAccessLog decoyAccessLog = new DecoyAccessLog();
        decoyAccessLog.setEndpoint(endpoint);
        decoyAccessLog.setIpAddress(ipAddress);
        decoyAccessLog.setUser(user);
        decoyAccessLogRepository.save(decoyAccessLog);

        honeyPotTriggered(user, ipAddress);
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
            addRisk(user, count*2);
        }
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

    private final Map<String, List<LocalDateTime>> requestTimestamps = new ConcurrentHashMap<>();

    public void checkForRapidRequests(String username, String ipAddress) {
        String requestFrom = username + " : " + ipAddress;

        List<LocalDateTime> timestamps = requestTimestamps.get(requestFrom);
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        timestamps.removeIf(t -> t.isBefore(oneMinuteAgo));

        if (timestamps.isEmpty()) { // remove entry entirely if user is no more active
            requestTimestamps.remove(requestFrom);
        }

        List<LocalDateTime> active = requestTimestamps.computeIfAbsent(requestFrom, k -> new ArrayList<>());
        active.add(LocalDateTime.now());

        if (timestamps.size() == 30) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Username not found"));

            SuspiciousEvent suspiciousEvent = new SuspiciousEvent();
            suspiciousEvent.setIpAddress(ipAddress);
            suspiciousEvent.setEventType(EventType.RAPID_REQUESTS);
            suspiciousEvent.setDescription("Too many requests in a short span of time");
            suspiciousEvent.setUser(user);
            suspiciousEventRepository.save(suspiciousEvent);

            addRisk(user, 8);
        }
    }

    public void honeyPotTriggered(User user, String ipAddress) {

        SuspiciousEvent suspiciousEvent = new SuspiciousEvent();
        suspiciousEvent.setIpAddress(ipAddress);
        suspiciousEvent.setEventType(EventType.HONEYPOT_TRIGGERED);
        suspiciousEvent.setDescription("Honeypot triggered");
        suspiciousEvent.setUser(user);
        suspiciousEventRepository.save(suspiciousEvent);

        addRisk(user, 15);
    }

    private void addRisk(User user, int points) {
        int newPoints = user.getRiskScore() + points;
        user.setRiskScore(newPoints);

        if (newPoints >= 15 && !user.isBlocked()) {
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
