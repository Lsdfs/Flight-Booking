package SE2.flightBooking.service;

import SE2.flightBooking.model.UserDTO;
import SE2.flightBooking.model.RoleType;
import SE2.flightBooking.model.User;
import SE2.flightBooking.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class ImplementAuth implements AuthService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public User registerUser(UserDTO userRegistrationDTO) {

        User user = new User();

        user.setPhoneNumber(userRegistrationDTO.getPhoneNumber());
        user.setPassword("{noop}" + userRegistrationDTO.getPassword()); /// Danh dau cho spring biet password chua duoc ma hoa
        user.setFirstName(userRegistrationDTO.getFirstName());
        user.setLastName(userRegistrationDTO.getLastName());
        user.setGmail(userRegistrationDTO.getGmail());
        user.setRole(RoleType.valueOf(userRegistrationDTO.getRole()));


        return userRepo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepo.findByPhoneNumber(phoneNumber); // Sử dụng phoneNumber để tìm kiếm người dùng

        if (user == null) {
            throw new UsernameNotFoundException("User not found with phone number: " + phoneNumber);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(),
                user.getPassword(),
                java.util.Collections.emptyList()
        );
    }

    @Override
    public User updateUser(UserDTO userDTO, String phoneNumber) {
        User user = userRepo.findByPhoneNumber(phoneNumber);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Only allow updating these fields
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassportNumber(userDTO.getPassportNumber());
        user.setCitizenID(userDTO.getCitizenID());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setGender(userDTO.getGender());
        user.setAddress(userDTO.getAddress());

        return userRepo.save(user);
    }
}