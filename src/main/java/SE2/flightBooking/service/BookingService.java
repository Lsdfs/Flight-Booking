package SE2.flightBooking.service;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.model.User;
import SE2.flightBooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    private MealService mealService;

    @Autowired
    private BaggageService baggageService;

    @Transactional
    public Booking createBooking(int passengers, List<Flight> flights, User user) {
        Booking booking = new Booking(passengers);
        flights.forEach(booking::addFlight);
        booking.calculateTotalPrice();
        return bookingRepository.save(booking);
    }

    @Transactional
    public void updateBooking(Booking booking) {
        booking.calculateTotalPrice();
        bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Booking booking) {
        for (var seat : booking.getSeats()) {
            seatService.releaseSeat(booking, seat);
        }
        for (var bm : booking.getBookingMeals()) {
            mealService.removeMealFromBooking(booking, bm);
        }
        for (var bl : booking.getBookingBaggage()) {
            baggageService.removeBaggageFromBooking(booking, bl);
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }
}
