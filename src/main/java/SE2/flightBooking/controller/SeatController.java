package SE2.flightBooking.controller;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.model.Seat;
import SE2.flightBooking.model.User; // Ensure User is imported
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/select")
    public String selectSeats(
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

        List<Seat> departSeats = seatService.getAvailableSeatsForFlight(departFlight);

        @SuppressWarnings("unchecked")
        List<Long> selectedDepartSeats = (List<Long>) session.getAttribute("tempDepartSeatIds_" + bookingId);
        if (selectedDepartSeats == null) {
            selectedDepartSeats = new ArrayList<>();
        }

        List<Seat> returnSeats = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Long> selectedReturnSeats = (List<Long>) session.getAttribute("tempReturnSeatIds_" + bookingId);
        if (selectedReturnSeats == null) {
            selectedReturnSeats = new ArrayList<>();
        }

        System.out.println("Depart Seats Found: " + departSeats.size());

        String passengerName = booking.getUser() != null
                ? booking.getUser().getLastName() + " " + booking.getUser().getFirstName()
                : "Unknown Passenger";

        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);

            if (returnFlightOpt.isPresent()) {
                Flight returnFlight = returnFlightOpt.get();
                returnSeats = seatService.getAvailableSeatsForFlight(returnFlight);
                returnSeats.forEach(seat -> seat.setFlight(returnFlight));
                System.out.println("Return Seats Found: " + returnSeats.size());
            } else {
                model.addAttribute("warning", "Return flight specified in booking (ID: " + returnFlightId + ") not found.");
                System.err.println("Warning: Return flight with ID " + returnFlightId + " not found for booking " + bookingId);
            }
        }

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("departFlight", departFlight);
        model.addAttribute("departSeats", departSeats);
        model.addAttribute("selectedDepartSeats", selectedDepartSeats);
        model.addAttribute("returnSeats", returnSeats);
        model.addAttribute("selectedReturnSeats", selectedReturnSeats);
        model.addAttribute("applyToReturnTrip", applyToReturnTrip);
        model.addAttribute("passengerName", passengerName);

        return "option/seat";
    }

    @PostMapping("/save")
    @Transactional
    public String saveSeats(
            @RequestParam Long bookingId,
            @RequestParam Long flightId,
            @RequestParam String applyToReturnTrip,
            @RequestParam(required = false) List<Long> departSeatIds,
            @RequestParam(required = false) List<Long> returnSeatIds,
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
            User user = booking.getUser();
            if (user == null) {
                model.addAttribute("error", "No user associated with this booking.");
                return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
            }

            int numberOfPassengers = 1;
            if (booking.getUser() != null && booking.getUser().getCoflightMembers() != null) {
                numberOfPassengers += booking.getUser().getCoflightMembers().size();
            }

            if (departSeatIds != null && departSeatIds.size() > numberOfPassengers) {
                model.addAttribute("error", "The number of seats selected for the departure trip exceeds the number of passengers (" + numberOfPassengers + ").");
                return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
            }

            if (departSeatIds != null && !departSeatIds.isEmpty()) {
                List<Seat> availableDepartSeats = seatService.getAvailableSeatsForFlight(departFlight);
                for (Long seatId : departSeatIds) {
                    Optional<Seat> seatOpt = seatService.findById(seatId);
                    if (seatOpt.isEmpty()) {
                        model.addAttribute("error", "Seat with ID " + seatId + " not found.");
                        return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                    }
                    Seat seat = seatOpt.get();
                    if (!availableDepartSeats.contains(seat)) {
                        model.addAttribute("error", "Seat with ID " + seatId + " is no longer available.");
                        return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                    }
                    seatService.assignSeatToBooking(booking, seat);
                }
            }

            if ("yes".equals(applyToReturnTrip)) {
                if (booking.getFlights().size() <= 1) {
                    model.addAttribute("error", "Cannot apply seats to return trip: No return flight found.");
                    return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                }

                Long returnFlightId = booking.getFlights().get(1).getId();
                Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
                if (returnFlightOpt.isEmpty()) {
                    model.addAttribute("error", "Return flight not found.");
                    return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                }

                Flight returnFlight = returnFlightOpt.get();
                List<Seat> availableReturnSeats = seatService.getAvailableSeatsForFlight(returnFlight);

                // Validate the number of seats selected for return
                if (returnSeatIds != null && returnSeatIds.size() > numberOfPassengers) {
                    model.addAttribute("error", "The number of seats selected for the return trip exceeds the number of passengers (" + numberOfPassengers + ").");
                    return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                }

                if (returnSeatIds != null && !returnSeatIds.isEmpty()) {
                    for (Long seatId : returnSeatIds) {
                        Optional<Seat> seatOpt = seatService.findById(seatId);
                        if (seatOpt.isEmpty()) {
                            model.addAttribute("error", "Seat with ID " + seatId + " not found.");
                            return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                        }
                        Seat seat = seatOpt.get();
                        if (!availableReturnSeats.contains(seat)) {
                            model.addAttribute("error", "Seat with ID " + seatId + " is no longer available.");
                            return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
                        }
                        seatService.assignSeatToBooking(booking, seat);
                    }
                }
            }

            booking.calculateTotalPrice();
            bookingRepository.save(booking);

            session.removeAttribute("tempDepartSeatIds_" + bookingId);
            session.removeAttribute("tempReturnSeatIds_" + bookingId);

            return "redirect:/booking/services?bookingId=" + bookingId;

        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return prepareSeatSelectionModel(model, booking, departFlight, applyToReturnTrip, departSeatIds, returnSeatIds);
        }
    }

    @PostMapping("/autosave")
    @ResponseBody
    @Transactional
    public ResponseEntity<String> autoSaveSeats(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {
        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            List<Long> departSeatIds = payload.get("departSeatIds") != null
                    ? (List<Long>) payload.get("departSeatIds")
                    : new ArrayList<>();
            List<Long> returnSeatIds = payload.get("returnSeatIds") != null
                    ? (List<Long>) payload.get("returnSeatIds")
                    : new ArrayList<>();

            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
            }

            Booking booking = bookingOpt.get();
            User user = booking.getUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No user associated with this booking.");
            }

            int numberOfPassengers = 1;
            if (booking.getUser() != null && booking.getUser().getCoflightMembers() != null) {
                numberOfPassengers += booking.getUser().getCoflightMembers().size();
            }

            if (departSeatIds.size() > numberOfPassengers) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The number of seats selected for the departure trip exceeds the number of passengers (" + numberOfPassengers + ").");
            }
            if (returnSeatIds.size() > numberOfPassengers) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The number of seats selected for the return trip exceeds the number of passengers (" + numberOfPassengers + ").");
            }

            // Save temporarily to session
            session.setAttribute("tempDepartSeatIds_" + bookingId, departSeatIds);
            session.setAttribute("tempReturnSeatIds_" + bookingId, returnSeatIds);

            return ResponseEntity.ok("Auto-saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Auto-save failed: " + e.getMessage());
        }
    }

    private String prepareSeatSelectionModel(
            Model model,
            Booking booking,
            Flight departFlight,
            String applyToReturnTrip,
            List<Long> departSeatIds,
            List<Long> returnSeatIds) {
        List<Seat> departSeats = seatService.getAvailableSeatsForFlight(departFlight);
        List<Seat> returnSeats = new ArrayList<>();

        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
            if (returnFlightOpt.isPresent()) {
                Flight returnFlight = returnFlightOpt.get();
                returnSeats = seatService.getAvailableSeatsForFlight(returnFlight);
                returnSeats.forEach(seat -> seat.setFlight(returnFlight));
            }
        }

        model.addAttribute("bookingId", booking.getId());
        model.addAttribute("booking", booking);
        model.addAttribute("departFlight", departFlight);
        model.addAttribute("departSeats", departSeats);
        model.addAttribute("selectedDepartSeats", departSeatIds != null ? departSeatIds : new ArrayList<>());
        model.addAttribute("returnSeats", returnSeats);
        model.addAttribute("selectedReturnSeats", returnSeatIds != null ? returnSeatIds : new ArrayList<>());
        model.addAttribute("applyToReturnTrip", applyToReturnTrip);
        model.addAttribute("passengerName", booking.getUser() != null
                ? booking.getUser().getLastName() + " " + booking.getUser().getFirstName()
                : "Unknown Passenger");

        return "option/seat";
    }
}