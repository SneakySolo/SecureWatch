package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.SuspiciousEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuspiciousEventRepository extends JpaRepository<SuspiciousEvent,Long> {
}
