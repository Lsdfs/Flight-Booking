package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/meal")
public class MealController {
    private final BookingRepository bookingRepository;
    private final MealRepository mealRepository;

    public MealController(BookingRepository bookingRepository, MealRepository mealRepository) {
        this.bookingRepository = bookingRepository;
        this.mealRepository = mealRepository;
    }

    @GetMapping("/meals")
    public String getMeals(Model model) {
        List<Meal> meals = mealRepository.findAll();
        model.addAttribute("meals", meals);
        return "option/fragments";
    }

    @PostMapping("/select")
    public String selectMeal(@RequestParam Long bookingId, @RequestParam Long mealId, @RequestParam int quantity,
                             @RequestParam boolean applyToReturn, Model model) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Meal> mealOpt = mealRepository.findById(mealId);

        if (bookingOpt.isEmpty() || mealOpt.isEmpty()) {
            model.addAttribute("error", "Booking or Meal not found");
            return "redirect:/booking/" + bookingId;
        }

        Booking booking = bookingOpt.get();
        Meal meal = mealOpt.get();
        if (meal.getStock() < quantity) {
            model.addAttribute("error", "Meal is out of stock");
            return "redirect:/booking/" + bookingId;
        }
        booking.addMeal(meal, quantity);
        meal.setStock(meal.getStock() - quantity);

        if (applyToReturn && booking.getReturnFlightId() != null) {
            Optional<Booking> returnTripOpt = bookingRepository.findById(booking.getReturnFlightId());
            if (returnTripOpt.isPresent()) {
                Booking returnTrip = returnTripOpt.get();
                returnTrip.addMeal(meal, quantity);
                bookingRepository.save(returnTrip);
            }
        }

        mealRepository.save(meal);
        bookingRepository.save(booking);
        if (meal.getPrice() > 0) return "redirect:/payment?bookingId=" + bookingId;
        return "redirect:/booking/" + bookingId;
    }
}