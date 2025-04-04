package SE2.flightBooking.controller;

import SE2.flightBooking.model.Baggage;
import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.BaggageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        String passengerName = booking.getUser().getLastName() + " " + booking.getUser().getFirstName();

        Flight returnFlight = null;
        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
            if (returnFlightOpt.isPresent()) {
                returnFlight = returnFlightOpt.get();
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

        if (departBaggageQuantities != null) {
            departBaggageQuantities.forEach((baggageIdStr, quantityStr) -> {
                try {
                    Long baggageId = Long.parseLong(baggageIdStr);
                    int quantity = Integer.parseInt(quantityStr);
                    if (quantity > 0) {
                        Optional<Baggage> baggageOpt = baggageService.findById(baggageId);
                        if (baggageOpt.isPresent()) {
                            Baggage baggage = baggageOpt.get();
                            baggageService.addBaggageToBooking(booking, baggage, flightId, quantity);
                        }
                    }
                } catch (NumberFormatException e) {
                    //
                }
            });
        }

        if ("yes".equals(applyToReturnTrip)) {
            if (booking.getFlights().size() <= 1) {
                model.addAttribute("error", "Cannot apply baggage to return trip: No return flight found.");
                model.addAttribute("bookingId", bookingId);
                model.addAttribute("booking", booking);
                model.addAttribute("departFlight", departFlight);
                model.addAttribute("returnFlight", null);
                model.addAttribute("baggageOptions", baggageService.getAllBaggageOptions());
                model.addAttribute("applyToReturnTrip", applyToReturnTrip);
                model.addAttribute("passengerName", booking.getUser().getLastName() + " " + booking.getUser().getFirstName());
                return "option/baggage";
            }

            Long returnFlightId = booking.getFlights().get(1).getId();
            if (returnBaggageQuantities != null) {
                returnBaggageQuantities.forEach((baggageIdStr, quantityStr) -> {
                    try {
                        Long baggageId = Long.parseLong(baggageIdStr);
                        int quantity = Integer.parseInt(quantityStr);
                        if (quantity > 0) {
                            Optional<Baggage> baggageOpt = baggageService.findById(baggageId);
                            if (baggageOpt.isPresent()) {
                                Baggage baggage = baggageOpt.get();
                                baggageService.addBaggageToBooking(booking, baggage, returnFlightId, quantity);
                            }
                        }
                    } catch (NumberFormatException e) {
                        //
                    }
                });
            }
        }

        booking.calculateTotalPrice();
        bookingRepository.save(booking);

        return "redirect:/booking/services?bookingId=" + bookingId;
    }
}