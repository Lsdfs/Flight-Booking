package SE2.flightBooking.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "coflight_members")
public class CoflightMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    User user;

    @Size(max=20, message = "Phone number must be at most 20 characters")
    @Pattern(regexp = "\\d{10,}", message = "Phone number must be at least 10 digits, no character allowed")
    @NotBlank
    private String phoneNumber;


    @Size(max = 60, message = "First name must be at most 60 characters")
    @NotBlank
    private String firstName;

    @Size(max = 60, message = "Last name must be at most 60 characters")
    @NotBlank
    private String lastName;

    @Pattern(regexp = "^$|.{9,20}", message = "Passport number must be between 9 and 20 characters or empty")
    private String passportNumber;

    @Pattern(regexp = "^$|.{8,15}", message = "Citizen ID must be between 8 and 15 characters or empty")
    private String citizenID;

    @Past(message = "Date of birth must be a past date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;


    @Pattern(regexp = "Male|Female|Other", message = "Gender must be either Male or Female or Other")
    @NotNull
    private String gender;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getCitizenID() {
        return citizenID;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }



}
