package SE2.flightBooking.controller;


import SE2.flightBooking.model.Flight;
import SE2.flightBooking.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("")
public class MainController {
    @Autowired
    private FlightRepository flightRepository;
    @GetMapping("/")
    public String showMainPage(Authentication authentication, Model model) {
        List<String> departures = flightRepository.findAll()
                .stream()
                .map(Flight::getDeparture)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> destinations = flightRepository.findAll()
                .stream()
                .map(Flight::getDestination)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("departures", departures);
        model.addAttribute("destinations", destinations);
        return "home";
    }

}
