package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getAvailableSeatsForFlight(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null.");
        }
        return seatRepository.findByFlightAndStatus(flight, Seat.SeatStatus.AVAILABLE);
    }

    @Transactional
    public void assignSeatToBooking(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat cannot be null.");
        }
        seat.setStatus(Seat.SeatStatus.OCCUPIED);
        seatRepository.save(seat);
    }

    @Transactional
    public void releaseSeat(Booking booking, Seat seat) {
        if (seat == null || seat.getStatus() != Seat.SeatStatus.OCCUPIED) {
            throw new IllegalArgumentException("Seat cannot be null.");
        }

        booking.getSeats().remove(seat);
        seat.setStatus(Seat.SeatStatus.AVAILABLE);
        seatRepository.save(seat);
    }

    public Optional<Seat> findById(Long seatId) {
        if (seatId == null) {
            return Optional.empty();
        }
        return seatRepository.findById(seatId);
    }
}