package SE2.flightBooking.controller;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.CoflightMember;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.model.User;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.CoflightRepo;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

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
    @Autowired
    FlightRepository flightRepository;
    @Autowired UserRepo userRepo;
    @GetMapping
    public String checkInMain(Model model, HttpSession httpSession){

        httpSession.removeAttribute("bookingCheckin");
        httpSession.removeAttribute("flightCheckin");
        String reservationCode = "";
        String lastName = "";
        model.addAttribute("reservationCode", reservationCode);
        model.addAttribute("lastName", lastName);
        return "checkin";
    }

    @PostMapping("/submit")
    public String checkCheckinInfo(
            @RequestParam("reservationCode") String reservationCode,
            @RequestParam("lastName") String lastName,
             HttpSession httpSession){

        if(reservationCode.isEmpty()||reservationCode.isBlank()||lastName.isEmpty()||lastName.isBlank()){
            return "redirect:/check-in";
        }
        Optional<Booking> opt = bookingRepository.findByReservationCodeAndUserLastName(reservationCode, lastName);
        if(opt.isEmpty()){
            System.err.println("error");
            return "redirect:/check-in";
        }
        Booking booking = opt.get();
        if(booking.getStatus()!= Booking.BookingStatus.CONFIRMED){
            return "redirect:/check-in";
        }
        httpSession.setAttribute("bookingCheckin", booking);
        return "redirect:/check-in/select-flight";
    }

    @GetMapping("/select-flight")
    public String checkinSelectFlight(Model model, HttpSession httpSession){
        Booking currentBooking = (Booking)httpSession.getAttribute("bookingCheckin");
        System.out.println("debug" + currentBooking);
        if(currentBooking==null){
            return "redirect:/check-in";
        }
        List<Flight> flightList = currentBooking.getFlights();
        System.out.println("flight list: " + flightList);
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
    public String checkinFlightSelected( HttpSession httpSession, Model model){
        Booking currentBooking = (Booking)httpSession.getAttribute("bookingCheckin");
        Flight currentFlight = (Flight)httpSession.getAttribute("flightCheckin");
        if(currentBooking==null||currentFlight==null){
            return "redirect:/check-in";
        }
        model.addAttribute("flight", currentFlight);
        model.addAttribute("booking", currentBooking);
        return "checkin-confirm";
    }

    @GetMapping("/lastPage")
    public String lastPage( HttpSession httpSession, Model model){

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
        bookingRepository.save(currentBooking);
        model.addAttribute("flight", currentFlight);
        model.addAttribute("booking", currentBooking);
        return "checkin-last";
    }
}
