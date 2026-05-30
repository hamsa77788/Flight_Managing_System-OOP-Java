import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Booking {
    private String bookingReference;
    private Customer customer;
    private Flight flight;
    private List<Passenger> passengers = new ArrayList<>();
    private List<SeatSelection> seatSelections = new ArrayList<>();
    private String status;
    private String paymentStatus;
    //removed private int seatQuantity;

    private static final String STATUS_RESERVED = "RESERVED";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String PAYMENT_PENDING = "PENDING";
    private static final String PAYMENT_CONFIRMED = "CONFIRMED";
    private static final String PAYMENT_FAILED = "FAILED";

    //
    public Booking(String bookingReference, Customer customer, Flight flight, SeatSelection seatSelection) {
        setBookingReference(bookingReference);
        setCustomer(customer);
        setFlight(flight);
        this.status = STATUS_RESERVED;
        this.paymentStatus = PAYMENT_PENDING;
        this.seatSelections.add(seatSelection); // نضيف الـ seat selection بدل seatQuantity
    }

    // Constructor
    public Booking(String bookingReference, Customer customer, Flight flight) {
        this.bookingReference = bookingReference;
        this.customer = customer;
        this.flight = flight;
        this.status = "Pending";  // يمكن تحديد الحالة بشكل افتراضي أو تخصيصها
        this.passengers = new ArrayList<>();  // إذا كنت ستضيف الركاب لاحقًا

    }


    // Getters
    public String getBookingReference() { return bookingReference; }
    //removed public int getSeatQuantity() { return seatQuantity; }
    public Customer getCustomer() { return customer; }
    public Flight getFlight() { return flight; }
    public List<Passenger> getPassengers() { return Collections.unmodifiableList(passengers); }
    public List<SeatSelection> getSeatSelections() { return Collections.unmodifiableList(seatSelections); }
    public String getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }

    // Setters with validation
    public void setStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        this.status = status;
    }

    public void setBookingReference(String bookingReference) {
        if (bookingReference == null || bookingReference.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking reference cannot be empty");
        }
        this.bookingReference = bookingReference;
    }

    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
    }

    public void setFlight(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }
        this.flight = flight;
    }

    public void setPaymentStatus(String paymentStatus) {
        if (!paymentStatus.equals(PAYMENT_PENDING) &&
                !paymentStatus.equals(PAYMENT_CONFIRMED) &&
                !paymentStatus.equals(PAYMENT_FAILED)) {
            throw new IllegalArgumentException("Invalid payment status");
        }
        this.paymentStatus = paymentStatus;
    }

    public void setPassengers(List<Passenger> passengers) {
        if (passengers == null) {
            throw new IllegalArgumentException("Passenger list cannot be null");
        }
        this.passengers = new ArrayList<>(passengers);
    }

    // Business methods
    public void addPassenger(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null");
        }
        passengers.add(passenger);
    }

    public void selectSeatClass(String className,  int quantity) {
        if (!flight.checkAvailability(className,  quantity)) {
            throw new IllegalStateException("Seats not available");
        }
        flight.reserveSeat(className,  quantity);
        seatSelections.add(new SeatSelection(className,  quantity));
    }

    public double calculateTotalPrice() {
        return seatSelections.stream()
                .mapToDouble(sel -> flight.calculateTotalPrice(sel.getClassName(),  sel.getQuantity()))
                .sum();
    }

    public void confirmBooking() {
        if (!paymentStatus.equals(PAYMENT_CONFIRMED)) {
            throw new IllegalStateException("Payment not confirmed");
        }
        this.status = STATUS_CONFIRMED;
    }

    public void cancelBooking() {
        if (status.equals(STATUS_CANCELLED)) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        seatSelections.forEach(sel -> flight.releaseSeat(sel.getClassName(),  sel.getQuantity()));
        this.status = STATUS_CANCELLED;
    }

    public void printItinerary() {
        if (this.status.equalsIgnoreCase("CANCELLED")) {
            System.out.println("This booking is cancelled.");
            return;  // إذا كان الحجز ملغى، توقف عن الطباعة
        }

        System.out.println("=== Itinerary ===");
        System.out.println("Booking Ref: " + bookingReference);
        System.out.println("Customer: " + customer.getName());
        System.out.println("Flight: " + flight.getFlightNumber() + " [" + flight.getOrigin() + " → " + flight.getDestination() + "]");
        System.out.println("Departure: " + flight.getDepartureTime());
        System.out.println("Arrival: " + flight.getArrivalTime());
        System.out.println("Status: " + status);
        System.out.println("Payment Status: " + paymentStatus);
        System.out.println("Passengers: ");
        passengers.forEach(p -> System.out.println(" - " + p.getName() + " (" + p.getPassportNumber() + ")"));
        System.out.println("Seat Selections: ");
        seatSelections.forEach(sel -> System.out.println(" - " + sel.getClassName() + ": " + sel.getQuantity() + " seats"));
        System.out.println("Total Price: " + calculateTotalPrice());
        System.out.println("=================");
    }


    public boolean isCancelled() {
        return status != null && status.equalsIgnoreCase("CANCELLED");
    }

}





