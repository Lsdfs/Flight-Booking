package SE2.flightBooking.repository;

import SE2.flightBooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByFlightIdAndStatus(Long flightId, Seat.Status status);
}