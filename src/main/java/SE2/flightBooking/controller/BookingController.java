package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final MealRepository mealRepository;
    private final BaggageRepository baggageRepository;

    public BookingController(BookingRepository bookingRepository, SeatRepository seatRepository,
                             MealRepository mealRepository, BaggageRepository baggageRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.mealRepository = mealRepository;
        this.baggageRepository = baggageRepository;
    }

    @GetMapping("/{bookingId}")
    public String showBookingOptions(@PathVariable Long bookingId, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Booking ID not found.");
            return "error/NotFoundError";
        }
        Booking booking = bookingOpt.get();
        model.addAttribute("seats", seatRepository.findByFlightIdAndStatus(booking.getFlightId(), Seat.Status.AVAILABLE));
        model.addAttribute("meals", mealRepository.findAll());
        model.addAttribute("baggages", baggageRepository.findAll());
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("isRoundTrip", booking.getReturnFlightId() != null);
        return "option/option";
    }

    @GetMapping("/services")
    public String showServicesPage() {
        return "option/services";
    }

    @PostMapping("/services")
    public String validateBooking(@RequestParam String bookingCode, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findByBookingCode(bookingCode);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Not found active trip");
            return "option/services";
        }
        return "redirect:/booking/" + bookingOpt.get().getId();
    }


}