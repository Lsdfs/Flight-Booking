package SE2.flightBooking.repository;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.BookingBaggage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingBaggageRepository extends JpaRepository<BookingBaggage, Long> {
    List<BookingBaggage> findByBookingAndFlightId(Booking booking, Long flightId);
    List<BookingBaggage> findByBooking(Booking booking);
}