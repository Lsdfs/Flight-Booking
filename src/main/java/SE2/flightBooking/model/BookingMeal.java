package SE2.flightBooking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_meal")
public class BookingMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @Column(name = "flight_id")
    private Long flightId;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    public BookingMeal() {}

    public BookingMeal(Booking booking, Meal meal, Long flightId, int quantity) {
        this.booking = booking;
        this.meal = meal;
        this.flightId = flightId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}