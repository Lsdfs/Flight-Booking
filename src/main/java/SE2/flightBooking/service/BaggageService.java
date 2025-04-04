package SE2.flightBooking.service;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.BookingBaggage;
import SE2.flightBooking.model.Baggage;
import SE2.flightBooking.repository.BaggageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BaggageService {

    @Autowired
    private BaggageRepository baggageRepository;

    public List<Baggage> getAllBaggageOptions() {
        return baggageRepository.findAll();
    }

    @Transactional
    public void addBaggageToBooking(Booking booking, Baggage baggage, Long flightId, int quantity) {
        if (booking == null || baggage == null) {
            throw new IllegalArgumentException("Booking and Baggage cannot be null.");
        }
        if (baggage.getStock() < quantity) {
            throw new IllegalStateException("Not found baggage.");
        }
        baggage.setStock(baggage.getStock() - quantity);
        baggageRepository.save(baggage);

        booking.addBaggage(baggage, flightId, quantity);
    }

    @Transactional
    public void removeBaggageFromBooking(Booking booking, BookingBaggage bookingBaggage) {
        if (booking == null || bookingBaggage == null) {
            throw new IllegalArgumentException("Booking and Baggage cannot be null.");
        }

        Baggage baggage = bookingBaggage.getBaggage();
        baggage.setStock(baggage.getStock() + bookingBaggage.getQuantity());
        baggageRepository.save(baggage);
        booking.getBookingBaggage().remove(bookingBaggage);
    }

    public Optional<Baggage> findById(Long baggageId) {
        if (baggageId == null) {
            return Optional.empty();
        }
        return baggageRepository.findById(baggageId);
    }
}