package SE2.flightBooking.repository;

import SE2.flightBooking.model.BookingBaggage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingBaggageRepository extends JpaRepository<BookingBaggage, Long>{

}
