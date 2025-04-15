package SE2.flightBooking.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "faq_category")
public class FaqCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FaqQuestion> questions;

    // Getters và Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FaqQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<FaqQuestion> questions) {
        this.questions = questions;
    }
}

