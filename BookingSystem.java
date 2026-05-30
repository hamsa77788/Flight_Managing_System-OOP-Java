import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class BookingSystem {

    private List<User> users;
    private List<Flight> flights;
    private List<Booking> bookings = new ArrayList<>();  // هنا تعرف المتغير bookings
    private List<Payment> payments;
    private List<Passenger> passengers;

    public BookingSystem() {
        this.users = new ArrayList<>();
        this.flights = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    // الحصول على الحد الأقصى للحجوزات من ملف الإعدادات
    // قراءة القيمة الخاصة بعدد الحجوزات المسموح بها
    private int getMaxBookingsPerUser() {
        String maxBookingsString = FileManager.getSystemSetting("MaxBookingsPerUser");
        if (maxBookingsString == null || maxBookingsString.trim().isEmpty()) {
            System.out.println("MaxBookingsPerUser is missing or empty. Using default value of 3.");
            return 3;  // قيمة افتراضية في حال عدم وجود إعداد في الملف أو إذا كان فارغاً
        }
        try {
            return Integer.parseInt(maxBookingsString);  // محاولة تحويل القيمة إلى عدد صحيح
        } catch (NumberFormatException e) {
            System.out.println("Invalid format for MaxBookingsPerUser. Using default value of 5.");
            return 5;  // إعادة قيمة افتراضية إذا كان التنسيق غير صحيح
        }
    }

    // التحقق من عدد الحجوزات التي قام بها المستخدم
    private boolean checkMaxBookingsPerUser(Customer customer) {
        int maxBookings = getMaxBookingsPerUser();  // الحصول على العدد الأقصى من دالة getMaxBookingsPerUser
        long userBookingsCount = bookings.stream()
                .filter(booking -> booking.getCustomer().equals(customer) && !booking.getStatus().equals("CANCELLED"))
                .count();
        return userBookingsCount < maxBookings;  // التحقق إذا كانت عدد حجوزات المستخدم أقل من الحد الأقصى
    }


    // Process payment for a booking
    public void processPayment(Booking booking, Payment payment) throws Exception {
        if (booking == null || payment == null) {
            throw new IllegalArgumentException("Invalid input data");
        }
        boolean success = payment.processPayment();
        if (success) {
            payments.add(payment);
            booking.setPaymentStatus(Payment.STATUS_CONFIRMED);
            System.out.println("Payment processed successfully");
        } else {
            throw new Exception("Payment processing failed");
        }
    }

    // Modify the booking by updating passenger details
    public void modifyBooking(String bookingRef, List<Passenger> newPassengers) {
        Booking bookingToModify = findBookingByReference(bookingRef);
        if (bookingToModify != null) {
            bookingToModify.setPassengers(newPassengers);
            System.out.println("Passenger information updated for booking " + bookingRef);
        } else {
            throw new IllegalArgumentException("Booking with reference " + bookingRef + " not found");
        }
    }

    // Generate a ticket for a booking
    public void generateTicket(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Invalid booking");
        }
        System.out.println("===== Booking Ticket =====");
        System.out.println("Passenger: " + booking.getCustomer().getName());
        System.out.println("Flight: " + booking.getFlight().getFlightNumber());
        System.out.println("Seat Class: " + booking.getSeatSelections());
        System.out.println("Booking Reference: " + booking.getBookingReference());
        System.out.println("=========================");
    }

    // Create a new booking
    public Booking createBooking(Customer customer, Flight flight, String seatClass, List<Passenger> passengers) throws Exception {
        if (customer == null || flight == null || seatClass == null || passengers == null || passengers.isEmpty()) {
            throw new IllegalArgumentException("Invalid input data");
        }

        // التحقق من الحد الأقصى للحجز لكل مستخدم
        if (!checkMaxBookingsPerUser(customer)) {
            throw new Exception("Maximum booking limit per user reached.");
        }

        int quantity = passengers.size();
        if (!flight.checkAvailability(seatClass, quantity)) {
            throw new Exception("Not enough seats available in the selected class");
        }

        String ref = "BK" + System.currentTimeMillis();
        SeatSelection selection = new SeatSelection(seatClass, quantity); // quantity هو عدد المقاعد التي اختارها المستخدم
        Booking booking = new Booking(ref, customer, flight, selection);

        // Set passengers for the booking
        booking.setPassengers(passengers);

        // Try to reserve seats for the booking
        if (flight.reserveSeat(seatClass, quantity)) {
            bookings.add(booking);
            System.out.println("Booking created successfully, reference: " + ref);
            return booking;
        } else {
            throw new Exception("Failed to reserve seats");
        }
    }

    // Cancel a booking
    public boolean cancelBooking(String bookingRef) {
        Booking booking = findBookingByReference(bookingRef);
        if (booking != null) {
            // تغيير حالة الحجز إلى "CANCELLED"
            booking.setStatus("CANCELLED");

            // إزالة الحجز من قائمة الحجوزات
            bookings.remove(booking);
            return true;
        }
        return false;
    }

    // Save all data to files during system shutdown
    public void shutdown() {
        try {
            FileManager.saveUsersToFile(users);
            FileManager.saveFlightsToFile(flights);
            FileManager.saveBookingsToFile(bookings);
            FileManager.savePassengersToFile(passengers);
            System.out.println("All data saved successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data during shutdown", e);
        }
    }

    // Search for flights by origin, destination, and date
    public List<Flight> searchFlights(String origin, String destination, String date) {
        List<Flight> results = new ArrayList<>();
        LocalDateTime searchDate = LocalDateTime.parse(date);
        for (Flight flight : flights) {
            if (flight.getOrigin().equalsIgnoreCase(origin) &&
                    flight.getDestination().equalsIgnoreCase(destination) &&
                    flight.getDepartureTime().toLocalDate().equals(searchDate.toLocalDate())) {
                results.add(flight);
            }
        }
        return results;
    }

    // Find a booking by its reference number
    public Booking findBookingByReference(String bookingRef) {
        for (Booking booking : bookings) {
            if (booking.getBookingReference().equals(bookingRef)) {
                return booking;
            }
        }
        return null;
    }
    // Getters and setters for the lists
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    // Get a flight by its flight number
    public Flight getFlightByNumber(String flightNumber) {
        for (Flight flight : flights) {
            if (flight.getFlightNumber().equals(flightNumber)) {
                return flight;
            }
        }
        return null;
    }

    // Find a user by their user ID
    public User findUserById(String userId) {
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }
    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
}
