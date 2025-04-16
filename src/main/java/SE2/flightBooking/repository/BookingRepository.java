package SE2.flightBooking.repository;

import SE2.flightBooking.model.Booking;
import SE2.flightBooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByStatus(String status);

    @Query("SELECT b FROM Booking b JOIN b.user u WHERE b.reservationCode = :reservationCode AND u.lastName = :lastName AND u.firstName = :firstName")
    Optional<Booking> findByReservationCodeAndUserLastNameAndUserFirstName(
            @Param("reservationCode") String reservationCode,
            @Param("lastName") String lastName,
            @Param("firstName") String firstName
    );
}