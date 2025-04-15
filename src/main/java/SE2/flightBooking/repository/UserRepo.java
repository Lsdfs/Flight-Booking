package SE2.flightBooking.repository;

import SE2.flightBooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

    User findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByGmail(String gmail);
    Optional<User> findByGmail(String gmail);



}
