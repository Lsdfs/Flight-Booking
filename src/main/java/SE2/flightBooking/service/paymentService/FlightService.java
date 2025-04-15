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
    @Transactional
    public void markBookingAsPaid(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ResponseCode.BOOKING_NOT_FOUND, "Booking not found."));

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

}
