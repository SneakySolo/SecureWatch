package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.DecoyAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecoyAccessLogRepository extends JpaRepository<DecoyAccessLog,Long> {
}
