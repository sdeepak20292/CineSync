package com.booking.seatbooking.controller;

import com.booking.seatbooking.dto.ShowDto;
import com.booking.seatbooking.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowRepository showRepository;

    @GetMapping
    public List<ShowDto> getAllShows() {
        return showRepository.findAll().stream().map(ShowDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowDto> getShow(@PathVariable Long id) {
        return showRepository.findById(id)
                .map(ShowDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}