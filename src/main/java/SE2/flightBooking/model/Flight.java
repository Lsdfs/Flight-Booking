package SE2.flightBooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String departure;
    
    @Column(nullable = false)
    private String destination;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "return_time")
    private LocalDateTime returnTime;
    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String airline;

    @ManyToMany(mappedBy = "flights")
    private List<Booking> bookings = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "flight_type", nullable = false)
    private FlightType flightType;
    public enum FlightType {
        ONE_WAY, ROUND_TRIP
    }

    @ManyToOne
    @JoinColumn(name = "ticket_class_id")
    private TicketClass ticketClass;

    public Flight() {}
    public Flight(String departure, String destination, LocalDateTime departureTime,
                  LocalDateTime returnTime, int availableSeats, double price,
                  String airline, FlightType flightType) {
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.returnTime = returnTime;
        this.availableSeats = availableSeats;
        this.price = price;
        this.airline = airline;
        this.flightType = flightType;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getReturnTime() { return returnTime; }
    public void setReturnTime(LocalDateTime returnTime) { this.returnTime = returnTime; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public FlightType getFlightType() { return flightType; }
    public void setFlightType(FlightType flightType) { this.flightType = flightType; }

    public TicketClass getTicketClass() {
        return ticketClass;
    }

    public void setTicketClass(TicketClass ticketClass) {
        this.ticketClass = ticketClass;
    }
}
