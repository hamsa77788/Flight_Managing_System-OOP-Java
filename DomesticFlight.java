import java.time.LocalDateTime;

public class DomesticFlight extends Flight {
    public DomesticFlight(String flightNumber, String airline, String origin, String destination,
                          LocalDateTime departureTime, LocalDateTime arrivalTime) {
        super(flightNumber, airline, origin, destination, departureTime, arrivalTime);
    }

    // No need to override calculateTotalPrice as it uses the default implementation from Flight
}
