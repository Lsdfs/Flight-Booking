package SE2.flightBooking.controller;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.CoflightMember;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.model.User;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.CoflightRepo;
import SE2.flightBooking.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/check-in")
public class CheckinController {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired UserRepo userRepo;
    @GetMapping({"", "/"})
    public String checkInMain(@AuthenticationPrincipal UserDetails authenticatedUser, Model model, HttpSession httpSession){
        if(authenticatedUser==null){
            return "redirect:/auth/login";
        }
        httpSession.removeAttribute("bookingCheckin");
        httpSession.removeAttribute("flightCheckin");
        String reservationCode = "";
        String lastName = "";
        model.addAttribute("reservationCode", reservationCode);
        model.addAttribute("lastName", lastName);
        return "checkin";
    }

    @PostMapping("/")
    public String checkCheckinInfo(
            @RequestParam("reservationCode") String reservationCode,
            @RequestParam("lastName") String lastName,
            @AuthenticationPrincipal UserDetails authenticatedUser, HttpSession httpSession){
        if(authenticatedUser==null){
            return "redirect:/auth/login";
        }
        if(reservationCode.isEmpty()||reservationCode.isBlank()||lastName.isEmpty()||lastName.isBlank()){
            return "redirect:/check-in";
        }
        Optional<Booking> opt = bookingRepository.findByReservationCodeAndUserLastName(reservationCode, lastName);
        if(opt.isEmpty()){
            return "redirect:/check-in";
        }
        Booking booking = opt.get();
        httpSession.setAttribute("bookingCheckin", booking);
        return "redirect:/select-flight";
    }

    @GetMapping("/select-flight")
    public String checkinSelectFlight(Model model,@AuthenticationPrincipal UserDetails authenticatedUser, HttpSession httpSession){
        if(authenticatedUser==null){
            return "redirect:/auth/login";
        }
        Booking currentBooking = (Booking)httpSession.getAttribute("bookingCheckin");
        if(currentBooking==null){
            return "redirect:/check-in";
        }
        List<Flight> flightList = currentBooking.getFlights();
        for(Flight a: flightList){
            if(a.getFlightType()== Flight.FlightType.ONE_WAY&&a.getDepartureTime().isBefore(LocalDateTime.now())){
                flightList.remove(a);
            }else if(a.getFlightType()== Flight.FlightType.ROUND_TRIP&&a.getReturnTime().isBefore(LocalDateTime.now())){
                flightList.remove(a);
            }
        }
        if(flightList.isEmpty()){
            return "redirect:/check-in";
        }
        Flight mainFlight = flightList.getFirst();
        if(mainFlight.getFlightType()== Flight.FlightType.ONE_WAY){
            model.addAttribute("flightType", "ONE_WAY");
            model.addAttribute("secondFlight", false);
        }else{
            model.addAttribute("flightType", "ROUND_TRIP");
            if(mainFlight.getDepartureTime().isBefore(LocalDateTime.now())){
                model.addAttribute("firstFlight", false);
                model.addAttribute("secondFlight", true);
            }else{
                model.addAttribute("firstFlight", true);
                model.addAttribute("secondFlight", false);
            }
        }
        httpSession.setAttribute("flightCheckin", mainFlight);
        model.addAttribute("flightIsSelected", false);
        model.addAttribute("flight", mainFlight);
        return "checkin-select-flight";
    }

    @GetMapping("/confirm-flight")
    public String checkinFlightSelected(@AuthenticationPrincipal UserDetails authenticatedUser, HttpSession httpSession, Model model){
        if(authenticatedUser==null){
            return "redirect:/auth/login";
        }
        Booking currentBooking = (Booking)httpSession.getAttribute("bookingCheckin");
        Flight currentFlight = (Flight)httpSession.getAttribute("flightCheckin");
        if(currentBooking==null||currentFlight==null){
            return "redirect:/check-in";
        }
        model.addAttribute("flight", currentFlight);
        model.addAttribute("booking", currentBooking);
        return "checkin-confirm";
    }

    @PostMapping("lastPage")
    public String lastPage(@AuthenticationPrincipal UserDetails authenticatedUser, HttpSession httpSession, Model model){
        if(authenticatedUser==null){
            return "redirect:/auth/login";
        }
        Booking currentBooking = (Booking)httpSession.getAttribute("bookingCheckin");
        Flight currentFlight = (Flight)httpSession.getAttribute("flightCheckin");
        if(currentBooking==null||currentFlight==null){
            return "redirect:/check-in";
        }
        if(currentFlight.getFlightType()==Flight.FlightType.ONE_WAY) {
            currentBooking.setStatus(Booking.BookingStatus.ONEWAYCHECKEDIN);
        }else if(currentFlight.getFlightType()==Flight.FlightType.ROUND_TRIP){
            currentBooking.setStatus(Booking.BookingStatus.ROUNDTRIPCHECKEDIN);
        }else{
            return "redirect:/check-in";
        }
        model.addAttribute("flight", currentFlight);
        model.addAttribute("booking", currentBooking);
        return "checkin-last";
    }
}
