package com.SneakySolo.SecureWatch.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter @Setter
public class BlockedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(nullable = false)
    private String entityValue;

    private String reason;
    private String blockedBy;
    private LocalDateTime blockedAt;

    @PrePersist
    void onCreate() {
        this.blockedAt = LocalDateTime.now();
    }
}
