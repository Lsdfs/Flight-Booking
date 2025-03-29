package SE2.flightBooking.controller;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.model.Seat;
import SE2.flightBooking.repository.FlightRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import SE2.flightBooking.repository.SeatRepository;
import SE2.flightBooking.repository.BookingRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/flight")
public class FlightController {
    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    public FlightController(FlightRepository flightRepository, BookingRepository bookingRepository, SeatRepository seatRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository=bookingRepository;
        this.seatRepository=seatRepository;
    }
    @GetMapping("/")
    public String homePage(Model model) {
        List<String> departure = flightRepository.findAllDepartures();
        List<String> destination = flightRepository.findAllDestinations();

        model.addAttribute("departure", departure);
        model.addAttribute("destination", destination);

        return "home";
    }

    // Search flight
    @GetMapping("/search")
    public String searchFlights(@RequestParam(required = false) String ticketType,
                                @RequestParam(required = false) String departure,
                                @RequestParam(required = false) String destination,
                                @RequestParam(required = false) String departureTime,
                                @RequestParam(required = false) String returnTime,
                                @RequestParam(required = false) Integer passengers,

                                Model model) {
// Check condition if null
        if (ticketType == null || departure == null || destination == null ||
                departureTime == null||returnTime == null || passengers == null) {
            model.addAttribute("error", "Please enter all information.");
            return "home";
        }
// Check departure and destination are not same
        if (departure.equals(destination)) {
            model.addAttribute("error", "Departure and Destination are the same.");
            return "home";
        }

        Flight.FlightType flightType = "one-way".equals(ticketType) ? Flight.FlightType.ONE_WAY : Flight.FlightType.ROUND_TRIP;

        List<Flight> flights = flightRepository.findFlightsByDepartureDestinationAndType(
                departure, destination, flightType, passengers, LocalDateTime.now());
// If flight is empty
        if (flights.isEmpty()) {
            model.addAttribute("error", "Unavailable flight.");
            return "home";
        }

        flights = flights.stream()
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .collect(Collectors.toList());

        model.addAttribute("flights", flights);
        model.addAttribute("passengers", passengers);
        model.addAttribute("ticketType", ticketType);
        return "searchFlight";
    }


// Booking flight in searchFlight page
    @PostMapping("/book")
    public String bookFlight(@RequestParam Long flightId, @RequestParam Long seatId,
                             @RequestParam Long bookingId, Model model) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Seat> seatOpt = seatRepository.findById(seatId);
//
        if (flightOpt.isEmpty() || seatOpt.isEmpty() || bookingOpt.isEmpty()) {
            model.addAttribute("error", "Flight, Seat, or Booking not found");
            return "redirect:/error";
        }
        Flight flight = flightOpt.get();
        Seat seat = seatOpt.get();
        Booking booking = bookingOpt.get();
// Check if it is not available seat
        if (flight.getAvailableSeats() <= 0) {
            model.addAttribute("error", "No seats available for this flight.");
            return "redirect:/flight/" + flightId;
        }

        if (!seat.getStatus().equals(Seat.Status.AVAILABLE)) {
            model.addAttribute("error", "This seat is already booked.");
            return "redirect:/searchFlight/" + flightId;
        }

        seat.setBooking(booking);
        seat.setStatus(Seat.Status.OCCUPIED);
        seatRepository.save(seat);
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        flightRepository.save(flight);
        return "redirect:/payment?bookingId=" + bookingId;
    }
}
