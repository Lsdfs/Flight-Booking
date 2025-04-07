package SE2.flightBooking.controller;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/services")
    public String servicesHome(Model model) {
        model.addAttribute("error", "");
        return "option/services";
    }

    @PostMapping("/find-booking")
    public String findBooking(
            @RequestParam String reservationCode,
            @RequestParam String lastName,
            @RequestParam String firstName,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<Booking> booking = bookingService.findByReservationCodeAndNames(
                    reservationCode, lastName, firstName);

            if (booking.isPresent()) {
                redirectAttributes.addAttribute("bookingId", booking.get().getId());
                return "redirect:/option/options";
            } else {
                model.addAttribute("error", "Your booking cannot be found");
                return "option/services";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "option/services";
        }
    }

    @GetMapping("/options")
    public String showOptions(@RequestParam Long bookingId, Model model) {
        Booking booking = bookingService.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
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
    @Transactional
    public String confirmBooking(@RequestParam Long bookingId,
                                 @RequestParam String cardNumber,
                                 @RequestParam String cardHolderName,
                                 @RequestParam String expiryDate,
                                 @RequestParam String cvv,
                                 Model model) {
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            model.addAttribute("error", "Booking not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();

        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            model.addAttribute("error", "Booking is already confirmed.");
            model.addAttribute("booking", booking);
            return "confirmation";
        }
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            model.addAttribute("error", "Cannot confirm a cancelled booking.");
            return "payment";
        }

        if (!isValidPayment(cardNumber, cardHolderName, expiryDate, cvv)) {
            model.addAttribute("error", "Invalid payment details. Please try again.");
            model.addAttribute("booking", booking);
            return "payment";
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingService.updateBooking(booking);

        model.addAttribute("message", "Booking confirmed successfully!");
        model.addAttribute("booking", booking);
        return "confirmation";
    }

    private boolean isValidPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        if (cardNumber == null || cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            return false;
        }
        if (cardHolderName == null || cardHolderName.trim().isEmpty()) {
            return false;
        }
        if (cvv == null || cvv.length() != 3 || !cvv.matches("\\d+")) {
            return false;
        }
        if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        return true;
    }
}