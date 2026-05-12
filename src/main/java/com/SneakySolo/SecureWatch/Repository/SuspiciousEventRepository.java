package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.SuspiciousEvent;
import com.SneakySolo.SecureWatch.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SuspiciousEventRepository extends JpaRepository<SuspiciousEvent,Long> {
    List<SuspiciousEvent> findAllByOrderByTimestampDesc();
    List<SuspiciousEvent> findByTimestampAfter(LocalDateTime since);
    List<SuspiciousEvent> findByUserOrderByTimestampDesc(User user);
    Optional<SuspiciousEvent> findTopByUserOrderByTimestampDesc(User user);
    int countByUser(User user);
}
