import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

class Agent extends User {
    private String agentId;
    private String department;
    private double commission;
    private BookingSystem bookingSystem;

    public Agent(String userId,
                 String username,
                 String password,
                 String name,
                 String email,
                 String contactInfo,
                 String agentId,
                 String department,
                 double commission,
                 BookingSystem bookingSystem) {
        super(userId, username, password, name, email, contactInfo);

        if (agentId == null || agentId.trim().isEmpty())
            throw new IllegalArgumentException("Agent ID is required.");
        if (department == null || department.trim().isEmpty())
            throw new IllegalArgumentException("Department is required.");
        if (commission < 0)
            throw new IllegalArgumentException("Commission cannot be negative.");
        if (bookingSystem == null)
            throw new IllegalArgumentException("Booking system is required.");

        this.agentId = agentId.trim();
        this.department = department.trim();
        this.commission = commission;
        this.bookingSystem = bookingSystem;
    }

    public void manageFlights(Flight flight, String action) {
        if (!isActive()) {
            System.out.println("❌ Account is not active. Cannot perform the action.");
            return;
        }

        // Check if the flight number is valid
        if (flight.getFlightNumber() == null || flight.getFlightNumber().isEmpty()) {
            System.out.println("❌ Invalid flight number. Flight number cannot be empty.");
            return;
        }

        // Check if the origin and destination are provided
        if (flight.getOrigin() == null || flight.getOrigin().isEmpty() || flight.getDestination() == null || flight.getDestination().isEmpty()) {
            System.out.println("❌ Origin and Destination cannot be empty.");
            return;
        }

        // Validate the date format for departure and arrival times
        try {
            LocalDateTime depTime = LocalDateTime.parse(flight.getDepartureTime().toString());
            LocalDateTime arrTime = LocalDateTime.parse(flight.getArrivalTime().toString());

            // Ensure arrival time is not before departure time
            if (arrTime.isBefore(depTime)) {
                System.out.println("❌ Arrival time cannot be before departure time.");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format. Please enter the dates in the format yyyy-MM-ddTHH:mm.");
            return;
        }

        // Validate action input (add, update, remove)
        if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("update") && !action.equalsIgnoreCase("remove")) {
            System.out.println("❌ Invalid action. Please use 'add', 'update', or 'remove'.");
            return;
        }

        List<Flight> flights = bookingSystem.getFlights();

        switch (action.toLowerCase()) {
            case "add":
                if (bookingSystem.getFlightByNumber(flight.getFlightNumber()) != null) {
                    System.out.println("❌ Flight already exists.");
                } else {
                    flights.add(flight);
                    System.out.println("✔ Flight added successfully: " + flight.getFlightNumber());
                }
                break;

            case "remove":
                Flight toRemove = bookingSystem.getFlightByNumber(flight.getFlightNumber());
                if (toRemove != null) {
                    flights.remove(toRemove);
                    System.out.println("✔ Flight removed successfully: " + flight.getFlightNumber());
                } else {
                    System.out.println("❌ Flight not found.");
                }
                break;

            case "update":
                Flight toUpdate = bookingSystem.getFlightByNumber(flight.getFlightNumber());
                if (toUpdate != null) {
                    toUpdate.updateSchedule(flight.getDepartureTime(), flight.getArrivalTime());
                    System.out.println("✔ Flight schedule updated successfully.");
                } else {
                    System.out.println("❌ Flight not found to update.");
                }
                break;

            default:
                System.out.println("❌ Unknown action. Use 'add', 'update', or 'remove'.");
        }

        bookingSystem.setFlights(flights); // Save the updated flights list
        bookingSystem.shutdown(); // Save the changes
    }


    public Booking createBookingForCustomer(Customer customer,
                                            Flight flight,
                                            String seatClass,
                                            List<Passenger> passengers) throws Exception {
        if (!isActive())
            throw new IllegalStateException("Agent account is inactive.");

        if (customer == null || flight == null || passengers == null || passengers.isEmpty()) {
            throw new IllegalArgumentException("Booking data is incomplete.");
        }

        if (!flight.isSeatAvailable(seatClass, passengers.size())) {
            throw new Exception("Not enough seats available in class " + seatClass);
        }

        String ref = "BK" + System.currentTimeMillis();
        SeatSelection selection = new SeatSelection(seatClass, passengers.size());
        Booking booking = new Booking(ref, customer, flight, selection);
        booking.setPassengers(passengers);

        if (flight.reserveSeat(seatClass, passengers.size())) {
            bookingSystem.getBookings().add(booking);
            System.out.println("Booking created. Reference: " + ref);
            return booking;
        } else {
            throw new Exception("Failed to reserve seats.");
        }
    }

    public void modifyBooking(String bookingRef, List<Passenger> newPassengers) {
        if (!isActive()) {
            System.out.println("Agent account is inactive.");
            return;
        }

        Booking booking = bookingSystem.findBookingByReference(bookingRef);
        if (booking != null) {
            booking.setPassengers(newPassengers);
            System.out.println("Booking passengers updated: " + bookingRef);
        } else {
            System.out.println("Booking not found.");
        }
    }

    public void generateReports() {
        if (!isActive()) {
            System.out.println(" Account is not active.");
            return;
        }

        int totalBookings = 0;
        double totalRevenue = 0.0;

        for (Booking booking : bookingSystem.getBookings()) {
            // تأكد من أن الحجز ليس ملغى
            if (!booking.getStatus().equalsIgnoreCase("CANCELLED")) {
                totalBookings++;

                // إذا تم تأكيد الدفع، قم بحساب الإيرادات
                for (Payment payment : bookingSystem.getPayments()) {
                    if (payment.getBooking().getBookingReference().equals(booking.getBookingReference()) && payment.getStatus().equals(Payment.STATUS_CONFIRMED)) {
                        totalRevenue += payment.getAmount();
                    }
                }
            }
        }

        System.out.println("Agent Report:");
        System.out.println("Total bookings: " + totalBookings);
        System.out.println("Total revenue: " + totalRevenue + " EGP");
        System.out.println("====================");
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        if (agentId == null || agentId.trim().isEmpty())
            throw new IllegalArgumentException("Agent ID is required.");
        this.agentId = agentId.trim();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty())
            throw new IllegalArgumentException("Department is required.");
        this.department = department.trim();
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        if (commission < 0)
            throw new IllegalArgumentException("Commission cannot be negative.");
        this.commission = commission;
    }

    @Override
    public boolean login(String username, String password) {
        return isActive() && getUsername().equals(username) && getPassword().equals(password);
    }

    @Override
    public void logout() {
        System.out.println("Logged out successfully.");
    }
}
