package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.MealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MealControllerTest {

    @InjectMocks
    private MealController mealController;

    @Mock
    private MealService mealService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    private Booking booking;
    private Flight flight;
    private Meal meal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        booking = new Booking();
        booking.setId(1L);
        booking.setAdditionalCost(0.0);
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

        booking.setFlights(Arrays.asList(flight));
    }

    @Test
    void selectMeal_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(mealService.getAllMeals()).thenReturn(Arrays.asList(meal));
        when(mealService.findBookingMealsByBookingAndFlight(any(Booking.class), anyLong()))
                .thenReturn(Collections.emptyList());

        String view = mealController.selectMeal(1L, 1L, "no", model, session);

        assertEquals("option/meal", view);
        verify(model).addAllAttributes(anyMap());
    }

    @Test
    void saveMeal_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(mealService.findById(1L)).thenReturn(Optional.of(meal));
        when(mealService.findBookingMealsByBookingAndFlight(any(Booking.class), anyLong()))
                .thenReturn(Collections.emptyList());
        when(mealService.calculateAdditionalMealCost(any(Booking.class), anyLong())).thenReturn(30.0);

        Map<String, String> params = new HashMap<>();
        params.put("departMeal[1]", "2");

        String redirect = mealController.saveMeal(1L, 1L, "no", params, model, session);

        assertEquals("redirect:/booking/services?bookingId=1", redirect);
        verify(bookingRepository).save(booking);
        assertEquals(30.0, booking.getAdditionalCost());
    }

    @Test
    void saveMeal_Ajax_Success() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bookingId", "1");
        payload.put("mealId", "1");
        payload.put("flightId", "1");
        payload.put("quantity", "2");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(mealService.findById(1L)).thenReturn(Optional.of(meal));
        when(mealService.findBookingMealsByBookingAndFlight(any(Booking.class), anyLong()))
                .thenReturn(Collections.emptyList());
        when(mealService.calculateAdditionalMealCost(any(Booking.class), anyLong())).thenReturn(30.0);

        ResponseEntity<Map<String, Object>> response = mealController.saveMealAjax(payload);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = response.getBody();
        assertEquals(30.0, responseBody.get("additionalCost"));
        verify(bookingRepository).save(booking);
    }
}