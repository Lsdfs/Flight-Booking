package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/meal")
public class MealController {

    @Autowired
    private MealService mealService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/select")
    public String selectMeals(@RequestParam Long bookingId, @RequestParam Long flightId,
                              @RequestParam String applyToReturnTrip, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            model.addAttribute("error", "Booking or flight not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();
        List<Meal> mealOptions = mealService.getAllMeals();
        List<Long> selectedDepartMeals = new ArrayList<>();
        Flight returnFlight = null;
        List<Long> selectedReturnMeals = new ArrayList<>();
        String passengerName = booking.getUser() != null
                ? booking.getUser().getLastName() + " " + booking.getUser().getFirstName()
                : "Unknown Passenger";

        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            Long returnFlightId = booking.getFlights().get(1).getId();
            Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
            if (returnFlightOpt.isPresent()) {
                returnFlight = returnFlightOpt.get();
            } else {
                model.addAttribute("warning", "Return flight not found.");
            }
        }

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("booking", booking);
        model.addAttribute("departFlight", departFlight);
        model.addAttribute("returnFlight", returnFlight);
        model.addAttribute("mealOptions", mealOptions);
        model.addAttribute("selectedDepartMeals", selectedDepartMeals);
        model.addAttribute("selectedReturnMeals", selectedReturnMeals);
        model.addAttribute("applyToReturnTrip", applyToReturnTrip);
        model.addAttribute("passengerName", passengerName);

        return "option/meal";
    }

    @PostMapping("/save")
    @Transactional
    public String saveMeals(@RequestParam Long bookingId,
                            @RequestParam Long flightId,
                            @RequestParam String applyToReturnTrip,
                            @RequestParam(required = false) List<Long> departMealIds,
                            @RequestParam(required = false) List<Long> returnMealIds,
                            @RequestParam(defaultValue = "1") int quantity,
                            Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            return "redirect:/error?message=Booking or flight not found.";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();

        try {
            if (departMealIds != null && !departMealIds.isEmpty()) {
                for (Long mealId : departMealIds) {
                    Optional<Meal> mealOpt = mealService.findById(mealId);
                    if (mealOpt.isEmpty()) {
                        model.addAttribute("error", "Meal with ID " + mealId + " not found.");
                        return prepareMealSelectionModel(model, booking, departFlight, applyToReturnTrip, departMealIds, returnMealIds);
                    }
                    Meal meal = mealOpt.get();
                    mealService.addMealToBooking(booking, meal, flightId, quantity);
                }
            }

            if ("yes".equals(applyToReturnTrip)) {
                if (booking.getFlights().size() <= 1) {
                    model.addAttribute("error", "Cannot apply meals to return trip: No return flight found.");
                    return prepareMealSelectionModel(model, booking, departFlight, applyToReturnTrip, departMealIds, returnMealIds);
                }

                Long returnFlightId = booking.getFlights().get(1).getId();
                Optional<Flight> returnFlightOpt = flightRepository.findById(returnFlightId);
                if (returnFlightOpt.isEmpty()) {
                    model.addAttribute("error", "Return flight not found.");
                    return prepareMealSelectionModel(model, booking, departFlight, applyToReturnTrip, departMealIds, returnMealIds);
                }

                if (returnMealIds != null && !returnMealIds.isEmpty()) {
                    for (Long mealId : returnMealIds) {
                        Optional<Meal> mealOpt = mealService.findById(mealId);
                        if (mealOpt.isEmpty()) {
                            model.addAttribute("error", "Meal with ID " + mealId + " not found.");
                            return prepareMealSelectionModel(model, booking, departFlight, applyToReturnTrip, departMealIds, returnMealIds);
                        }
                        Meal meal = mealOpt.get();
                        mealService.addMealToBooking(booking, meal, returnFlightId, quantity);
                    }
                }
            }

            booking.calculateTotalPrice();
            bookingRepository.save(booking);
            return "redirect:/booking/services?bookingId=" + bookingId;

        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return prepareMealSelectionModel(model, booking, departFlight, applyToReturnTrip, departMealIds, returnMealIds);
        }
    }

    private String prepareMealSelectionModel(Model model, Booking booking, Flight departFlight,
                                             String applyToReturnTrip, List<Long> departMealIds, List<Long> returnMealIds) {
        model.addAttribute("bookingId", booking.getId());
        model.addAttribute("booking", booking);
        model.addAttribute("departFlight", departFlight);
        model.addAttribute("returnFlight", null);
        model.addAttribute("mealOptions", mealService.getAllMeals());
        model.addAttribute("selectedDepartMeals", departMealIds != null ? departMealIds : new ArrayList<>());
        model.addAttribute("selectedReturnMeals", returnMealIds != null ? returnMealIds : new ArrayList<>());
        model.addAttribute("applyToReturnTrip", applyToReturnTrip);
        return "option/meal";
    }
}