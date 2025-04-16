package SE2.flightBooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_code", nullable = false, unique = true)
    private String reservationCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "passenger_count", nullable = false)
    private int passengerCount;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Column(name = "additional_cost", nullable = false)
    private double additionalCost;

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



    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingMeal> bookingMeals = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingBaggage> bookingBaggages = new ArrayList<>();

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED, PAID, ONEWAYCHECKEDIN, ONEWAYNOTCHECKEDIN,ROUNDTRIPNOTCHECKEDIN,ROUNDTRIPCHECKEDIN, FLIGHTDONE
    }

    // Constructors
    public Booking() {
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
        this.totalPrice = 0.0;
        this.additionalCost = 0.0;
        this.passengerCount = 1;
    }

    public Booking(int passengerCount) {
        this();
        if (passengerCount <= 0) {
            throw new IllegalArgumentException("Number of passengers must be positive.");
        }
        this.passengerCount = passengerCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
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

    public double getAdditionalCost() {
        return additionalCost;
    }

    public void setAdditionalCost(double additionalCost) {
        this.additionalCost = additionalCost;
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
        this.flights = flights != null ? flights : new ArrayList<>();
        calculateTotalPrice();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        this.user = user;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats != null ? seats : new ArrayList<>();
        calculateTotalPrice();
    }

    public List<BookingMeal> getBookingMeals() {
        return bookingMeals;
    }

    public void setBookingMeals(List<BookingMeal> bookingMeals) {
        this.bookingMeals = bookingMeals != null ? bookingMeals : new ArrayList<>();
        calculateTotalPrice();
    }

    public List<BookingBaggage> getBookingBaggages() {
        return bookingBaggages;
    }

    public void setBookingBaggages(List<BookingBaggage> bookingBaggages) {
        this.bookingBaggages = bookingBaggages != null ? bookingBaggages : new ArrayList<>();
        calculateTotalPrice();
    }

    public void addFlight(Flight flight) {
        if (flight != null && !flights.contains(flight)) {
            this.flights.add(flight);
            calculateTotalPrice();
        }
    }

    public void addSeat(Seat seat) {
        if (seat != null && !seats.contains(seat)) {
            this.seats.add(seat);
            seat.setBooking(this);
            calculateTotalPrice();
        }
    }

    public void removeSeat(Seat seat) {
        if (seat != null && seats.remove(seat)) {
            seat.setBooking(null);
            calculateTotalPrice();
        }
    }

    public void addBookingMeal(BookingMeal bookingMeal) {
        if (bookingMeal != null && !bookingMeals.contains(bookingMeal)) {
            this.bookingMeals.add(bookingMeal);
            bookingMeal.setBooking(this);
            calculateTotalPrice();
        }
    }

    public void removeBookingMeal(BookingMeal bookingMeal) {
        if (bookingMeal != null && bookingMeals.remove(bookingMeal)) {
            bookingMeal.setBooking(null);
            calculateTotalPrice();
        }
    }

    public void addBookingBaggage(BookingBaggage bookingBaggage) {
        if (bookingBaggage != null && !bookingBaggages.contains(bookingBaggage)) {
            this.bookingBaggages.add(bookingBaggage);
            bookingBaggage.setBooking(this);
            calculateTotalPrice();
        }
    }

    public void removeBookingBaggage(BookingBaggage bookingBaggage) {
        if (bookingBaggage != null && bookingBaggages.remove(bookingBaggage)) {
            bookingBaggage.setBooking(null);
            calculateTotalPrice();
        }
    }

    public void calculateTotalPrice() {
        double flightTotal = flights.stream().mapToDouble(Flight::getPrice).sum();

        double seatTotal = seats.stream()
                .filter(seat -> seat.getPrice() > 0)
                .mapToDouble(Seat::getPrice)
                .sum();

        double mealTotal = bookingMeals.stream()
                .filter(bm -> !"Standard".equals(bm.getMeal().getCategory()) || isMealChargeable(bm))
                .mapToDouble(bm -> bm.getMeal().getPrice() * bm.getQuantity())
                .sum();

        double baggageTotal = bookingBaggages.stream()
                .filter(bb -> bb.getBaggage().getPricePerKg() > 0)
                .mapToDouble(bb -> {
                    double freeWeight = getFreeBaggageWeightPerPassenger(bb.getFlightId());
                    double excessWeight = Math.max(0, bb.getBaggage().getWeight() - freeWeight);
                    return bb.getBaggage().getPricePerKg() * excessWeight * bb.getQuantity();
                })
                .sum();

        this.totalPrice = flightTotal + seatTotal + mealTotal + baggageTotal + additionalCost;
    }

    private boolean isMealChargeable(BookingMeal bookingMeal) {
        long freeMeals = getFreeMealsForFlight(bookingMeal.getFlightId());
        long usedStandardMeals = bookingMeals.stream()
                .filter(bm -> bm.getFlightId().equals(bookingMeal.getFlightId()))
                .filter(bm -> "Standard".equals(bm.getMeal().getCategory()))
                .count();
        return usedStandardMeals > freeMeals;
    }

    private long getFreeMealsForFlight(Long flightId) {
        return flights.stream()
                .filter(f -> f.getId().equals(flightId))
                .findFirst()
                .map(f -> (long) f.getTicketClass().getFreeMealQuantity() * passengerCount)
                .orElse(0L);
    }

    private double getFreeBaggageWeightPerPassenger(Long flightId) {
        return flights.stream()
                .filter(f -> f.getId().equals(flightId))
                .findFirst()
                .map(f -> f.getTicketClass() != null ? f.getTicketClass().getFreeBaggageWeight() : 0)
                .orElse(0.0);
    }
}