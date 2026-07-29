package com.booking.seatbooking.exception;

/** Thrown when a seat can't be held/booked because it's already HELD or BOOKED by someone else. */
public class SeatUnavailableException extends RuntimeException {
    public SeatUnavailableException(String message) {
        super(message);
    }
}
