package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.BaggageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/baggage")
public class BaggageController {

    @Autowired
    private BaggageService baggageService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/select")
    public String selectBaggage(@RequestParam Long bookingId, @RequestParam Long flightId,
                                @RequestParam String applyToReturnTrip, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            model.addAttribute("error", "Booking or flight not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();
        List<Baggage> baggageOptions = baggageService.getAllBaggageOptions();
        String passengerName = booking.getUser() != null
                ? booking.getUser().getLastName() + " " + booking.getUser().getFirstName()
                : "Unknown Passenger"; // Xử lý user null

        Flight returnFlight = null;
        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
            if (returnFlightOpt.isPresent()) {
                returnFlight = returnFlightOpt.get();
            } else {
                model.addAttribute("warning", "Return flight not found.");
            }
        }

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("departFlight", departFlight);
        model.addAttribute("returnFlight", returnFlight);
        model.addAttribute("baggageOptions", baggageOptions);
        model.addAttribute("applyToReturnTrip", applyToReturnTrip);
        model.addAttribute("passengerName", passengerName);

        return "option/baggage";
    }

    @PostMapping("/save")
    @Transactional
    public String saveBaggage(@RequestParam Long bookingId,
                              @RequestParam Long flightId,
                              @RequestParam String applyToReturnTrip,
                              @RequestParam(required = false) Map<String, String> departBaggageQuantities,
                              @RequestParam(required = false) Map<String, String> returnBaggageQuantities,
                              Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            return "redirect:/error?message=Booking or flight not found.";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();

        try {
            if (departBaggageQuantities != null && !departBaggageQuantities.isEmpty()) {
                processBaggageQuantities(booking, flightId, departBaggageQuantities, model);
            }

            if ("yes".equals(applyToReturnTrip)) {
                if (booking.getFlights().size() <= 1) {
                    model.addAttribute("error", "Cannot apply baggage to return trip: No return flight found.");
                    return prepareBaggageSelectionModel(model, booking, departFlight, applyToReturnTrip);
                }

                Long returnFlightId = booking.getFlights().get(1).getId();
                Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
                if (returnFlightOpt.isEmpty()) {
                    model.addAttribute("error", "Return flight not found.");
                    return prepareBaggageSelectionModel(model, booking, departFlight, applyToReturnTrip);
                }

                if (returnBaggageQuantities != null && !returnBaggageQuantities.isEmpty()) {
                    processBaggageQuantities(booking, returnFlightId, returnBaggageQuantities, model);
                }
            }

            booking.calculateTotalPrice();
            bookingRepository.save(booking);
            return "redirect:/booking/services?bookingId=" + bookingId;

        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return prepareBaggageSelectionModel(model, booking, departFlight, applyToReturnTrip);
        }
    }

    private void processBaggageQuantities(Booking booking, Long flightId, Map<String, String> baggageQuantities, Model model) {
        baggageQuantities.forEach((baggageIdStr, quantityStr) -> {
            try {
                Long baggageId = Long.parseLong(baggageIdStr);
                int quantity = Integer.parseInt(quantityStr);
                if (quantity > 0) {
                    Optional<Baggage> baggageOpt = baggageService.findById(baggageId);
                    if (baggageOpt.isEmpty()) {
                        throw new IllegalArgumentException("Baggage with ID " + baggageId + " not found.");
                    }
                    Baggage baggage = baggageOpt.get();
                    baggageService.addBaggageToBooking(booking, baggage, flightId, quantity);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid baggage quantity for ID " + baggageIdStr);
            }
        });
    }

    private String prepareBaggageSelectionModel(Model model, Booking booking, Flight departFlight, String applyToReturnTrip) {
        model.addAttribute("bookingId", booking.getId());
        model.addAttribute("booking", booking);
        model.addAttribute("departFlight", departFlight);
        model.addAttribute("returnFlight", null);
        model.addAttribute("baggageOptions", baggageService.getAllBaggageOptions());
        model.addAttribute("applyToReturnTrip", applyToReturnTrip);
        model.addAttribute("passengerName", booking.getUser() != null
                ? booking.getUser().getLastName() + " " + booking.getUser().getFirstName()
                : "Unknown Passenger");
        return "option/baggage";
    }
}