package com.SneakySolo.SecureWatch.Service;

import com.SneakySolo.SecureWatch.Dto.*;
import com.SneakySolo.SecureWatch.Entity.*;
import com.SneakySolo.SecureWatch.Exception.EntityAlreadyBlockedException;
import com.SneakySolo.SecureWatch.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SuspiciousEventRepository suspiciousEventRepository;
    private final BlockedEntityRepository blockedEntityRepository;
    private final DecoyAccessLogRepository decoyAccessLogRepository;

    public List<SuspiciousEventDTO> getAllSuspiciousEvents() {
        List<SuspiciousEvent> event = suspiciousEventRepository.findAllByOrderByTimestampDesc();
        List<SuspiciousEventDTO> dtos = new ArrayList<>();

        for (SuspiciousEvent ev : event) {
            SuspiciousEventDTO dto = new SuspiciousEventDTO();
            if (ev.getUser() == null) {
                dto.setUsername("Anonymous");
            }
            else {
                dto.setUsername(ev.getUser().getUsername());
            }
            dto.setId(Long.valueOf(ev.getId()));
            dto.setDescription(ev.getDescription());
            dto.setEventType(String.valueOf(ev.getEventType()));
            dto.setIpAddress(ev.getIpAddress());
            dto.setTimestamp(ev.getTimestamp());

            dtos.add(dto);
        }
        return dtos;
    }

    public List<BlockedEntityDTO> getAllBlockedEntities() {
        List<BlockedEntity> blocked =  blockedEntityRepository.findAllByOrderByBlockedAtDesc();
        List<BlockedEntityDTO> dtos = new ArrayList<>();

        for (BlockedEntity entity : blocked) {
            BlockedEntityDTO dto = new BlockedEntityDTO();
            dto.setId(Long.valueOf(entity.getId()));
            dto.setBlockedAt(entity.getBlockedAt());
            dto.setBlockedBy(entity.getBlockedBy());
            dto.setEntityType(String.valueOf(entity.getEntityType()));
            dto.setEntityValue(entity.getEntityValue());
            dto.setReason(entity.getReason());

            dtos.add(dto);
        }
        return dtos;
    }

    public List<DecoyAccessLogDTO> getAllDecoyLogs() {
        List<DecoyAccessLog> accessLogs = decoyAccessLogRepository.findAllByOrderByTimestampDesc();
        List<DecoyAccessLogDTO> dtos = new ArrayList<>();

        for (DecoyAccessLog accessLog : accessLogs) {
            DecoyAccessLogDTO dto = new DecoyAccessLogDTO();
            dto.setId(Long.valueOf(accessLog.getId()));
            dto.setIpAddress(accessLog.getIpAddress());
            dto.setTimestamp(accessLog.getTimestamp());
            dto.setEndpoint(accessLog.getEndpoint());

            if (accessLog.getUser().getUsername() != null) {
                dto.setUsername(accessLog.getUser().getUsername());
            }
            else {
                dto.setUsername("Anonymous");
            }

            dtos.add(dto);
        }
        return dtos;
    }

    public void blockEntity(BlockRequestDTO dto, String adminUsername) {
        EntityType type = EntityType.valueOf(dto.getEntityType());
        if (blockedEntityRepository.existsByEntityTypeAndEntityValue(type, dto.getEntityValue())) {
            throw new EntityAlreadyBlockedException(dto.getEntityValue() + " is already blocked");
        }

        BlockedEntity blockedEntity = new BlockedEntity();

        if (dto.getEntityType().equals("USER")) {
            User user = userRepository.findByUsername(dto.getEntityValue())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            user.setBlocked(true);
            userRepository.save(user);
        }
        blockedEntity.setBlockedBy(adminUsername);
        blockedEntity.setBlockedAt(LocalDateTime.now());
        blockedEntity.setEntityType(EntityType.valueOf(dto.getEntityType()));
        blockedEntity.setReason(dto.getReason());
        blockedEntity.setEntityValue(dto.getEntityValue());
        blockedEntityRepository.save(blockedEntity);
    }

    @Transactional
    public void unblockEntity(Integer id) {
        BlockedEntity entity = blockedEntityRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UsernameNotFoundException("Blocked entity not found"));

        if (entity.getEntityType().toString().equals("USER")) {
            String username = entity.getEntityValue();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            user.setBlocked(false);
            user.setRiskScore(0);
            userRepository.save(user);
        }
        blockedEntityRepository.delete(entity);
    }

    public AdminSummaryDTO getSummary() {
        AdminSummaryDTO dto = new AdminSummaryDTO();
        dto.setTotalEventsToday(suspiciousEventRepository.findByTimestampAfter(LocalDateTime.now().minusDays(1)).size());
        dto.setTotalBlockedEntities((int) blockedEntityRepository.count());

        List<User> list = userRepository.findTop5ByOrderByRiskScoreDesc();
        List<String> userList = new ArrayList<>();

        for (User user : list) {
            String username = user.getUsername();
            userList.add(username);
        }
        dto.setTopRiskyUsers(userList);
        return dto;
    }

    public List<UserSummaryDTO> getAllActiveUsers() {
        List<User> users = userRepository.findByIsBlockedFalseOrderByRiskScoreDesc();
        List<UserSummaryDTO> dtos = new ArrayList<>();

        for (User user : users) {
            if (user.getRole().toString().equals("ADMIN")) {
                continue;
            }

            UserSummaryDTO dto = new UserSummaryDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setRiskScore(user.getRiskScore());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setTotalSuspiciousEvents(suspiciousEventRepository.countByUser(user));

            suspiciousEventRepository.findTopByUserOrderByTimestampDesc(user)
                    .ifPresent(event -> dto.setLastActivity(event.getTimestamp()));

            dtos.add(dto);
        }
        return dtos;
    }

    public List<SuspiciousEventDTO> getUserSuspiciousEvents(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SuspiciousEvent> events = suspiciousEventRepository.findByUserOrderByTimestampDesc(user);
        List<SuspiciousEventDTO> dtos = new ArrayList<>();

        for (SuspiciousEvent ev : events) {
            SuspiciousEventDTO dto = new SuspiciousEventDTO();
            dto.setId(Long.valueOf(ev.getId()));
            dto.setUsername(user.getUsername());
            dto.setIpAddress(ev.getIpAddress());
            dto.setEventType(String.valueOf(ev.getEventType()));
            dto.setDescription(ev.getDescription());
            dto.setTimestamp(ev.getTimestamp());
            dtos.add(dto);
        }
        return dtos;
    }
}
