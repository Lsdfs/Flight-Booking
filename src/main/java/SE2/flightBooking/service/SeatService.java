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
    public void assignSeatToBooking(Booking booking, Seat seat) {
        if (booking == null || seat == null) {
            throw new IllegalArgumentException("Booking or seat cannot be null.");
        }
        if (seat.getStatus() != Seat.SeatStatus.AVAILABLE) {
            throw new IllegalStateException("Seat is not available for assignment.");
        }
        seat.setStatus(Seat.SeatStatus.OCCUPIED);
        booking.addSeat(seat); // Gán ghế vào booking
        seatRepository.save(seat);
    }

    @Transactional
    public void releaseSeat(Booking booking, Seat seat) {
        if (booking == null || seat == null) {
            throw new IllegalArgumentException("Booking or seat cannot be null.");
        }
        if (seat.getStatus() != Seat.SeatStatus.OCCUPIED) {
            throw new IllegalStateException("Seat is not occupied.");
        }
        if (!booking.getSeats().contains(seat)) {
            throw new IllegalStateException("Seat does not belong to this booking.");
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