package com.SneakySolo.SecureWatch.Repository;

import com.SneakySolo.SecureWatch.Entity.BlockedEntity;
import com.SneakySolo.SecureWatch.Entity.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockedEntityRepository extends JpaRepository<BlockedEntity,Long> {
    boolean existsByEntityTypeAndEntityValue(EntityType entityType, String entityValue);
    List<BlockedEntity> findAllByOrderByBlockedAtDesc();
    void deleteByEntityTypeAndEntityValue(EntityType entityType, String entityValue);
    BlockedEntity findById(Integer id);
}
