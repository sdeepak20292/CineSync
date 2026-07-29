package com.booking.seatbooking.dto;

import com.booking.seatbooking.entity.Seat;
import com.booking.seatbooking.entity.SeatStatus;

public record SeatDto(Long id, String seatNumber, SeatStatus status) {
    public static SeatDto from(Seat seat) {
        return new SeatDto(seat.getId(), seat.getSeatNumber(), seat.getStatus());
    }
}
