package SE2.flightBooking.controller;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.repository.FlightRepository;
import SE2.flightBooking.service.SeatService;
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

class SeatControllerTest {

    @InjectMocks
    private SeatController seatController;

    @Mock
    private SeatService seatService;

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
    private Seat seat;

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
        ticketClass.setFreeSeatClass(Seat.SeatClass.STANDARD);
        flight.setTicketClass(ticketClass);

        seat = new Seat();
        seat.setId(1L);
        seat.setSeatClass(Seat.SeatClass.EXTRA_LEGROOM);
        seat.setPrice(50.0);
        seat.setStatus(Seat.SeatStatus.AVAILABLE);
        seat.setFlight(flight);

        booking.setFlights(Arrays.asList(flight));
    }

    @Test
    void selectSeats_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(seatService.getAvailableSeatsForFlight(flight)).thenReturn(Arrays.asList(seat));

        String view = seatController.selectSeats(1L, 1L, "no", model, session);

        assertEquals("option/seat", view);
        verify(model).addAttribute("departSeats", Arrays.asList(seat));
    }

    @Test
    void saveSeats_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(seatService.findById(1L)).thenReturn(Optional.of(seat));
        when(seatService.isSeatAvailable(any(Seat.class), any(Flight.class))).thenReturn(true);

        List<Long> departSeatIds = Arrays.asList(1L);

        String redirect = seatController.saveSeats(1L, 1L, "no", departSeatIds, null, model, session);

        assertEquals("redirect:/booking/services?bookingId=1", redirect);
        verify(bookingRepository).save(booking);
        assertEquals(50.0, booking.getAdditionalCost());
    }

    @Test
    void saveSeat_Ajax_Success() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bookingId", "1");
        payload.put("seatId", "1");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(seatService.findById(1L)).thenReturn(Optional.of(seat));
        when(seatService.isSeatAvailable(any(Seat.class), any(Flight.class))).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = seatController.saveSeatAjax(payload);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = response.getBody();
        assertEquals(50.0, responseBody.get("additionalCost"));
        verify(bookingRepository).save(booking);
    }

    @Test
    void saveSeats_TooManySeats() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        List<Long> departSeatIds = Arrays.asList(1L, 2L, 3L); // More than passenger count (2)

        String view = seatController.saveSeats(1L, 1L, "no", departSeatIds, null, model, session);

        assertEquals("option/seat", view);
        verify(model).addAttribute("error", "The number of seats selected for the departure trip exceeds the number of passengers (2).");
    }
}