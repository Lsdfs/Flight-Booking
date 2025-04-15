package SE2.flightBooking.repository;

import SE2.flightBooking.model.Baggage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaggageRepository extends JpaRepository<Baggage, Long> {
    Optional<Baggage> findFirstByCategory(String category);
}