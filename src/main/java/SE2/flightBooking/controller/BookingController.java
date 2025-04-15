package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired private BookingService bookingService;
    @Autowired private SeatService seatService;
    @Autowired private MealService mealService;
    @Autowired private BaggageService baggageService;

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
            Optional<Booking> bookingOpt = bookingService.findByReservationCodeAndNames(
                    reservationCode.trim(), lastName.trim(), firstName.trim());

            if (bookingOpt.isEmpty()) {
                model.addAttribute("error", "Booking not found");
                return "option/services";
            }

            Booking booking = bookingOpt.get();
            if (needsDefaultServices(booking)) {
                bookingService.updateBooking(booking);
            }

            redirectAttributes.addAttribute("bookingId", booking.getId());
            return "redirect:/booking/options";
        } catch (Exception e) {
            logger.error("Error finding booking", e);
            model.addAttribute("error", "Error finding booking: " + e.getMessage());
            return "option/services";
        }
    }

    private boolean needsDefaultServices(Booking booking) {
        return booking.getSeats().size() < booking.getPassengerCount() ||
                booking.getBookingMeals().size() < booking.getPassengerCount() ||
                booking.getBookingBaggages().isEmpty();
    }

    @GetMapping("/options")
    public String showOptions(
            @RequestParam Long bookingId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Booking booking = bookingService.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            boolean isRoundTrip = booking.getFlights().size() > 1;
            Flight departFlight = booking.getFlights().get(0);
            Flight returnFlight = isRoundTrip ? booking.getFlights().get(1) : null;

            model.addAttribute("booking", booking);
            model.addAttribute("isRoundTrip", isRoundTrip);
            model.addAttribute("departFlight", departFlight);
            model.addAttribute("returnFlight", returnFlight);
            model.addAttribute("availableDepartSeats", seatService.getAvailableSeatsForFlight(departFlight));
            model.addAttribute("availableReturnSeats", returnFlight != null ?
                    seatService.getAvailableSeatsForFlight(returnFlight) : List.of());
            model.addAttribute("mealOptions", mealService.getAllMeals());
            model.addAttribute("baggageOptions", baggageService.getAllBaggageOptions());

            return "option/option";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/booking/services";
        }
    }

    @GetMapping("/payment")
    public String showPayment(
            @RequestParam Long bookingId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Booking booking = bookingService.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            booking.calculateTotalPrice();
            bookingService.updateBooking(booking);

            model.addAttribute("booking", booking);
            return "payment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/booking/services";
        }
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

        try {
            if (!isValidPayment(cardNumber, cardHolderName, expiryDate, cvv)) {
                throw new IllegalArgumentException("Invalid payment details");
            }

            Booking booking = bookingService.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

            if (booking.getStatus() != Booking.BookingStatus.PENDING) {
                throw new IllegalStateException("Booking is already confirmed or cancelled");
            }

            bookingService.confirmBooking(bookingId);
            model.addAttribute("booking", booking);
            model.addAttribute("message", "Booking confirmed successfully!");
            return "confirmation";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "payment";
        }
    }

    private boolean isValidPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        return cardNumber != null && cardNumber.matches("\\d{16}") &&
                cardHolderName != null && !cardHolderName.trim().isEmpty() &&
                expiryDate != null && expiryDate.matches("\\d{2}/\\d{2}") &&
                cvv != null && cvv.matches("\\d{3}");
    }
}