import java.util.ArrayList;
import java.util.List;

class Customer extends User {
    private String customerId;
    private String address;
    private List<String> preferences;
    private List<Booking> bookingHistory;
    private BookingSystem bookingSystem;

    public Customer(String userId,
                    String username,
                    String password,
                    String name,
                    String email,
                    String contactInfo,
                    String customerId,
                    String address,
                    List<String> preferences,
                    BookingSystem bookingSystem) {
        super(userId, username, password, name, email, contactInfo);

        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (bookingSystem == null) {
            throw new IllegalArgumentException("Booking system is required");
        }

        this.customerId = customerId.trim();
        this.address = address.trim();
        this.preferences = preferences != null ? new ArrayList<>(preferences) : new ArrayList<>();
        this.bookingHistory = new ArrayList<>();
        this.bookingSystem = bookingSystem;
    }

    public List<Flight> searchFlights(String origin, String destination, String date) {
        if (!isActive()) {
            System.out.println("Cannot search for flights; account is not active.");
            return new ArrayList<>();
        }
        return bookingSystem.searchFlights(origin, destination, date);
    }

    public Booking createBooking(Flight flight, String seatClass, List<Passenger> passengers) throws Exception {
        if (!isActive()) {
            throw new IllegalStateException("Cannot create booking; account is not active.");
        }

        Booking booking = bookingSystem.createBooking(this, flight, seatClass, passengers);
        this.bookingHistory.add(booking);
        return booking;
    }

    public List<Booking> viewBookings() {
        // العودة فقط للحجوزات التي لم يتم إلغاؤها والتي تخص العميل الحالي
        List<Booking> activeBookings = new ArrayList<>();
        for (Booking booking : bookingSystem.getBookings()) {
            // إذا كان الحجز مرتبط بالعميل الحالي أو تم إنشاؤه بواسطة الوكيل له
            if (booking.getCustomer().equals(this) && !booking.getStatus().equalsIgnoreCase("CANCELLED")) {
                activeBookings.add(booking);
            }
        }
        return activeBookings;
    }




    public boolean cancelBooking(String ref) {
        // التحقق إذا كان العميل قد قام بالحجز أم لا
        List<Booking> allBookings = viewBookings(); // استخدم viewBookings لاسترجاع كل الحجوزات الخاصة بالعميل
        if (allBookings.isEmpty()) {
            System.out.println("✘ You haven't made any bookings yet.");
            return false; // لا يوجد حجزات لإلغاءها
        }

        // محاولة العثور على الحجز بناءً على الرقم المرجعي
        Booking booking = allBookings.stream()
                .filter(b -> b.getBookingReference().equals(ref))
                .findFirst()
                .orElse(null);

        if (booking == null) {
            System.out.println("✘ Booking not found.");
            return false; // الحجز غير موجود
        }

        // إذا تم العثور على الحجز، يتم إلغاءه
        if (booking.getStatus().equalsIgnoreCase("CANCELLED")) {
            System.out.println("✘ This booking is already cancelled.");
            return false;
        }

        booking.setStatus("CANCELLED");

        return true; // الحجز تم إلغاؤه بنجاح
    }


    @Override
    public boolean login(String username, String password) {
        return isActive() && getUsername().equals(username) && verifyPassword(password);
    }

    private boolean verifyPassword(String inputPassword) {
        // Implement secure password verification here
        // This could involve hashing the input password and comparing it to a stored hash
        return getPassword().equals(inputPassword);
    }

    @Override
    public void logout() {
        System.out.println("Customer logged out successfully");
    }

    // Getters and setters
    public String getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        this.address = address.trim();
    }

    public List<String> getPreferences() {
        return new ArrayList<>(preferences);
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences != null ? new ArrayList<>(preferences) : new ArrayList<>();
    }

    public List<Booking> getBookingHistory() {
        return new ArrayList<>(bookingHistory);
    }
}