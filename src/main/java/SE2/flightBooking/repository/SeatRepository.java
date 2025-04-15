package SE2.flightBooking.repository;

import SE2.flightBooking.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlight(Flight flight);

    List<Seat> findByFlightAndSeatClass(Flight flight, Seat.SeatClass seatClass);

    List<Seat> findByFlightAndStatus(Flight flight, Seat.SeatStatus status);

    List<Seat> findByFlightAndSeatClassAndStatus(Flight flight, Seat.SeatClass seatClass, Seat.SeatStatus status);
}