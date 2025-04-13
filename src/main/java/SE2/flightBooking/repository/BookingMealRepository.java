package SE2.flightBooking.repository;

import SE2.flightBooking.model.BookingMeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingMealRepository extends JpaRepository<BookingMeal, Long> {

}