package SE2.flightBooking.repository;

import SE2.flightBooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find a payment by its ID
    Optional<Payment> findById(Long paymentId);

    List<Payment> findByReservationCode(String reservationCode);

    List<Payment> findByPaymentStatus(String paymentStatus);
}
