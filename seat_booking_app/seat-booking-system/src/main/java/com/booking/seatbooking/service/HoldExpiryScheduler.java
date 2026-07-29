package com.booking.seatbooking.service;

import com.booking.seatbooking.entity.Seat;
import com.booking.seatbooking.entity.SeatStatus;
import com.booking.seatbooking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Sweeps seats stuck in HELD past their TTL back to AVAILABLE.
 *
 * This is the MySQL-only v1 of the hold mechanism. Trade-off worth calling
 * out in interviews: this runs on a fixed interval (poll-based), so a stale
 * hold can sit for up to `fixedDelay` past its actual expiry before it's
 * cleaned up - fine for a 5-min hold window, not fine if you need sub-second
 * precision. The v2 upgrade path is Redis: SET key EX 300 with a keyspace
 * notification on expiry gives you near-instant release instead of polling.
 * That upgrade story (and *why* you'd make it) is exactly the kind of thing
 * worth writing up in your README.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HoldExpiryScheduler {

    private final SeatRepository seatRepository;
    private final SeatBroadcastService broadcastService;

    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void releaseExpiredHolds() {
        List<Seat> expired = seatRepository.findByStatusAndHoldExpiresAtBefore(
                SeatStatus.HELD, LocalDateTime.now());

        for (Seat seat : expired) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHeldBy(null);
            seat.setHoldExpiresAt(null);
            seatRepository.save(seat);
            broadcastService.broadcastSeatUpdate(seat);
        }

        if (!expired.isEmpty()) {
            log.info("Released {} expired seat hold(s)", expired.size());
        }
    }
}
