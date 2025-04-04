package SE2.flightBooking.controller;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.model.Seat;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
    public String selectSeats(@RequestParam Long bookingId, @RequestParam Long flightId,
                              @RequestParam String applyToReturnTrip, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            model.addAttribute("error", "Booking or flight not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();
        List<Seat> departSeats = seatService.getAvailableSeatsForFlight(departFlight);
        List<Long> selectedDepartSeats = new ArrayList<>();
        List<Seat> returnSeats = new ArrayList<>();
        List<Long> selectedReturnSeats = new ArrayList<>();
        String passengerName = booking.getUser().getLastName() + " " + booking.getUser().getFirstName();

        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
            if (returnFlightOpt.isPresent()) {
                returnSeats = seatService.getAvailableSeatsForFlight(returnFlightOpt.get());
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
    public String saveSeats(@RequestParam Long bookingId,
                            @RequestParam Long flightId,
                            @RequestParam String applyToReturnTrip,
                            @RequestParam(required = false) List<Long> departSeatIds,
                            @RequestParam(required = false) List<Long> returnSeatIds,
                            Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            return "redirect:/error?message=Booking or flight not found.";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();

        if (departSeatIds != null && !departSeatIds.isEmpty()) {
            for (Long seatId : departSeatIds) {
                Optional<Seat> seatOpt = seatService.findById(seatId);
                if (seatOpt.isPresent()) {
                    Seat seat = seatOpt.get();
                    seatService.assignSeatToBooking(seat);
                    booking.addSeat(seat);
                }
            }
        }

        if ("yes".equals(applyToReturnTrip)) {
            if (booking.getFlights().size() <= 1) {
                model.addAttribute("error", "Cannot apply seats to return trip: No return flight found.");
                model.addAttribute("bookingId", bookingId);
                model.addAttribute("booking", booking);
                model.addAttribute("departFlight", departFlight);
                model.addAttribute("departSeats", seatService.getAvailableSeatsForFlight(departFlight));
                model.addAttribute("selectedDepartSeats", departSeatIds != null ? departSeatIds : new ArrayList<>());
                model.addAttribute("returnSeats", new ArrayList<>());
                model.addAttribute("selectedReturnSeats", new ArrayList<>());
                model.addAttribute("applyToReturnTrip", applyToReturnTrip);
                return "option/seat";
            }

            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
            if (returnFlightOpt.isPresent() && returnSeatIds != null && !returnSeatIds.isEmpty()) {
                Flight returnFlight = returnFlightOpt.get();
                List<Seat> availableReturnSeats = seatService.getAvailableSeatsForFlight(returnFlight);
                for (Long seatId : returnSeatIds) {
                    Optional<Seat> seatOpt = seatService.findById(seatId);
                    if (seatOpt.isPresent() && availableReturnSeats.contains(seatOpt.get())) {
                        Seat seat = seatOpt.get();
                        seatService.assignSeatToBooking(seat);
                        booking.addSeat(seat);
                    }
                }
            }
        }

        booking.calculateTotalPrice();
        bookingRepository.save(booking);

        return "redirect:/booking/services?bookingId=" + bookingId;
    }
}