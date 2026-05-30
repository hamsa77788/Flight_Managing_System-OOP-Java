import java.io.IOException;
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



    public void manageFlights(BookingSystem bookingSystem, Scanner scanner) {
        int choice = -1;
        do {
            System.out.println("\n===== Manage Flights =====");
            System.out.println("1. View All Flights");
            System.out.println("2. Add New Flight");
            System.out.println("3. Update Existing Flight");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        System.out.println("\n--- All Flights ---");
                        if (bookingSystem.getFlights().isEmpty()) {
                            System.out.println("No flights available.");
                        } else {
                            for (Flight flight : bookingSystem.getFlights()) {
                                System.out.println(flight);
                            }
                        }
                        break;

                    case 2:
                        try {
                            System.out.println("\n--- Add New Flight ---");

                            System.out.print("Flight Number: ");
                            String flightNumber = scanner.nextLine();

                            // Check if flight number already exists
                            boolean exists = bookingSystem.getFlights().stream()
                                    .anyMatch(f -> f.getFlightNumber().equals(flightNumber));
                            if (exists) {
                                System.out.println("❌ Flight already exists with this number.");
                                break;
                            }

                            System.out.print("Airline: ");
                            String airline = scanner.nextLine();

                            System.out.print("Origin: ");
                            String origin = scanner.nextLine();

                            System.out.print("Destination: ");
                            String destination = scanner.nextLine();

                            System.out.print("Departure Time (yyyy-MM-ddTHH:mm): ");
                            LocalDateTime departureTime;
                            try {
                                departureTime = LocalDateTime.parse(scanner.nextLine().trim());
                            } catch (DateTimeParseException e) {
                                System.out.println("❌ Invalid departure time format. Use yyyy-MM-ddTHH:mm");
                                break;
                            }

                            System.out.print("Arrival Time (yyyy-MM-ddTHH:mm): ");
                            LocalDateTime arrivalTime;
                            try {
                                arrivalTime = LocalDateTime.parse(scanner.nextLine().trim());
                            } catch (DateTimeParseException e) {
                                System.out.println("❌ Invalid arrival time format. Use yyyy-MM-ddTHH:mm");
                                break;
                            }

                            if (!arrivalTime.isAfter(departureTime)) {
                                System.out.println("❌ Arrival time must be after departure time.");
                                break;
                            }

                            Flight newFlight = new Flight(flightNumber, airline, origin, destination, departureTime, arrivalTime);

                            List<Flight> flights = FileManager.loadFlightsFromFile();
                            flights.add(newFlight);
                            FileManager.saveFlightsToFile(flights);
                            bookingSystem.setFlights(flights);

                            System.out.println("✅ Flight added successfully.");
                        } catch (IOException e) {
                            System.out.println("❌ Error saving flight: " + e.getMessage());
                        }
                        break;

                    case 3:
                        try {
                            System.out.println("\n--- Update Existing Flight ---");
                            System.out.print("Enter Flight Number to update: ");
                            String updateId = scanner.nextLine();
                            Flight toUpdate = null;
                            for (Flight flight : bookingSystem.getFlights()) {
                                if (flight.getFlightNumber().equals(updateId)) {
                                    toUpdate = flight;
                                    break;
                                }
                            }

                            if (toUpdate != null) {
                                System.out.println("Leave field empty to keep current value.");

                                System.out.println("Current Airline: " + toUpdate.getAirline());
                                System.out.print("New Airline: ");
                                String newAirline = scanner.nextLine();
                                if (!newAirline.isEmpty()) toUpdate.setAirline(newAirline);

                                System.out.println("Current Origin: " + toUpdate.getOrigin());
                                System.out.print("New Origin: ");
                                String newOrigin = scanner.nextLine();
                                if (!newOrigin.isEmpty()) toUpdate.setOrigin(newOrigin);

                                System.out.println("Current Destination: " + toUpdate.getDestination());
                                System.out.print("New Destination: ");
                                String newDestination = scanner.nextLine();
                                if (!newDestination.isEmpty()) toUpdate.setDestination(newDestination);

                                System.out.println("Current Departure Time: " + toUpdate.getDepartureTime());
                                System.out.print("New Departure Time (yyyy-MM-ddTHH:mm): ");
                                String newDeparture = scanner.nextLine();

                                System.out.println("Current Arrival Time: " + toUpdate.getArrivalTime());
                                System.out.print("New Arrival Time (yyyy-MM-ddTHH:mm): ");
                                String newArrival = scanner.nextLine();

                                // Time validation
                                LocalDateTime finalDeparture = toUpdate.getDepartureTime();
                                LocalDateTime finalArrival = toUpdate.getArrivalTime();

                                if (!newDeparture.isEmpty()) {
                                    try {
                                        finalDeparture = LocalDateTime.parse(newDeparture);
                                    } catch (DateTimeParseException e) {
                                        System.out.println("❌ Invalid departure time format. Keeping old value.");
                                    }
                                }

                                if (!newArrival.isEmpty()) {
                                    try {
                                        finalArrival = LocalDateTime.parse(newArrival);
                                    } catch (DateTimeParseException e) {
                                        System.out.println("❌ Invalid arrival time format. Keeping old value.");
                                    }
                                }

                                if (!finalArrival.isAfter(finalDeparture)) {
                                    System.out.println("❌ Arrival time must be after departure time. Update cancelled.");
                                    break;
                                }

                                if (!newDeparture.isEmpty()) toUpdate.setDepartureTime(finalDeparture);
                                if (!newArrival.isEmpty()) toUpdate.setArrivalTime(finalArrival);

                                FileManager.saveFlightsToFile(bookingSystem.getFlights());
                                System.out.println("✅ Flight updated successfully.");
                            } else {
                                System.out.println("❌ Flight not found.");
                            }

                        } catch (IOException e) {
                            System.out.println("❌ Error updating flight: " + e.getMessage());
                        }
                        break;

                    case 0:
                        System.out.println("Returning to main menu...");
                        break;

                    default:
                        System.out.println("❌ Invalid choice. Try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }

        } while (choice != 0);
    }
}



