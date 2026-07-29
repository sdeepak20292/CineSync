package com.booking.seatbooking.repository;

import com.booking.seatbooking.entity.Seat;
import com.booking.seatbooking.entity.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShowId(Long showId);

    /**
     * Pessimistic version: takes a row-level lock (SELECT ... FOR UPDATE) so
     * concurrent transactions physically queue up on this row instead of
     * racing and failing optimistically. Swap to this if you want "block and
     * wait" semantics instead of "fail fast and retry" semantics.
     *
     * Trade-off: higher contention = threads block on the DB connection,
     * which is why HikariCP pool sizing matters here (you've already tuned
     * this once for the batch job - same idea applies).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id = :id")
    Optional<Seat> findByIdForUpdate(@Param("id") Long id);

    List<Seat> findByStatusAndHoldExpiresAtBefore(SeatStatus status, LocalDateTime cutoff);
}
