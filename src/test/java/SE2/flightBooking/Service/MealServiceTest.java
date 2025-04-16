package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingMealRepository;
import SE2.flightBooking.repository.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MealServiceTest {

    @InjectMocks
    private MealService mealService;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private BookingMealRepository bookingMealRepository;

    private Booking booking;
    private Flight flight;
    private Meal meal;
    private BookingMeal bookingMeal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        booking = new Booking();
        booking.setId(1L);
        booking.setPassengerCount(2);

        flight = new Flight();
        flight.setId(1L);
        TicketClass ticketClass = new TicketClass();
        ticketClass.setFreeMealQuantity(1);
        flight.setTicketClass(ticketClass);

        meal = new Meal();
        meal.setId(1L);
        meal.setCategory("Premium");
        meal.setPrice(15.0);

        bookingMeal = new BookingMeal(booking, meal, 1L, 2);

        booking.setFlights(Arrays.asList(flight));
    }

    @Test
    void getAllMeals() {
        when(mealRepository.findAll()).thenReturn(Arrays.asList(meal));

        assertEquals(1, mealService.getAllMeals().size());
        verify(mealRepository).findAll();
    }

    @Test
    void createBookingMeal() {
        when(bookingMealRepository.save(any(BookingMeal.class))).thenReturn(bookingMeal);

        BookingMeal result = mealService.createBookingMeal(booking, meal, 1L, 2);

        assertEquals(2, result.getQuantity());
        verify(bookingMealRepository).save(any(BookingMeal.class));
    }

    @Test
    void calculateAdditionalMealCost_PremiumMeals() {
        when(bookingMealRepository.findByBookingAndFlightId(booking, 1L)).thenReturn(Arrays.asList(bookingMeal));
        when(bookingMealRepository.countByBookingAndFlightIdAndMealCategory(booking, 1L, "Standard")).thenReturn(0);

        double cost = mealService.calculateAdditionalMealCost(booking, 1L);

        assertEquals(30.0, cost); // 2 Premium meals * $15
        verify(bookingMealRepository).findByBookingAndFlightId(booking, 1L);
    }

    @Test
    void calculateAdditionalMealCost_StandardMealsWithinFreeLimit() {
        meal.setCategory("Standard");
        when(bookingMealRepository.findByBookingAndFlightId(booking, 1L)).thenReturn(Arrays.asList(bookingMeal));
        when(bookingMealRepository.countByBookingAndFlightIdAndMealCategory(booking, 1L, "Standard")).thenReturn(2);

        double cost = mealService.calculateAdditionalMealCost(booking, 1L);

        assertEquals(0.0, cost); // 2 Standard meals within free limit (2 passengers * 1 free meal)
    }
}