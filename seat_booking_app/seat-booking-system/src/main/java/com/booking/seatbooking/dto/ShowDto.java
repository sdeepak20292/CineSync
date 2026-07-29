package com.booking.seatbooking.dto;

import com.booking.seatbooking.entity.Show;
import com.booking.seatbooking.entity.ShowCategory;

import java.time.LocalDateTime;

public record ShowDto(Long id, String title, String venue, LocalDateTime startTime, ShowCategory category) {
    public static ShowDto from(Show show) {
        return new ShowDto(show.getId(), show.getTitle(), show.getVenue(), show.getStartTime(), show.getCategory());
    }
}