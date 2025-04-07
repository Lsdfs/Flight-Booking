package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatService seatService;
    private final MealService mealService;
    private final BaggageService baggageService;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          SeatService seatService,
                          MealService mealService,
                          BaggageService baggageService) {
        this.bookingRepository = bookingRepository;
        this.seatService = seatService;
        this.mealService = mealService;
        this.baggageService = baggageService;
    }

    @Transactional
    public Booking createBooking(int passengers, List<Flight> flights, User user) {
        if (passengers <= 0) {
            throw new IllegalArgumentException("Number of passengers must be positive.");
        }
        if (flights == null || flights.isEmpty()) {
            throw new IllegalArgumentException("Flights list cannot be null or empty.");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        Booking booking = new Booking(passengers);
        booking.setUser(user);
        flights.forEach(booking::addFlight);
        booking.calculateTotalPrice();
        return bookingRepository.save(booking);
    }

    @Transactional
    public void updateBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null.");
        }
        booking.calculateTotalPrice();
        bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null.");
        }
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled.");
        }

        List<Seat> seats = new ArrayList<>(booking.getSeats());
        for (Seat seat : seats) {
            seatService.releaseSeat(booking, seat);
        }

        List<BookingMeal> bookingMeals = new ArrayList<>(booking.getBookingMeals());
        for (BookingMeal bm : bookingMeals) {
            mealService.removeMealFromBooking(booking, bm);
        }

        List<BookingBaggage> bookingBaggages = new ArrayList<>(booking.getBookingBaggage());
        for (BookingBaggage bl : bookingBaggages) {
            baggageService.removeBaggageFromBooking(booking, bl);
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long bookingId) {
        if (bookingId == null) {
            return Optional.empty();
        }
        return bookingRepository.findById(bookingId);
    }

    public Optional<Booking> findByReservationCodeAndNames(String reservationCode, String lastName, String firstName) {
        if (reservationCode == null || lastName == null || firstName == null) {
            return Optional.empty();
        }
        return bookingRepository.findByReservationCodeAndUserLastNameAndUserFirstName(
                reservationCode, lastName, firstName);
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);

        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking is already confirmed");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot confirm cancelled booking");
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
}