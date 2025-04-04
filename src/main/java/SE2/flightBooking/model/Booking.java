package SE2.flightBooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "passenger_count", nullable = false)
    private int passengerCount;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @ManyToMany
    @JoinTable(
            name = "booking_flights",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "flight_id")
    )
    private List<Flight> flights = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingMeal> bookingMeals = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingBaggage> bookingBaggage = new ArrayList<>();

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, PAID
    }

    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
    }

    public Booking(int passengerCount) {
        this();
        this.passengerCount = passengerCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public List<BookingMeal> getBookingMeals() {
        return bookingMeals;
    }

    public void setBookingMeals(List<BookingMeal> bookingMeals) {
        this.bookingMeals = bookingMeals;
    }

    public List<BookingBaggage> getBookingBaggage() {
        return bookingBaggage;
    }

    public void setBookingBaggage(List<BookingBaggage> bookingBaggage) {
        this.bookingBaggage = bookingBaggage;
    }

    public void addFlight(Flight flight) {
        this.flights.add(flight);
    }

    public void addSeat(Seat seat) {
        this.seats.add(seat);
    }

    public void addMeal(Meal meal, Long flightId, int quantity) {
        BookingMeal bookingMeal = new BookingMeal(this, meal, flightId, quantity);
        this.bookingMeals.add(bookingMeal);
    }

    public void addBaggage(Baggage baggage, Long flightId, int quantity) {
        BookingBaggage bookingBaggage = new BookingBaggage(this, baggage, flightId, quantity);
        this.bookingBaggage.add(bookingBaggage);
    }

    public void calculateTotalPrice() {
        double flightTotal = flights.stream().mapToDouble(Flight::getPrice).sum();
        double seatTotal = seats.stream().mapToDouble(Seat::getPrice).sum();
        double mealTotal = bookingMeals.stream()
                .mapToDouble(bm -> bm.getMeal().getPrice() * bm.getQuantity())
                .sum();
        double baggageTotal = bookingBaggage.stream()
                .mapToDouble(bl -> bl.getBaggage().getPrice() * bl.getQuantity())
                .sum();
        this.totalPrice = flightTotal + seatTotal + mealTotal + baggageTotal;
    }
}