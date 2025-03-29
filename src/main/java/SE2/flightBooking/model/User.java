package SE2.flightBooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "user")
    private List<CoflightMember> coflightMembers;

    @NotBlank(message = "Phone number is mandatory")
    @Size(max=20, message = "Phone number must be at most 20 characters")
    @Pattern(regexp = "\\d{10,}", message = "Phone number must be at least 10 digits, no character allowed")
    @Column(unique=true)
    private String phoneNumber;


    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 70, message = "Address must be at most 70 characters")
    private String address;

    @Size(max = 60, message = "First name must be at most 60 characters")
    private String firstName;

    @Size(max = 60, message = "Last name must be at most 60 characters")
    private String lastName;

    @Pattern(regexp = "^$|.{9,20}", message = "Passport number must be between 9 and 20 characters or empty")
    private String passportNumber;

    @Pattern(regexp = "^$|.{8,15}", message = "Citizen ID must be between 8 and 15 characters or empty")
    private String citizenID;

    @Past(message = "Date of birth must be a past date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;


    @Pattern(regexp = "Male|Female|Other", message = "Gender must be either Male or Female or Other")
    private String gender;


    @NotBlank(message = "Gmail is mandatory")
    @Column(unique=true)
    private String gmail;


    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleType role;

    /// Contructor rỗng
    public User() {
    }

    public void setCoflightMembers(List<CoflightMember> coflightMembers) {
        this.coflightMembers = coflightMembers;
    }

    public List<CoflightMember> getCoflightMembers() {
        return coflightMembers;
    }

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
