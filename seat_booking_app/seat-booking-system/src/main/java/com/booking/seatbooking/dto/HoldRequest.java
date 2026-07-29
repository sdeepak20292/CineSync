package com.booking.seatbooking.dto;

import jakarta.validation.constraints.NotBlank;

public record HoldRequest(@NotBlank String userId) {
}
