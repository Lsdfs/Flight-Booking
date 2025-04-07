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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

        if (reservationCode == null || reservationCode.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty() ||
                firstName == null || firstName.trim().isEmpty()) {
            model.addAttribute("error", "Please provide all required fields: reservation code, last name, and first name.");
            return "option/services";
        }

        try {
            Optional<Booking> bookingOpt = bookingService.findByReservationCodeAndNames(
                    reservationCode, lastName, firstName);

            if (bookingOpt.isPresent()) {
                redirectAttributes.addAttribute("bookingId", bookingOpt.get().getId());
                return "redirect:/booking/options";
            } else {
                model.addAttribute("error", "Your booking cannot be found. Please check your reservation code and name.");
                return "option/services";
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while finding your booking: " + e.getMessage());
            return "option/services";
        }
    }

    @GetMapping("/options")
    public String showOptions(@RequestParam Long bookingId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found.");
            return "redirect:/booking/services";
        }

        Booking booking = bookingOpt.get();
        boolean isRoundTrip = booking.getFlights() != null && booking.getFlights().size() > 1;

        model.addAttribute("booking", booking);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("isRoundTrip", isRoundTrip);

        return "option/option";
    }

    @GetMapping("/payment")
    public String showPayment(@RequestParam Long bookingId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found.");
            return "redirect:/booking/services";
        }

        Booking booking = bookingOpt.get();
        model.addAttribute("booking", booking);
        return "payment";
    }

    @PostMapping("/confirm")
    @Transactional
    public String confirmBooking(
            @RequestParam Long bookingId,
            @RequestParam String cardNumber,
            @RequestParam String cardHolderName,
            @RequestParam String expiryDate,
            @RequestParam String cvv,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Booking> bookingOpt = bookingService.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Booking not found.");
            return "redirect:/booking/services";
        }

        Booking booking = bookingOpt.get();

        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            model.addAttribute("error", "Booking is already confirmed.");
            model.addAttribute("booking", booking);
            return "confirmation";
        }
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            model.addAttribute("error", "Cannot confirm a cancelled booking.");
            model.addAttribute("booking", booking);
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

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            LocalDate expiry = LocalDate.parse(expiryDate, formatter);
            LocalDate now = LocalDate.now();
            if (expiry.isBefore(now.withDayOfMonth(1))) {
                return false; // Card is expired
            }
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }
}