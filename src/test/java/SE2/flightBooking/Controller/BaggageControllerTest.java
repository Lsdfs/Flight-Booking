package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.BaggageService;
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

class BaggageControllerTest {

    @InjectMocks
    private BaggageController baggageController;

    @Mock
    private BaggageService baggageService;

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
    private Baggage baggage;

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
        ticketClass.setFreeBaggageWeight(20.0);
        flight.setTicketClass(ticketClass);

        baggage = new Baggage();
        baggage.setId(1L);
        baggage.setWeight(10.0);
        baggage.setPricePerKg(5.0);

        booking.setFlights(Arrays.asList(flight));
    }

    @Test
    void selectBaggage_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(baggageService.getAllBaggageOptions()).thenReturn(Arrays.asList(baggage));
        when(baggageService.findBookingBaggagesByBookingAndFlight(any(Booking.class), anyLong()))
                .thenReturn(Collections.emptyList());

        String view = baggageController.selectBaggage(1L, 1L, "no", model, session);

        assertEquals("option/baggage", view);
        verify(model).addAllAttributes(anyMap());
    }

    @Test
    void selectBaggage_BookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        String view = baggageController.selectBaggage(1L, 1L, "no", model, session);

        assertEquals("error/notFoundError", view);
        verify(model).addAttribute("error", "Booking or flight not found.");
    }

    @Test
    void saveBaggage_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(baggageService.findById(1L)).thenReturn(Optional.of(baggage));
        when(baggageService.findBookingBaggagesByBookingAndFlight(any(Booking.class), anyLong()))
                .thenReturn(Collections.emptyList());
        when(baggageService.calculateAdditionalBaggageCost(any(Booking.class), anyLong())).thenReturn(50.0);

        Map<String, String> params = new HashMap<>();
        params.put("departBaggage[1]", "2");

        String redirect = baggageController.saveBaggage(1L, 1L, "no", params, model, session);

        assertEquals("redirect:/booking/services?bookingId=1", redirect);
        verify(bookingRepository).save(booking);
        assertEquals(50.0, booking.getAdditionalCost());
    }

    @Test
    void saveBaggage_Ajax_Success() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bookingId", "1");
        payload.put("baggageId", "1");
        payload.put("flightId", "1");
        payload.put("quantity", "2");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(baggageService.findById(1L)).thenReturn(Optional.of(baggage));
        when(baggageService.findBookingBaggagesByBookingAndFlight(any(Booking.class), anyLong()))
                .thenReturn(Collections.emptyList());
        when(baggageService.calculateAdditionalBaggageCost(any(Booking.class), anyLong())).thenReturn(50.0);

        ResponseEntity<Map<String, Object>> response = baggageController.saveBaggageAjax(payload);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = response.getBody();
        assertEquals(50.0, responseBody.get("additionalCost"));
        verify(bookingRepository).save(booking);
    }
}