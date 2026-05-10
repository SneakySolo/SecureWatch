package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.SuspiciousEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SuspiciousEventRepository extends JpaRepository<SuspiciousEvent,Long> {
    List<SuspiciousEvent> findAllByOrderByTimestampDesc();
    List<SuspiciousEvent> findByTimestampAfter(LocalDateTime since);
}
