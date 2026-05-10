package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt,Long> {
    int countByUsernameAndSuccessAndTimestampAfter(String username, boolean success, LocalDateTime since);
    boolean existsByUsernameAndIpAddressAndSuccess(String username, String ipAddress, boolean success);
}
