package SE2.flightBooking.service.paymentService;

import SE2.flightBooking.common.constant.ResponseCode;
import SE2.flightBooking.common.exception.BusinessException;
import SE2.flightBooking.repository.BookingRepository;
import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.Flight;
import SE2.flightBooking.model.BookingMeal;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class FlightService {
    @Autowired
    BookingRepository bookingRepository;
    public void calculateTotalPrice(Booking booking, Flight departureFlight, Flight returnFlight) {
        double total = 0.0;

        int passengerCount = booking.getPassengerCount() != null ? booking.getPassengerCount() : 1;

        // Add flight prices
        if (departureFlight != null) {
            total += departureFlight.getPrice() * passengerCount;
        }

        if (returnFlight != null) {
            total += returnFlight.getPrice() * passengerCount;
        }

        // Add meal prices
        if (booking.getMeals() != null) {
            for (BookingMeal meal : booking.getMeals()) {
                total += meal.getMeal().getPrice() * meal.getQuantity();
            }
        }

        // Add seat price
        if (booking.getSeat() != null) {
            total += booking.getSeat().getPrice();
        }

        // Add baggage price
        if (booking.getBaggage() != null) {
            total += booking.getBaggage().getPrice();
        }
        booking.setTotalPrice(total);

    }
    @Transactional
    public void markBookingAsPaid(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BOOKING_NOT_FOUND, "Booking not found."));

        booking.setStatus(Booking.Status.CONFIRMED);
        bookingRepository.save(booking);
    }

}
