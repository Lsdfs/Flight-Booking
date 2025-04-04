package SE2.flightBooking.service;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.BookingMeal;
import SE2.flightBooking.model.Meal;
import SE2.flightBooking.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    @Transactional
    public void addMealToBooking(Booking booking, Meal meal, Long flightId, int quantity) {
        if (meal == null) {
            throw new IllegalArgumentException("Meal cannot be null.");
        }
        if (meal.getStock() < quantity) {
            throw new IllegalStateException("Not found meals.");
        }
        meal.setStock(meal.getStock() - quantity);
        mealRepository.save(meal);

        booking.addMeal(meal, flightId, quantity);
    }

    @Transactional
    public void removeMealFromBooking(Booking booking, BookingMeal bookingMeal) {
        if (bookingMeal == null || booking == null) {
            throw new IllegalArgumentException("Booking or BookingMeal cannot be null.");
        }
        Meal meal = bookingMeal.getMeal();
        meal.setStock(meal.getStock() + bookingMeal.getQuantity());
        mealRepository.save(meal);

        booking.getBookingMeals().remove(bookingMeal);
    }

    public Optional<Meal> findById(Long mealId) {
        if (mealId == null) {
            return Optional.empty();
        }
        return mealRepository.findById(mealId);
    }
}