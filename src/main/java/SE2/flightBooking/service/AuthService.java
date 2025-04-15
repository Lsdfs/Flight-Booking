package SE2.flightBooking.service;

import SE2.flightBooking.model.User;
import SE2.flightBooking.model.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface AuthService extends UserDetailsService {
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    User registerUser(UserDTO userRegistrationDTO);
    User updateUser(UserDTO userDTO, String phoneNumber);
}