package com.booking.seatbooking.dto;

public record AuthResponse(String token, String username, long expiresInSeconds) {}