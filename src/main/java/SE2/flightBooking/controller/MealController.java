package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.MealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/meal")
public class MealController {
    private static final Logger logger = LoggerFactory.getLogger(MealController.class);

    @Autowired
    private MealService mealService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/select")
    public String selectMeal(
            @RequestParam Long bookingId,
            @RequestParam Long flightId,
            @RequestParam String applyToReturnTrip,
            Model model,
            HttpSession session) {

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            model.addAttribute("error", "Booking or flight not found.");
            return "error/notFoundError";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();

        List<Meal> mealOptions = mealService.getAllMeals();
        Map<Long, Integer> selectedDepartMeals = mealService.findBookingMealsByBookingAndFlight(booking, flightId)
                .stream()
                .collect(Collectors.toMap(
                        bm -> bm.getMeal().getId(),
                        BookingMeal::getQuantity
                ));

        Map<Long, Integer> selectedReturnMeals = new HashMap<>();
        Flight returnFlight = null;
        if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
            returnFlight = booking.getFlights().get(1);
            selectedReturnMeals = mealService.findBookingMealsByBookingAndFlight(booking, returnFlight.getId())
                    .stream()
                    .collect(Collectors.toMap(
                            bm -> bm.getMeal().getId(),
                            BookingMeal::getQuantity
                    ));
        }

        model.addAllAttributes(prepareModelAttributes(
                booking, departFlight, returnFlight,
                mealOptions, selectedDepartMeals, selectedReturnMeals,
                applyToReturnTrip
        ));

        return "option/meal";
    }

    @PostMapping("/save")
    @Transactional
    public String saveMeal(
            @RequestParam Long bookingId,
            @RequestParam Long flightId,
            @RequestParam String applyToReturnTrip,
            @RequestParam Map<String, String> allParams,
            Model model,
            HttpSession session) {

        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (bookingOpt.isEmpty() || flightOpt.isEmpty()) {
            return "redirect:/error?message=Booking or flight not found.";
        }

        Booking booking = bookingOpt.get();
        Flight departFlight = flightOpt.get();

        try {
            Map<Long, Integer> departMeals = parseMealQuantities(allParams, "departMeal");
            double additionalCost = processMeals(booking, departFlight.getId(), departMeals);

            if ("yes".equals(applyToReturnTrip) && booking.getFlights().size() > 1) {
                Flight returnFlight = booking.getFlights().get(1);
                Map<Long, Integer> returnMeals = parseMealQuantities(allParams, "returnMeal");
                additionalCost += processMeals(booking, returnFlight.getId(), returnMeals);
            }

            booking.setAdditionalCost(booking.getAdditionalCost() + additionalCost);
            booking.calculateTotalPrice();
            bookingRepository.save(booking);

            session.removeAttribute("tempDepartMeals_" + bookingId);
            session.removeAttribute("tempReturnMeals_" + bookingId);

            return "redirect:/booking/services?bookingId=" + bookingId;

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return prepareMealSelectionModel(model, booking, departFlight, applyToReturnTrip);
        }
    }

    @PostMapping("/save-ajax")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> saveMealAjax(
            @RequestBody Map<String, Object> payload) {

        try {
            Long bookingId = Long.valueOf(payload.get("bookingId").toString());
            Long mealId = Long.valueOf(payload.get("mealId").toString());
            Long flightId = Long.valueOf(payload.get("flightId").toString());
            int quantity = Integer.parseInt(payload.get("quantity").toString());

            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            Optional<Meal> mealOpt = mealService.findById(mealId);

            if (bookingOpt.isEmpty() || mealOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingOpt.get();
            Meal meal = mealOpt.get();

            Optional<BookingMeal> existing = mealService.findBookingMealsByBookingAndFlight(booking, flightId)
                    .stream()
                    .filter(bm -> bm.getMeal().getId().equals(mealId))
                    .findFirst();

            if (existing.isPresent()) {
                mealService.updateBookingMealQuantity(existing.get().getId(), quantity);
            } else {
                mealService.createBookingMeal(booking, meal, flightId, quantity);
            }

            double additionalCost = mealService.calculateAdditionalMealCost(booking, flightId);
            booking.setAdditionalCost(additionalCost);
            booking.calculateTotalPrice();
            bookingRepository.save(booking);

            Map<String, Object> response = new HashMap<>();
            response.put("additionalCost", additionalCost);
            response.put("total", booking.getTotalPrice());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Các phương thức hỗ trợ
    private Map<Long, Integer> parseMealQuantities(Map<String, String> allParams, String prefix) {
        Map<Long, Integer> quantities = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith(prefix + "[")) {
                Long mealId = Long.parseLong(entry.getKey().replaceAll(prefix + "\\[(\\d+)\\]", "$1"));
                quantities.put(mealId, Integer.parseInt(entry.getValue()));
            }
        }
        return quantities;
    }

    private double processMeals(Booking booking, Long flightId, Map<Long, Integer> mealSelections) {
        double additionalCost = 0.0;

        List<BookingMeal> currentMeals = mealService.findBookingMealsByBookingAndFlight(booking, flightId);

        for (Map.Entry<Long, Integer> entry : mealSelections.entrySet()) {
            Long mealId = entry.getKey();
            int quantity = entry.getValue();

            Optional<Meal> mealOpt = mealService.findById(mealId);
            if (mealOpt.isEmpty()) continue;

            Meal meal = mealOpt.get();

            Optional<BookingMeal> existing = currentMeals.stream()
                    .filter(bm -> bm.getMeal().getId().equals(mealId))
                    .findFirst();

            if (existing.isPresent()) {
                BookingMeal bm = existing.get();
                if (quantity > 0) {
                    mealService.updateBookingMealQuantity(bm.getId(), quantity);
                } else {
                    mealService.deleteBookingMeal(bm.getId());
                }
            } else if (quantity > 0) {
                mealService.createBookingMeal(booking, meal, flightId, quantity);
            }
        }

        return mealService.calculateAdditionalMealCost(booking, flightId);
    }

    private Map<String, Object> prepareModelAttributes(
            Booking booking, Flight departFlight, Flight returnFlight,
            List<Meal> mealOptions, Map<Long, Integer> selectedDepartMeals,
            Map<Long, Integer> selectedReturnMeals, String applyToReturnTrip) {

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("bookingId", booking.getId());
        attributes.put("flightId", departFlight.getId());
        attributes.put("booking", booking);
        attributes.put("departFlight", departFlight);
        attributes.put("returnFlight", returnFlight);
        attributes.put("mealOptions", mealOptions);
        attributes.put("selectedDepartMeals", selectedDepartMeals);
        attributes.put("selectedReturnMeals", selectedReturnMeals);
        attributes.put("applyToReturnTrip", applyToReturnTrip);
        attributes.put("passengerName", booking.getUser() != null ?
                booking.getUser().getLastName() + " " + booking.getUser().getFirstName() :
                "Unknown Passenger");
        attributes.put("isRoundTrip", booking.getFlights().size() > 1);

        return attributes;
    }

    private String prepareMealSelectionModel(Model model, Booking booking, Flight flight, String applyToReturnTrip) {
        return selectMeal(booking.getId(), flight.getId(), applyToReturnTrip, model, null);
    }
}