package SE2.flightBooking.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class UserDTO {

    private Integer id;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 60, message = "First name must be at most 60 characters")
    private String firstName;

    @Size(max = 60, message = "Last name must be at most 60 characters")
    private String lastName;

    @Past(message = "Date of birth must be a past date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "Male|Female|Other", message = "Gender must be either Male or Female or Other")
    private String gender;

    @Size(max = 70, message = "Address must be at most 70 characters")
    private String address;

    @NotBlank(message = "Phone number is mandatory")
    @Size(max=20, message = "Phone number must be at most 20 characters")
    @Pattern(regexp = "\\d{10,}", message = "Phone number must be at least 10 digits")
    private String phoneNumber;

    @Size(min = 10, max = 20, message = "Passport number must be at most 20 characters")
    private String passportNumber;

    @Size(min = 8, max = 15, message = "Citizen ID must be at most 15 characters")
    private String citizenID;


    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getCitizenID() {
        return citizenID;
    }

    private String gmail;

    private String role;

    public UserDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }
}
