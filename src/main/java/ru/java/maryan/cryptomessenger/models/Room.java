package ru.java.maryan.cryptomessenger.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_users_pair",
                        columnNames = {"user_first_id", "user_second_id"}
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_first_id", nullable = false)
    private User userFirst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_second_id", nullable = false)
    private User userSecond;

    @Column(name = "crypto_algorithm", nullable = false, length = 20)
    private String cryptoAlgorithm;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    private void validateUsers() {
        if (userFirst != null && userFirst.equals(userSecond)) {
            throw new IllegalArgumentException("UserFirst and UserSecond must be different");
        }
    }
}
