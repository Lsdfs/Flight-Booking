package SE2.flightBooking.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "ticket_classes")
public class TicketClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Seat.SeatClass freeSeatClass;
    private int freeMealQuantity;
    private double freeBaggageWeight;
    private double basePriceMultiplier;

    @OneToMany(mappedBy = "ticketClass")
    private List<Flight> flights;

    public TicketClass() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Seat.SeatClass getFreeSeatClass() {
        return freeSeatClass;
    }

    public void setFreeSeatClass(Seat.SeatClass freeSeatClass) {
        this.freeSeatClass = freeSeatClass;
    }

    public int getFreeMealQuantity() {
        return freeMealQuantity;
    }

    public void setFreeMealQuantity(int freeMealQuantity) {
        this.freeMealQuantity = freeMealQuantity;
    }

    public double getFreeBaggageWeight() {
        return freeBaggageWeight;
    }

    public void setFreeBaggageWeight(double freeBaggageWeight) {
        this.freeBaggageWeight = freeBaggageWeight;
    }

    public double getBasePriceMultiplier() {
        return basePriceMultiplier;
    }

    public void setBasePriceMultiplier(double basePriceMultiplier) {
        this.basePriceMultiplier = basePriceMultiplier;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}