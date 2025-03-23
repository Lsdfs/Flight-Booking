package SE2.flightBooking.controller;

import SE2.flightBooking.model.User;
import SE2.flightBooking.model.UserDTO;
import SE2.flightBooking.repository.UserRepo;
import SE2.flightBooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final AuthService authService;
    private final UserRepo userRepo;

    // Constructor injection
    public UserController(AuthService authService, UserRepo userRepo) {
        this.authService = authService;
        this.userRepo = userRepo;
    }

    @GetMapping("/profile")
    public String getCurrentUser(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (authenticatedUser == null) {
            return "error"; // Redirect to an error page if not authenticated
        }

        String phoneNumber = authenticatedUser.getUsername();
        User user = userRepo.findByPhoneNumber(phoneNumber);
        if (user == null) {
            return "error"; // Show an error page if the user is not found
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setAddress(user.getAddress());
        userDTO.setGender(user.getGender());
        userDTO.setLastName(user.getLastName());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setCitizenID(user.getCitizenID());
        userDTO.setPassportNumber(user.getPassportNumber());
        model.addAttribute("user", userDTO);
        return "profile"; // Render the "profile.html" template
    }

    @GetMapping("/update")
    public String showUpdateForm(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (authenticatedUser == null) {
            return "error"; // Redirect to an error page if not authenticated
        }

        String phoneNumber = authenticatedUser.getUsername();
        User user = userRepo.findByPhoneNumber(phoneNumber);

        if (user == null) {
            return "error"; // Show an error page if the user is not found
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setAddress(user.getAddress());
        userDTO.setGender(user.getGender());
        userDTO.setLastName(user.getLastName());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setCitizenID(user.getCitizenID());
        userDTO.setPassportNumber(user.getPassportNumber());


        model.addAttribute("user", userDTO);
        return "update"; // Render "update.html" template
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                             @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (authenticatedUser == null) {
            return "error"; // Redirect to an error page if not authenticated
        }

        String phoneNumber = authenticatedUser.getUsername();
        authService.updateUser(userDTO, phoneNumber);

        return "redirect:/users/account"; // Redirect to the account page after update
    }
}