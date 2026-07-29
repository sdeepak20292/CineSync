package com.booking.seatbooking.dto;

import com.booking.seatbooking.entity.SeatStatus;

/** Pushed over WebSocket to /topic/shows/{showId} whenever a seat's status changes. */
public record SeatUpdateEvent(Long seatId, String seatNumber, SeatStatus status) {
}
