package SE2.flightBooking.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingMeal> bookingMeals = new ArrayList<>();

    public Meal() {}

    // Constructors
    public Meal(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<BookingMeal> getBookingMeals() {
        return bookingMeals;
    }

    public void setBookingMeals(List<BookingMeal> bookingMeals) {
        this.bookingMeals = bookingMeals;
    }

    public void addBookingMeal(BookingMeal bookingMeal) {
        bookingMeals.add(bookingMeal);
        bookingMeal.setMeal(this);
    }

    public void removeBookingMeal(BookingMeal bookingMeal) {
        bookingMeals.remove(bookingMeal);
        bookingMeal.setMeal(null);
    }
}