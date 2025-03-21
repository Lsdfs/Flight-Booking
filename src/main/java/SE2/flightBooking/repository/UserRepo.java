package SE2.flightBooking.repository;

import SE2.flightBooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {

//    User findByUsername(String username);

    User findByPhoneNumber(String phoneNumber); // Thêm phương thức này

}
