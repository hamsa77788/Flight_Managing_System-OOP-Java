import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Flight {
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private final List<SeatClass> seatClasses = new ArrayList<>();

    public Flight(String flightNumber, String airline, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime) {
        setFlightNumber(flightNumber);
        setAirline(airline);
        setOrigin(origin);
        setDestination(destination);
        setDepartureTime(departureTime);
        setArrivalTime(arrivalTime);
    }

    // Getters
    public String getFlightNumber() {
        return flightNumber;
    }

    public String getAirline() {
        return airline;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public List<SeatClass> getSeatClasses() {
        return Collections.unmodifiableList(seatClasses);
    }

    // Setters with Validation
    public void setFlightNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number is required");
        }
        this.flightNumber = flightNumber.trim();
    }

    public void setAirline(String airline) {
        if (airline == null || airline.trim().isEmpty()) {
            throw new IllegalArgumentException("Airline name is required");
        }
        this.airline = airline.trim();
    }

    public void setOrigin(String origin) {
        if (origin == null || origin.trim().isEmpty()) {
            throw new IllegalArgumentException("Origin airport is required");
        }
        this.origin = origin.trim();
    }

    public void setDestination(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination airport is required");
        }
        this.destination = destination.trim();
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        if (departureTime == null) {
            throw new IllegalArgumentException("Departure time is required");
        }
        this.departureTime = departureTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        if (arrivalTime == null) {
            throw new IllegalArgumentException("Arrival time is required");
        }
        if (departureTime != null && arrivalTime.isBefore(departureTime)) {
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }
        this.arrivalTime = arrivalTime;
    }

    // Seat Management
    public void addSeatClass(String className, int totalCapacity, double pricePerSeat) {
        seatClasses.add(new SeatClass(className, totalCapacity, pricePerSeat));
    }

    public boolean checkAvailability(String className, int quantity) {
        for (SeatClass seat : seatClasses) {
            if (seat.getClassName().equalsIgnoreCase(className)) {
                return seat.getAvailableSeats() >= quantity;
            }
        }
        return false;
    }


    public boolean reserveSeat(String className, int quantity) {
        for (SeatClass seat : seatClasses) {
            if (seat.getClassName().equalsIgnoreCase(className)) {
                if (seat.getAvailableSeats() < quantity) {
                    System.out.println("❌ Not enough seats available in " + className + " class. Max available: " + seat.getAvailableSeats());
                    return false;
                }
                seat.reserveSeats(quantity);
                System.out.println("✅ " + quantity + " seat(s) reserved in " + className + " class.");
                return true;
            }
        }
        System.out.println("❌ Seat class " + className + " not found.");
        return false;
    }

    public void releaseSeat(String className, int quantity) {
        for (SeatClass seat : seatClasses) {
            if (seat.getClassName().equalsIgnoreCase(className)) {
                seat.releaseSeats(quantity);
                return;
            }
        }
        throw new IllegalArgumentException("Seat class not found");
    }

    public double calculateBasePrice(String className, int quantity) {
        for (SeatClass seat : seatClasses) {
            if (seat.getClassName().equalsIgnoreCase(className)) {
                return seat.calculateTotalPrice(quantity);
            }
        }
        return 0.0;
    }

    public boolean isSeatAvailable(String className, int quantity) {
        for (SeatClass seat : seatClasses) {
            if (seat.getClassName().equalsIgnoreCase(className)) {
                return seat.isSeatAvailable(quantity);
            }
        }
        return false;
    }

    // Default implementation for calculating total price
    public double calculateTotalPrice(String className, int quantity) {
        return calculateBasePrice(className, quantity);
    }

    // Schedule Update
    public void updateSchedule(LocalDateTime newDepartureTime, LocalDateTime newArrivalTime) {
        setDepartureTime(newDepartureTime);
        setArrivalTime(newArrivalTime);
    }

    @Override
    public String toString() {
        return "✈ Flight " + flightNumber + " | " + airline +
                " | " + origin + " → " + destination +
                " | Departure: " + departureTime +
                " | Arrival: " + arrivalTime;
    }
}