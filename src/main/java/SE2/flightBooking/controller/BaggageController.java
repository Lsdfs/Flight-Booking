package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.BaggageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/baggage")
public class BaggageController {
    private static final Logger logger = LoggerFactory.getLogger(BaggageController.class);

    @Autowired
    private BaggageService baggageService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/select")
    public String selectBaggage(
            @RequestParam Long bookingId,
            @RequestParam Long flightId,
            @RequestParam String applyToReturnTrip,
            Model model,
            HttpSession session) {

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            model.addAttribute("error", "Booking or flight not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();

        List<Baggage> baggageOptions = baggageService.getAllBaggageOptions();
        Map<Long, Integer> selectedDepartBaggage = baggageService.findBookingBaggagesByBookingAndFlight(booking, flightId)
                .stream()
                .collect(Collectors.toMap(
                        bb -> bb.getBaggage().getId(),
                        BookingBaggage::getQuantity
                ));

        Map<Long, Integer> selectedReturnBaggage = new HashMap<>();
        Flight returnFlight = null;
        if ("yes".equals(applyToReturnTrip)) {
            returnFlight = booking.getFlights().stream()
                    .filter(f -> !f.getId().equals(flightId))
                    .findFirst()
                    .orElse(null);

            if (returnFlight != null) {
                selectedReturnBaggage = baggageService.findBookingBaggagesByBookingAndFlight(booking, returnFlight.getId())
                        .stream()
                        .collect(Collectors.toMap(
                                bb -> bb.getBaggage().getId(),
                                BookingBaggage::getQuantity
                        ));
            }
        }

        model.addAllAttributes(prepareModelAttributes(
                booking, departFlight, returnFlight,
                baggageOptions, selectedDepartBaggage, selectedReturnBaggage,
                applyToReturnTrip
        ));

        return "option/baggage";
    }

    @PostMapping("/save")
    @Transactional
    public String saveBaggage(
            @RequestParam Long bookingId,
            @RequestParam Long flightId,
            @RequestParam String applyToReturnTrip,
            @RequestParam Map<String, String> allParams,
            Model model,
            HttpSession session) {

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            return "redirect:/error?message=Booking or flight not found.";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();

        try {
            Map<Long, Integer> departBaggage = parseBaggageQuantities(allParams, "departBaggage");
            double additionalCost = processBaggages(booking, departFlight.getId(), departBaggage);

            if ("yes".equals(applyToReturnTrip)) {
                Optional<Flight> returnFlightOpt = booking.getFlights().stream()
                        .filter(f -> !f.getId().equals(flightId))
                        .findFirst();

                if (returnFlightOpt.isPresent()) {
                    Map<Long, Integer> returnBaggage = parseBaggageQuantities(allParams, "returnBaggage");
                    additionalCost += processBaggages(booking, returnFlightOpt.get().getId(), returnBaggage);
                }
            }

            booking.setAdditionalCost(booking.getAdditionalCost() + additionalCost);
            booking.calculateTotalPrice();
            bookingRepository.save(booking);

            session.removeAttribute("tempDepartBaggage_" + bookingId);
            session.removeAttribute("tempReturnBaggage_" + bookingId);

            return "redirect:/booking/services?bookingId=" + bookingId;

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return prepareBaggageSelectionModel(model, booking, departFlight, applyToReturnTrip);
        }
    }

    @PostMapping("/save-ajax")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> saveBaggageAjax(
            @RequestBody Map<String, Object> payload) {

        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long baggageId = Long.valueOf(payload.get("baggageId").toString());
            Long flightId = Long.valueOf(payload.get("flightId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            Optional<Baggage> baggageOpt = baggageService.findById(baggageId);

            if (bookingOpt.isEmpty() || baggageOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingOpt.get();
            Baggage baggage = baggageOpt.get();

            Optional<BookingBaggage> existing = baggageService.findBookingBaggagesByBookingAndFlight(booking, flightId)
                    .stream()
                    .filter(bb -> bb.getBaggage().getId().equals(baggageId))
                    .findFirst();

            if (existing.isPresent()) {
                baggageService.updateBookingBaggageQuantity(existing.get().getId(), quantity);
            } else if (quantity > 0) {
                baggageService.createBookingBaggage(booking, baggage, flightId, quantity);
            }

            double additionalCost = baggageService.calculateAdditionalBaggageCost(booking, flightId);
            booking.setAdditionalCost(additionalCost);
            booking.calculateTotalPrice();
            bookingRepository.save(booking);

            Map<String, Object> response = new HashMap<>();
            response.put("additionalCost", additionalCost);
            response.put("total", booking.getTotalPrice());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Map<Long, Integer> parseBaggageQuantities(Map<String, String> allParams, String prefix) {
        Map<Long, Integer> quantities = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith(prefix + "[")) {
                Long baggageId = Long.parseLong(entry.getKey().replaceAll(prefix + "\\[(\\d+)\\]", "$1"));
                quantities.put(baggageId, Integer.parseInt(entry.getValue()));
            }
        }
        return quantities;
    }

    private double processBaggages(Booking booking, Long flightId, Map<Long, Integer> baggageSelections) {
        List<BookingBaggage> currentBaggages = baggageService.findBookingBaggagesByBookingAndFlight(booking, flightId);

        for (Map.Entry<Long, Integer> entry : baggageSelections.entrySet()) {
            Long baggageId = entry.getKey();
            int quantity = entry.getValue();

            Optional<Baggage> baggageOpt = baggageService.findById(baggageId);
            if (baggageOpt.isEmpty()) continue;

            Baggage baggage = baggageOpt.get();

            Optional<BookingBaggage> existing = currentBaggages.stream()
                    .filter(bb -> bb.getBaggage().getId().equals(baggageId))
                    .findFirst();

            if (existing.isPresent()) {
                BookingBaggage bb = existing.get();
                if (quantity > 0) {
                    baggageService.updateBookingBaggageQuantity(bb.getId(), quantity);
                } else {
                    baggageService.deleteBookingBaggage(bb.getId());
                }
            } else if (quantity > 0) {
                baggageService.createBookingBaggage(booking, baggage, flightId, quantity);
            }
        }

        return baggageService.calculateAdditionalBaggageCost(booking, flightId);
    }

    private Map<String, Object> prepareModelAttributes(
            Booking booking, Flight departFlight, Flight returnFlight,
            List<Baggage> baggageOptions, Map<Long, Integer> selectedDepartBaggage,
            Map<Long, Integer> selectedReturnBaggage, String applyToReturnTrip) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("bookingId", booking.getId());
        attributes.put("flightId", departFlight.getId());
        attributes.put("booking", booking);
        attributes.put("departFlight", departFlight);
        attributes.put("returnFlight", returnFlight);
        attributes.put("baggageOptions", baggageOptions);
        attributes.put("selectedDepartBaggage", selectedDepartBaggage);
        attributes.put("selectedReturnBaggage", selectedReturnBaggage);
        attributes.put("applyToReturnTrip", applyToReturnTrip);
        attributes.put("passengerName", booking.getUser() != null ?
                booking.getUser().getLastName() + " " + booking.getUser().getFirstName() :
                "Unknown Passenger");

        return attributes;
    }

    private String prepareBaggageSelectionModel(Model model, Booking booking, Flight flight, String applyToReturnTrip) {
        return selectBaggage(booking.getId(), flight.getId(), applyToReturnTrip, model, null);
    }
}