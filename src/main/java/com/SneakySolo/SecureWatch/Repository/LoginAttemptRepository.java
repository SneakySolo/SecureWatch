package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt,Long> {
    
}
