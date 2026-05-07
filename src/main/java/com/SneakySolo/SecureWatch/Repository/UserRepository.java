package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
