package SE2.flightBooking.repository;

import SE2.flightBooking.model.Baggage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaggageRepository extends JpaRepository<Baggage, Long> {}