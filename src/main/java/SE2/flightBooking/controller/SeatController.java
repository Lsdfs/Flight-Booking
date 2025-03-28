package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/seat")
public class SeatController {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    public SeatController(BookingRepository bookingRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }

    @GetMapping("/seats")
    public String getSeats(Model model) {
        List<Seat> seats = seatRepository.findAll();
        model.addAttribute("seats", seats);
        return "option/fragments";
    }

    @PostMapping("/select")
    public String selectSeat(@RequestParam Long bookingId, @RequestParam Long seatId, @RequestParam boolean applyToReturn, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Seat> seatOpt = seatRepository.findById(seatId);

        if (bookingOpt.isEmpty() || seatOpt.isEmpty()) {
            model.addAttribute("error", "Booking or Seat not found");
            return "redirect:/booking/" + bookingId;
        }

        Booking booking = bookingOpt.get();
        Seat seat = seatOpt.get();
        if (seat.getStatus() != Seat.Status.AVAILABLE) {
            model.addAttribute("error", "This seat has been selected");
            return "redirect:/booking/" + bookingId;
        }
        seat.setBooking(booking);
        seat.setStatus(Seat.Status.OCCUPIED);
        booking.setSeat(seat);

        if (applyToReturn && booking.getReturnFlightId() != null) {
            Optional<Seat> returnSeatOpt = seatRepository.findById(seatId);
            if (returnSeatOpt.isPresent()) {
                Seat returnSeat = returnSeatOpt.get();
                returnSeat.setStatus(Seat.Status.OCCUPIED);
                seatRepository.save(returnSeat);
            }
        }

        seatRepository.save(seat);
        bookingRepository.save(booking);
        if (seat.getPrice() > 0) return "redirect:/payment?bookingId=" + bookingId;
        return "redirect:/booking/" + bookingId;
    }
}