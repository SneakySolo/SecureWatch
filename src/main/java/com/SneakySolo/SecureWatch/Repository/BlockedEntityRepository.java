package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.BlockedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedEntityRepository extends JpaRepository<BlockedEntity,Long> {
}
