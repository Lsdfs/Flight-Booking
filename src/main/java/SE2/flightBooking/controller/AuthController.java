package SE2.flightBooking.controller;

import SE2.flightBooking.model.UserDTO;
import SE2.flightBooking.model.User;
import SE2.flightBooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    private String registerPage (Model model){

        UserDTO userDTO = new UserDTO();
        model.addAttribute("userDTO", userDTO);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTO userRegistrationDTO,
                               BindingResult result,
                               Model model) {


        if (result.hasErrors()) {
            return "register";
        }


        if (authService.existsByEmail(userRegistrationDTO.getGmail())) {
            model.addAttribute("emailError", "Email existed, please use another one");
            return "register";
        }


        if (authService.existsByPhoneNumber(userRegistrationDTO.getPhoneNumber())) {
            model.addAttribute("phoneError", "Phone number existed, please use another one");
            return "register";
        }

        authService.registerUser(userRegistrationDTO);
        return "redirect:/auth/login?registerSuccess";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute UserDTO userDTO, Model model) {
        try {
            User user = (User) authService.loadUserByUsername(userDTO.getPhoneNumber());

            if (user != null) {
                return "redirect:/";
            }

        } catch (UsernameNotFoundException e) {
            model.addAttribute("error", "Wrong phone number or password!");
        }
        return "login";
    }

}
