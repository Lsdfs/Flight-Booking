package SE2.flightBooking.repository;

import SE2.flightBooking.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT DISTINCT f.departure FROM Flight f")
    List<String> findAllDepartures();

    @Query("SELECT DISTINCT f.destination FROM Flight f")
    List<String> findAllDestinations();

    @Query("SELECT f FROM Flight f WHERE f.departure = :departure " +
            "AND f.destination = :destination " +
            "AND f.flightType = :flightType " +
            "AND f.availableSeats >= :availableSeats " )
    List<Flight> findFlightsByDepartureDestinationAndType(
            @Param("departure") String departure,
            @Param("destination") String destination,
            @Param("flightType") Flight.FlightType flightType,
            @Param("availableSeats") int availableSeats);
}

