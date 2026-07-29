package com.booking.seatbooking.controller;

import com.booking.seatbooking.dto.SeatDto;
import com.booking.seatbooking.entity.Booking;
import com.booking.seatbooking.entity.Seat;
import com.booking.seatbooking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows/{showId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public List<SeatDto> getSeats(@PathVariable Long showId) {
        return seatService.getSeatsForShow(showId).stream().map(SeatDto::from).toList();
    }

    @PostMapping("/{seatId}/hold")
    public ResponseEntity<SeatDto> holdSeat(@PathVariable Long showId, @PathVariable Long seatId, Authentication authentication) {
        Seat seat = seatService.holdSeat(seatId, authentication.getName());
        return ResponseEntity.ok(SeatDto.from(seat));
    }

    @PostMapping("/{seatId}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long showId, @PathVariable Long seatId, Authentication authentication) {
        Booking booking = seatService.confirmBooking(seatId, authentication.getName());
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{seatId}/release")
    public ResponseEntity<SeatDto> releaseSeat(@PathVariable Long showId, @PathVariable Long seatId, Authentication authentication) {
        Seat seat = seatService.releaseHold(seatId, authentication.getName());
        return ResponseEntity.ok(SeatDto.from(seat));
    }
}