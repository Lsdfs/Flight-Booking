package SE2.flightBooking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_baggage")
public class BookingBaggage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "baggage_id")
    private Baggage baggage;

    @Column(name = "flight_id")
    private Long flightId;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    public BookingBaggage() {}

    public BookingBaggage(Booking booking, Baggage baggage, Long flightId, int quantity) {
        this.booking = booking;
        this.baggage = baggage;
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

    public Baggage getBaggage() {
        return baggage;
    }

    public void setBaggage(Baggage baggage) {
        this.baggage = baggage;
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