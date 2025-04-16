package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BaggageRepository;
import SE2.flightBooking.repository.BookingBaggageRepository;
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

class BaggageServiceTest {

    @InjectMocks
    private BaggageService baggageService;

    @Mock
    private BaggageRepository baggageRepository;

    @Mock
    private BookingBaggageRepository bookingBaggageRepository;

    private Booking booking;
    private Flight flight;
    private Baggage baggage;
    private BookingBaggage bookingBaggage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        booking = new Booking();
        booking.setId(1L);
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

        bookingBaggage = new BookingBaggage(booking, baggage, 1L, 2);

        booking.setFlights(Arrays.asList(flight));
    }

    @Test
    void getAllBaggageOptions() {
        when(baggageRepository.findAll()).thenReturn(Arrays.asList(baggage));

        assertEquals(1, baggageService.getAllBaggageOptions().size());
        verify(baggageRepository).findAll();
    }

    @Test
    void createBookingBaggage() {
        when(bookingBaggageRepository.save(any(BookingBaggage.class))).thenReturn(bookingBaggage);

        BookingBaggage result = baggageService.createBookingBaggage(booking, baggage, 1L, 2);

        assertEquals(2, result.getQuantity());
        verify(bookingBaggageRepository).save(any(BookingBaggage.class));
    }

    @Test
    void calculateAdditionalBaggageCost_ExcessWeight() {
        when(bookingBaggageRepository.findByBookingAndFlightId(booking, 1L)).thenReturn(Arrays.asList(bookingBaggage));

        double cost = baggageService.calculateAdditionalBaggageCost(booking, 1L);

        assertEquals(50.0, cost);
        verify(bookingBaggageRepository).findByBookingAndFlightId(booking, 1L);
    }

    @Test
    void calculateAdditionalBaggageCost_NoExcessWeight() {
        baggage.setWeight(5.0);
        bookingBaggage.setQuantity(2);
        when(bookingBaggageRepository.findByBookingAndFlightId(booking, 1L)).thenReturn(Arrays.asList(bookingBaggage));

        double cost = baggageService.calculateAdditionalBaggageCost(booking, 1L);

        assertEquals(0.0, cost);
    }
}