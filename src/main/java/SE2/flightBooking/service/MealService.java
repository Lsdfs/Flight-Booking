package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingMealRepository;
import SE2.flightBooking.repository.MealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealService {
    private static final Logger logger = LoggerFactory.getLogger(MealService.class);

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private BookingMealRepository bookingMealRepository;

    // Lấy tất cả meal options
    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    // Tìm meal theo category
    public Optional<Meal> findFirstByCategory(String category) {
        return mealRepository.findFirstByCategory(category);
    }

    public Optional<Meal> findById(Long mealId) {
        return mealRepository.findById(mealId);
    }

    @Transactional
    public BookingMeal createBookingMeal(Booking booking, Meal meal, Long flightId, int quantity) {
        BookingMeal bookingMeal = new BookingMeal(booking, meal, flightId, quantity);
        return bookingMealRepository.save(bookingMeal);
    }

    @Transactional
    public BookingMeal updateBookingMealQuantity(Long bookingMealId, int newQuantity) {
        BookingMeal bookingMeal = bookingMealRepository.findById(bookingMealId)
                .orElseThrow(() -> new IllegalArgumentException("BookingMeal not found"));
        bookingMeal.setQuantity(newQuantity);
        return bookingMealRepository.save(bookingMeal);
    }

    @Transactional
    public void deleteBookingMeal(Long bookingMealId) {
        bookingMealRepository.deleteById(bookingMealId);
    }

    public List<BookingMeal> findBookingMealsByBookingAndFlight(Booking booking, Long flightId) {
        return bookingMealRepository.findByBookingAndFlightId(booking, flightId);
    }

    public int countSelectedMeals(Booking booking, Long flightId, String category) {
        return bookingMealRepository.countByBookingAndFlightIdAndMealCategory(booking, flightId, category);
    }

    public double calculateAdditionalMealCost(Booking booking, Long flightId) {
        List<BookingMeal> bookingMeals = findBookingMealsByBookingAndFlight(booking, flightId);
        int freeMeals = booking.getPassengerCount() * booking.getFlights().stream()
                .filter(f -> f.getId().equals(flightId))
                .findFirst()
                .map(f -> f.getTicketClass().getFreeMealQuantity())
                .orElse(0);

        int standardMeals = countSelectedMeals(booking, flightId, "Standard");
        int remainingFree = Math.max(0, freeMeals - standardMeals);

        return bookingMeals.stream()
                .filter(bm -> !"Standard".equals(bm.getMeal().getCategory()) || remainingFree <= 0)
                .mapToDouble(bm -> bm.getMeal().getPrice() * bm.getQuantity())
                .sum();
    }
}