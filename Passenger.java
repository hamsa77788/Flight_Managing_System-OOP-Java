import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Passenger {
    private int passengerId;
    private String name;
    private String passportNumber;
    private LocalDateTime dateOfBirth; // تغيير هنا لاستخدام LocalDateTime
    private String specialRequests;

    public Passenger(int passengerId, String name, String passportNumber,
                     LocalDateTime dateOfBirth, String specialRequests) {
        setPassengerId(passengerId);
        setName(name);
        setPassportNumber(passportNumber);
        setDateOfBirth(dateOfBirth);
        setSpecialRequests(specialRequests);
    }

    // Getters
    public int getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public LocalDateTime getDateOfBirth() { // استخدام LocalDateTime
        return dateOfBirth;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    // Setters with validation
    public void setPassengerId(int passengerId) {
        if (passengerId <= 0) {
            throw new IllegalArgumentException("Passenger ID must be positive");
        }
        this.passengerId = passengerId;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be empty");
        }
        this.name = name.trim();
    }

    public void setPassportNumber(String passportNumber) {
        if (passportNumber == null || passportNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Passport number is required");
        }
        this.passportNumber = passportNumber.trim();
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }
        this.dateOfBirth = dateOfBirth;
    }


    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = (specialRequests == null || specialRequests.trim().isEmpty())
                ? "None"
                : specialRequests.trim();
    }

    // Helper methods
    public void updateInfo(String name, String passportNumber,
                           LocalDateTime dateOfBirth, String specialRequests) {
        setName(name);
        setPassportNumber(passportNumber);
        setDateOfBirth(dateOfBirth);
        setSpecialRequests(specialRequests);
    }

    public String getPassengerDetails() {
        return "Passenger ID: " + passengerId + "\n" +
                "Name: " + name + "\n" +
                "Passport Number: " + passportNumber + "\n" +
                "Date of Birth: " + dateOfBirth + "\n" +
                "Special Requests: " + specialRequests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return passengerId == passenger.passengerId &&
                Objects.equals(passportNumber, passenger.passportNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passengerId, passportNumber);
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId=" + passengerId +
                ", name='" + name + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", specialRequests='" + specialRequests + '\'' +
                '}';
    }
}
