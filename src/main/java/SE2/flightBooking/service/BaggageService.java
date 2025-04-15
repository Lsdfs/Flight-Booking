package SE2.flightBooking.service;

import SE2.flightBooking.model.*;
import SE2.flightBooking.repository.BaggageRepository;
import SE2.flightBooking.repository.BookingBaggageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BaggageService {
    private static final Logger logger = LoggerFactory.getLogger(BaggageService.class);

    @Autowired
    private BaggageRepository baggageRepository;

    @Autowired
    private BookingBaggageRepository bookingBaggageRepository;

    public List<Baggage> getAllBaggageOptions() {
        return baggageRepository.findAll();
    }

    public Optional<Baggage> findById(Long baggageId) {
        return baggageRepository.findById(baggageId);
    }

    public Optional<Baggage> findFirstByCategory(String category) {
        return baggageRepository.findFirstByCategory(category);
    }

    @Transactional
    public BookingBaggage createBookingBaggage(Booking booking, Baggage baggage, Long flightId, int quantity) {
        BookingBaggage bookingBaggage = new BookingBaggage(booking, baggage, flightId, quantity);
        return bookingBaggageRepository.save(bookingBaggage);
    }

    @Transactional
    public void updateBookingBaggageQuantity(Long bookingBaggageId, int newQuantity) {
        BookingBaggage bookingBaggage = bookingBaggageRepository.findById(bookingBaggageId)
                .orElseThrow(() -> new IllegalArgumentException("BookingBaggage not found"));
        bookingBaggage.setQuantity(newQuantity);
        bookingBaggageRepository.save(bookingBaggage);
    }

    @Transactional
    public void deleteBookingBaggage(Long bookingBaggageId) {
        bookingBaggageRepository.deleteById(bookingBaggageId);
    }

    public List<BookingBaggage> findBookingBaggagesByBookingAndFlight(Booking booking, Long flightId) {
        return bookingBaggageRepository.findByBookingAndFlightId(booking, flightId);
    }

    public double calculateTotalWeightForFlight(Booking booking, Long flightId) {
        return bookingBaggageRepository.findByBookingAndFlightId(booking, flightId).stream()
                .mapToDouble(bb -> bb.getBaggage().getWeight() * bb.getQuantity())
                .sum();
    }

    public double calculateAdditionalBaggageCost(Booking booking, Long flightId) {
        Optional<Flight> flightOpt = booking.getFlights().stream()
                .filter(f -> f.getId().equals(flightId))
                .findFirst();

        if (flightOpt.isEmpty()) return 0.0;

        double freeWeightPerPassenger = flightOpt.get().getTicketClass().getFreeBaggageWeight();
        double totalFreeWeight = freeWeightPerPassenger * booking.getPassengerCount();
        double totalUsedWeight = calculateTotalWeightForFlight(booking, flightId);
        double excessWeight = Math.max(0, totalUsedWeight - totalFreeWeight);

        return bookingBaggageRepository.findByBookingAndFlightId(booking, flightId).stream()
                .mapToDouble(bb -> {
                    double baggageWeight = bb.getBaggage().getWeight() * bb.getQuantity();
                    double ratio = baggageWeight / totalUsedWeight;
                    return bb.getBaggage().getPricePerKg() * excessWeight * ratio;
                })
                .sum();
    }
}