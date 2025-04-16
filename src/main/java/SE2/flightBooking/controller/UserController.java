package SE2.flightBooking.controller;

import SE2.flightBooking.model.CoflightMember;
import SE2.flightBooking.model.User;
import SE2.flightBooking.model.UserDTO;
import SE2.flightBooking.model.UserProfileUpdateDTO;
import SE2.flightBooking.repository.CoflightRepo;
import SE2.flightBooking.repository.UserRepo;
import SE2.flightBooking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final AuthService authService;
    private final UserRepo userRepo;
    private final CoflightRepo coflightRepo;

    // Constructor injection
    public UserController(AuthService authService, UserRepo userRepo, CoflightRepo coflightRepo) {
        this.authService = authService;
        this.userRepo = userRepo;
        this.coflightRepo = coflightRepo;
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
        model.addAttribute("user", user);
        return "profile"; // Render the "profile.html" template
    }

    @GetMapping("/update")
    public String showUpdateForm(Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (authenticatedUser == null) {
            return "error"; // Not authenticated
        }

        User user = userRepo.findByPhoneNumber(authenticatedUser.getUsername());
        if (user == null) {
            return "error"; // User not found
        }

        UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
        dto.setLastName(user.getLastName());
        dto.setFirstName(user.getFirstName());
        dto.setAddress(user.getAddress());
        dto.setGender(user.getGender());
        dto.setCitizenID(user.getCitizenID());
        dto.setPassportNumber(user.getPassportNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        // Set any other fields you want the user to be able to update

        model.addAttribute("user", dto);
        return "update";
    }

    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") UserProfileUpdateDTO dto,
                             BindingResult result,
                             @AuthenticationPrincipal UserDetails authenticatedUser) {
        if (result.hasErrors()) {
            return "update";
        }

        User user = userRepo.findByPhoneNumber(authenticatedUser.getUsername());
        if (user == null) return "error";

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setPassportNumber(dto.getPassportNumber());
        user.setCitizenID(dto.getCitizenID());

        userRepo.save(user);
        return "redirect:/user/profile";
    }

    @GetMapping("/coflight")
    public String getCoFlightMembers(Model model, @AuthenticationPrincipal UserDetails authenticatedUser){
        if (authenticatedUser == null) {
            return "error"; // Redirect to an error page if not authenticated
        }
        String phoneNumber = authenticatedUser.getUsername();
        User user = userRepo.findByPhoneNumber(phoneNumber);
        if (user == null) {
            return "error"; // Show an error page if the user is not found
        }
        List<CoflightMember> coflightMembers = user.getCoflightMembers();
        model.addAttribute("coflightMembers", coflightMembers);
        return "coflight";
    }

    @GetMapping("/coflight/add")
    public String addCoflightMemberForm(Model model){
        CoflightMember coflightMember = new CoflightMember();
        model.addAttribute("coflightMember", coflightMember);
        return "add-coflight";
    }

    @PostMapping("/coflight/add")
    public String addMember(
            @Valid @ModelAttribute("coflightMember") CoflightMember coflightMember,
            BindingResult result,
            Model model, @AuthenticationPrincipal UserDetails authenticatedUser) {

        if (result.hasErrors()) {
            return "add-coflight"; // return to form if there are errors
        }
        coflightMember.setUser(userRepo.findByPhoneNumber(authenticatedUser.getUsername()));
        coflightRepo.save(coflightMember);
        return "redirect:/user/coflight";
    }
    @GetMapping("/coflight/update/{id}")
    public String updateCoflightMemberForm(@PathVariable(value="id") int id, Model model){
        Optional<CoflightMember> coflightMember = coflightRepo.findById(id);
        if(coflightMember.isEmpty()){
            return "redirect:/user/coflight";
        }
        model.addAttribute("coflightMember", coflightMember.get());
        return "update-coflight";
    }

    @PostMapping("/coflight/update")
    public String updateCoflightMember(
            @Valid @ModelAttribute("coflightMember") CoflightMember coflightMember,
            BindingResult result) {
        if (result.hasErrors()) {
            return "update-coflight"; // return to form if there are errors
        }
        coflightRepo.save(coflightMember);
        return "redirect:/user/coflight";
    }

    @RequestMapping("/coflight/delete/{id}")
    public String deleteCoflightMember(@PathVariable(value="id") Integer id){
        if(coflightRepo.findById(id).isPresent()){
            CoflightMember cm = coflightRepo.getById(id);
            coflightRepo.delete(cm);
            return "redirect:/user/coflight";
        }else{
            return "404";
        }
    }
}