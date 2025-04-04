package SE2.flightBooking.controller;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/services")
    public String showServices(@RequestParam Long bookingId, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Booking not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        model.addAttribute("booking", booking);
        return "option/option";
    }

    @GetMapping("/payment")
    public String showPayment(@RequestParam Long bookingId, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Booking not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        model.addAttribute("booking", booking);
        return "payment";
    }

    @PostMapping("/confirm")
    public String confirmBooking(@RequestParam Long bookingId, @RequestParam String cardNumber, @RequestParam String cardHolderName, @RequestParam String expiryDate, @RequestParam String cvv, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Booking not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        model.addAttribute("message", "Booking confirmed successfully!");
        model.addAttribute("booking", booking);
        return "confirmation";
    }
}