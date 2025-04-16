package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeatServiceTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

    private Booking booking;
    private Flight flight;
    private Seat seat;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        booking = new Booking();
        booking.setId(1L);

        flight = new Flight();
        flight.setId(1L);

        seat = new Seat();
        seat.setId(1L);
        seat.setFlight(flight);
        seat.setStatus(Seat.SeatStatus.AVAILABLE);
        seat.setSeatClass(Seat.SeatClass.STANDARD);

        booking.setFlights(Arrays.asList(flight));
    }

    @Test
    void isSeatAvailable_Success() {
        assertTrue(seatService.isSeatAvailable(seat, flight));
    }

    @Test
    void isSeatAvailable_SeatNotAvailable() {
        seat.setStatus(Seat.SeatStatus.SELECTING);
        seat.setBooking(booking);

        assertFalse(seatService.isSeatAvailable(seat, flight));
    }

    @Test
    void getAvailableSeatsForFlight() {
        when(seatRepository.findByFlightAndStatus(flight, Seat.SeatStatus.AVAILABLE)).thenReturn(Arrays.asList(seat));

        assertEquals(1, seatService.getAvailableSeatsForFlight(flight).size());
        verify(seatRepository).findByFlightAndStatus(flight, Seat.SeatStatus.AVAILABLE);
    }

    @Test
    void assignSeatToBooking_Success() {
        when(seatRepository.save(seat)).thenReturn(seat);

        seatService.assignSeatToBooking(booking, seat, flight);

        assertEquals(Seat.SeatStatus.SELECTING, seat.getStatus());
        assertEquals(booking, seat.getBooking());
        verify(seatRepository).save(seat);
    }

    @Test
    void assignSeatToBooking_SeatNotAvailable() {
        seat.setStatus(Seat.SeatStatus.SELECTING);
        seat.setBooking(new Booking());

        assertThrows(IllegalStateException.class, () -> seatService.assignSeatToBooking(booking, seat, flight));
    }

    @Test
    void releaseSeat_Success() {
        seat.setStatus(Seat.SeatStatus.SELECTING);
        seat.setBooking(booking);
        when(seatRepository.save(seat)).thenReturn(seat);

        seatService.releaseSeat(booking, seat);

        assertEquals(Seat.SeatStatus.AVAILABLE, seat.getStatus());
        assertNull(seat.getBooking());
        verify(seatRepository).save(seat);
    }
}