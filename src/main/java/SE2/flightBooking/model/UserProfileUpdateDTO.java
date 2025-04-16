package SE2.flightBooking.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class UserProfileUpdateDTO {
    @NotBlank
    @Size(max = 60, message = "First name must be at most 60 characters")
    private String firstName;

    @NotBlank
    @Size(max = 60, message = "Last name must be at most 60 characters")
    private String lastName;

    @Pattern(regexp = "Male|Female|Other", message = "Gender must be either Male or Female or Other")
    private String gender;

    @Past(message = "Date of birth must be a past date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 70, message = "Address must be at most 70 characters")
    private String address;
    @Size(min = 10, max = 20, message = "Passport number must be at most 20 characters")
    private String passportNumber;
    @Size(min = 8, max = 15, message = "Citizen ID must be at most 15 characters")
    private String citizenID;
    public @NotBlank String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCitizenID() {
        return citizenID;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }



    // Getters and setters...
}
