package SE2.flightBooking.repository;

import SE2.flightBooking.model.CoflightMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoflightRepo extends JpaRepository<CoflightMember, Integer>{
}
