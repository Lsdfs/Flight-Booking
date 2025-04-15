package SE2.flightBooking.repository;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.BookingMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingMealRepository extends JpaRepository<BookingMeal, Long> {
    List<BookingMeal> findByBookingAndFlightId(Booking booking, Long flightId);
    int countByBookingAndFlightIdAndMealCategory(Booking booking, Long flightId, String category);
}