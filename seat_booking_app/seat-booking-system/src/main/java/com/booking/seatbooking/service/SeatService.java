package com.booking.seatbooking.service;

import com.booking.seatbooking.dto.BookingRepository;
import com.booking.seatbooking.entity.Booking;
import com.booking.seatbooking.entity.Seat;
import com.booking.seatbooking.entity.SeatStatus;
import com.booking.seatbooking.exception.SeatUnavailableException;
import com.booking.seatbooking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The whole project's thesis lives in this class: make sure two people can
 * never end up holding/booking the same seat, even when requests arrive at
 * the exact same millisecond.
 *
 * Strategy used here: OPTIMISTIC locking via the @Version column on Seat.
 * - Two threads read seat v3 (status=AVAILABLE).
 * - Both try to update to HELD.
 * - First commit wins, seat is now v4.
 * - Second commit's UPDATE ... WHERE id=? AND version=3 matches zero rows,
 *   Hibernate throws ObjectOptimisticLockingFailureException.
 * - GlobalExceptionHandler turns that into a clean 409 for the loser.
 *
 * This is "fail fast" - good for a low-contention, high-read scenario like
 * seat browsing where most seats aren't actually contested. If you want
 * "block and wait" semantics instead for a hot seat under heavy contention,
 * swap findById() below for seatRepository.findByIdForUpdate() (pessimistic
 * SELECT ... FOR UPDATE) - it's already in SeatRepository. Building both and
 * writing up the trade-off in your README is a great resume talking point.
 */
@Service
@RequiredArgsConstructor
public class SeatService {

    private static final int HOLD_DURATION_MINUTES = 5;

    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final SeatBroadcastService broadcastService;

    public List<Seat> getSeatsForShow(Long showId) {
        return seatRepository.findByShowId(showId);
    }

    @Transactional
    public Seat holdSeat(Long seatId, String userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatUnavailableException("Seat not found"));

        boolean availableForHold = seat.getStatus() == SeatStatus.AVAILABLE
                || (seat.getStatus() == SeatStatus.HELD && isExpired(seat));

        if (!availableForHold) {
            throw new SeatUnavailableException(
                    "Seat " + seat.getSeatNumber() + " is not available for hold");
        }

        seat.setStatus(SeatStatus.HELD);
        seat.setHeldBy(userId);
        seat.setHoldExpiresAt(LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES));

        // save() here is where the @Version check actually fires against the DB
        Seat saved = seatRepository.save(seat);
        broadcastService.broadcastSeatUpdate(saved);
        return saved;
    }

    @Transactional
    public Booking confirmBooking(Long seatId, String userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatUnavailableException("Seat not found"));

        boolean heldByThisUser = seat.getStatus() == SeatStatus.HELD
                && userId.equals(seat.getHeldBy())
                && !isExpired(seat);

        if (!heldByThisUser) {
            throw new SeatUnavailableException(
                    "Seat " + seat.getSeatNumber() + " is not held by you, or the hold expired");
        }

        seat.setStatus(SeatStatus.BOOKED);
        Seat saved = seatRepository.save(seat);

        Booking booking = new Booking();
        booking.setSeat(saved);
        booking.setUserId(userId);
        booking.setBookedAt(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        broadcastService.broadcastSeatUpdate(saved);
        return savedBooking;
    }

    @Transactional
    public Seat releaseHold(Long seatId, String userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatUnavailableException("Seat not found"));

        if (seat.getStatus() == SeatStatus.HELD && userId.equals(seat.getHeldBy())) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHeldBy(null);
            seat.setHoldExpiresAt(null);
            Seat saved = seatRepository.save(seat);
            broadcastService.broadcastSeatUpdate(saved);
            return saved;
        }
        return seat;
    }

    private boolean isExpired(Seat seat) {
        return seat.getHoldExpiresAt() != null && seat.getHoldExpiresAt().isBefore(LocalDateTime.now());
    }

}
