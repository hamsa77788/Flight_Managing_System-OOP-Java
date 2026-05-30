import java.time.LocalDateTime;

public class InternationalFlight extends Flight {
    private static final double INTERNATIONAL_SURCHARGE = 0.10; // 10%

    public InternationalFlight(String flightNumber, String airline, String origin, String destination,
                               LocalDateTime departureTime, LocalDateTime arrivalTime) {
        super(flightNumber, airline, origin, destination, departureTime, arrivalTime);
    }

    @Override
    public double calculateTotalPrice(String className, int quantity) {
        double basePrice = calculateBasePrice(className, quantity);
        return basePrice + (basePrice * INTERNATIONAL_SURCHARGE);
    }
}