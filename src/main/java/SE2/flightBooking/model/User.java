package SE2.flightBooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotBlank(message = "Phone number is mandatory")
    @Column(unique=true)
    private String phoneNumber;


    @NotBlank(message = "Password is mandatory")
    private String password;

    @Size(max = 100, message = "Address must be at most 70 characters")
    private String address;

    @Size(max = 60, message = "First name must be at most 60 characters")
    private String firstName;

    @Size(max = 60, message = "Last name must be at most 60 characters")
    private String lastName;

    @Size(min = 10, max = 20, message = "Passport number must be at most 20 characters")
    private String passportNumber;

    @Size(min = 8, max = 15, message = "Citizen ID must be at most 15 characters")
    private String citizenID;

    @Past(message = "Date of birth must be a past date")
    private LocalDate dateOfBirth;

//    Vẫn còn thiếu biến boolean isEnabled
//    nên bỏ age đi vì nó có thể suy ra từ năm sinh
    private Integer age;

    @Pattern(regexp = "Male|Female|Other", message = "Gender must be either Male or Female or Other")
    private String gender;

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
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

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getCitizenID() {
        return citizenID;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @NotBlank(message = "Gmail is mandatory")
    @Column(unique=true)
    private String gmail;


    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType role;

    /// Contructor rỗng
    public User() {
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }
}
