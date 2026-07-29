package com.booking.seatbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * The row we contend on. Two guarantees matter here:
 *
 * 1. `version` (optimistic lock) - JPA/Hibernate auto-increments this on every
 *    UPDATE. If two threads read the same seat and both try to update it,
 *    the second commit fails with OptimisticLockException because the
 *    version it read is stale. This is our primary double-booking guard.
 *
 * 2. `holdExpiresAt` - lets us implement "soft locks" (temporary holds while
 *    a user is checking out) without needing Redis. A scheduled job sweeps
 *    expired holds back to AVAILABLE. See HoldExpiryScheduler.
 */
@Entity
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"show_id", "seat_number"})
})
@Getter
@Setter
@NoArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;

    /** Whoever currently holds/booked the seat. In a real system this is a userId FK. */
    private String heldBy;

    private LocalDateTime holdExpiresAt;

    @Version
    private Long version;
}
