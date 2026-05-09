package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.BlockedEntity;
import com.SneakySolo.SecureWatch.Entity.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedEntityRepository extends JpaRepository<BlockedEntity,Long> {
    boolean existsByEntityTypeAndEntityValue(EntityType entityType, String entityValue);
}
