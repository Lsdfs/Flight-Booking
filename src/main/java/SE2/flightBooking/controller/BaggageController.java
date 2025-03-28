package SE2.flightBooking.controller;
import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/baggage")
public class BaggageController {
    private final BookingRepository bookingRepository;
    private final BaggageRepository baggageRepository;

    public BaggageController(BookingRepository bookingRepository, BaggageRepository baggageRepository) {
        this.bookingRepository = bookingRepository;
        this.baggageRepository = baggageRepository;
    }

    @GetMapping("/baggages")
    public String getBaggages(Model model) {
        List<Baggage> baggages = baggageRepository.findAll();
        model.addAttribute("baggages", baggages);
        return "option/fragments";
    }

    @PostMapping("/select")
    public String selectBaggage(@RequestParam Long bookingId, @RequestParam Long baggageId, @RequestParam boolean applyToReturn, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Baggage> baggageOpt = baggageRepository.findById(baggageId);

        if (bookingOpt.isEmpty() || baggageOpt.isEmpty()) {
            model.addAttribute("error", "Booking or Baggage not found");
            return "redirect:/booking/" + bookingId;
        }

        Booking booking = bookingOpt.get();
        Baggage baggage = baggageOpt.get();
        if (baggage.getBooking() != null) {
            model.addAttribute("error", "Baggage option is unavailable");
            return "redirect:/booking/" + bookingId;
        }
        baggage.setBooking(booking);
        booking.setBaggage(baggage);

        if (applyToReturn && booking.getReturnFlightId() != null) {
            Optional<Booking> returnTripOpt = bookingRepository.findById(booking.getReturnFlightId());
            if (returnTripOpt.isPresent()) {
                Booking returnTrip = returnTripOpt.get();
                returnTrip.setBaggage(baggage);
                bookingRepository.save(returnTrip);
            }
        }

        baggageRepository.save(baggage);
        bookingRepository.save(booking);
        if (baggage.getPrice() > 0) return "redirect:/payment?bookingId=" + bookingId;
        return "redirect:/booking/" + bookingId;
    }
}