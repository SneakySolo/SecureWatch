package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt,Long> {
    int countByUsernameAndSuccessAndTimestampAfter(String username, boolean success, LocalDateTime since);
    boolean existsByUsernameAndIpAddressAndSuccess(String username, String ipAddress, boolean success);

    @Query("SELECT COUNT(l) FROM LoginAttempt l WHERE (l.username = :username OR l.ipAddress = :ipAddress) AND l.timestamp > :since")
    int countRecentByUsernameOrIp(@Param("username") String username, @Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}
