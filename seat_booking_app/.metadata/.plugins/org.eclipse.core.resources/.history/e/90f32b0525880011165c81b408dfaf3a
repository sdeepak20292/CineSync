package com.booking.seatbooking.config;

import com.booking.seatbooking.entity.Seat;
import com.booking.seatbooking.entity.Show;
import com.booking.seatbooking.entity.ShowCategory;
import com.booking.seatbooking.repository.SeatRepository;
import com.booking.seatbooking.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    private record DemoShow(String title, String venue, ShowCategory category, int daysFromNow) {}

    @Override
    public void run(String... args) {
        if (showRepository.count() > 0) return;

        DemoShow[] demoShows = {
                new DemoShow("Demo Screening",      "Screen 1",     ShowCategory.MOVIE,           1),
                new DemoShow("Championship Final",  "Arena 1",      ShowCategory.SPORT,           2),
                new DemoShow("Global Tech Keynote",  "Online",       ShowCategory.LIVE_STREAMING,  1),
                new DemoShow("Friday Night Laughs",  "Comedy Club",  ShowCategory.STAND_UP_COMEDY, 3),
                new DemoShow("Modern Art Gala",      "Gallery Hall", ShowCategory.ART,             5),
        };

        for (DemoShow demo : demoShows) {
            Show show = new Show();
            show.setTitle(demo.title());
            show.setVenue(demo.venue());
            show.setCategory(demo.category());
            show.setStartTime(LocalDateTime.now().plusDays(demo.daysFromNow()));
            showRepository.save(show);

            String[] rows = {"A", "B", "C", "D", "E", "F"};
            for (String row : rows) {
                for (int num = 1; num <= 10; num++) {
                    Seat seat = new Seat();
                    seat.setShow(show);
                    seat.setSeatNumber(row + num);
                    seatRepository.save(seat);
                }
            }
        }
    }
}