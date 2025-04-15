package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SeatService seatService;
    @Autowired
    private MealService mealService;
    @Autowired
    private BaggageService baggageService;

    @Transactional
    public Booking createBooking(int passengerCount, List<Flight> flights, User user) {
        if (flights == null || flights.isEmpty()) {
            throw new IllegalArgumentException("Flights list cannot be null or empty");
        }
        if (passengerCount <= 0) {
            throw new IllegalArgumentException("Number of passengers must be positive");
        }

        Booking booking = new Booking(passengerCount);
        booking.setFlights(flights);
        booking.setUser(user);
        booking.setReservationCode(generateReservationCode());
        booking.setStatus(Booking.BookingStatus.PENDING);

        bookingRepository.save(booking);
        applyDefaultFreeServices(booking);

        logger.info("Created booking {} for {} passengers", booking.getId(), passengerCount);
        return booking;
    }

    @Transactional
    public Booking confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void updateBooking(Booking booking) {
        applyDefaultFreeServices(booking);
        booking.calculateTotalPrice();
        bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public Optional<Booking> findByReservationCodeAndNames(String code, String lastName, String firstName) {
        return bookingRepository.findByReservationCodeAndUserLastNameAndUserFirstName(code, lastName, firstName);
    }

    private String generateReservationCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void applyDefaultFreeServices(Booking booking) {
        if (booking.getFlights() == null || booking.getFlights().isEmpty()) return;

        for (Flight flight : booking.getFlights()) {
            // Áp dụng ghế miễn phí
            assignDefaultSeats(booking, flight);

            // Áp dụng bữa ăn miễn phí
            assignDefaultMeals(booking, flight);

            // Áp dụng hành lý miễn phí
            assignDefaultBaggage(booking, flight);
        }
    }

    private void assignDefaultSeats(Booking booking, Flight flight) {
        long assignedSeats = booking.getSeats().stream()
                .filter(s -> s.getFlight().equals(flight))
                .count();

        if (assignedSeats < booking.getPassengerCount()) {
            seatService.getAvailableSeatsForFlight(flight).stream()
                    .limit(booking.getPassengerCount() - assignedSeats)
                    .forEach(seat -> seatService.assignSeatToBooking(booking, seat, flight));
        }
    }

    private void assignDefaultMeals(Booking booking, Flight flight) {
        mealService.findFirstByCategory("Standard").ifPresent(standardMeal -> {
            long assignedMeals = booking.getBookingMeals().stream()
                    .filter(bm -> bm.getFlightId().equals(flight.getId()))
                    .filter(bm -> "Standard".equals(bm.getMeal().getCategory()))
                    .count();

            if (assignedMeals < booking.getPassengerCount()) {
                int mealsToAdd = (int) (booking.getPassengerCount() - assignedMeals);
                mealService.createBookingMeal(booking, standardMeal, flight.getId(), mealsToAdd);
            }
        });
    }

    private void assignDefaultBaggage(Booking booking, Flight flight) {
        baggageService.findFirstByCategory("Standard").ifPresent(standardBaggage -> {
            double freeWeight = flight.getTicketClass().getFreeBaggageWeight() * booking.getPassengerCount();
            double currentWeight = baggageService.calculateTotalWeightForFlight(booking, flight.getId());

            if (currentWeight < freeWeight) {
                int baggageToAdd = (int) ((freeWeight - currentWeight) / standardBaggage.getWeight());
                baggageService.createBookingBaggage(booking, standardBaggage, flight.getId(), baggageToAdd);
            }
        });
    }
}