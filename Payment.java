import java.time.LocalDateTime;

class Payment {
    public static final String STATUS_PENDING   = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_FAILED    = "FAILED";

    private String paymentId;
    private Booking booking;
    private double amount;
    private String currency;
    private PaymentMethod method;
    private String status;
    private LocalDateTime transactionDate;

    // Constructor 1: For initial payment creation with status "PENDING"
    public Payment(String paymentId, Booking booking, String currency, PaymentMethod method) {
        setPaymentId(paymentId);
        setBooking(booking);
        this.amount = booking.calculateTotalPrice();  // Assuming this method exists in Booking class
        setCurrency(currency);
        setMethod(method);
        this.status = STATUS_PENDING;
        this.transactionDate = null;
    }

    // Constructor 2: For updating payment after processing
    public Payment(String paymentId, Booking booking, double amount, String currency, String status, LocalDateTime transactionDate) {
        setPaymentId(paymentId);
        setBooking(booking);
        this.amount = amount;
        setCurrency(currency);
        this.status = status;
        this.transactionDate = transactionDate;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public Booking getBooking() { return booking; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public LocalDateTime getTransactionDate() { return transactionDate; }

    // Setters with validation
    public void setPaymentId(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        this.paymentId = paymentId.trim();
    }

    public void setBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking is required for payment");
        }
        this.booking = booking;
    }

    public void setCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency is required");
        }
        this.currency = currency.trim();
    }

    public void setMethod(PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        this.method = method;
    }

    // Business Logic to process payment
    public boolean processPayment() {
        if (!method.validateDetails()) {
            status = STATUS_FAILED;
            booking.setPaymentStatus(STATUS_FAILED);
            System.out.println("✘ " + method.getMethodName() + " details are invalid. Payment failed.");
            return false;
        }

        boolean ok = method.process(amount);
        if (ok) {
            status = STATUS_CONFIRMED;
            transactionDate = LocalDateTime.now();
            booking.setPaymentStatus(STATUS_CONFIRMED);
            System.out.println("✔ Payment successful at: " + transactionDate);
        } else {
            status = STATUS_FAILED;
            booking.setPaymentStatus(STATUS_FAILED);
            System.out.println("✘ Payment processing failed via " + method.getMethodName());
        }

        return ok;
    }

    // Cancel the payment if pending
    public void cancelPayment() {
        if (!status.equals(STATUS_PENDING)) {
            throw new IllegalStateException("Cannot cancel payment in current state");
        }
        status = STATUS_FAILED;
        booking.setPaymentStatus(STATUS_FAILED);
        System.out.println("✘ Payment cancelled for booking: " + booking.getBookingReference());
    }
}
