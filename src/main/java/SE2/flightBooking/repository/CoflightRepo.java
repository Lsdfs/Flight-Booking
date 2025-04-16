package SE2.flightBooking.repository;

import SE2.flightBooking.model.CoflightMember;
import SE2.flightBooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoflightRepo extends JpaRepository<CoflightMember, Integer>{
    Optional<List<CoflightMember>> findByUser(User user);
}
