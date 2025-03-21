package SE2.flightBooking.service;

import SE2.flightBooking.model.User;
import SE2.flightBooking.model.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AuthService extends UserDetailsService {

    User registerUser(UserDTO userRegistrationDTO);
}
