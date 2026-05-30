class SeatClass {
    private String className;
    private int totalCapacity;
    private int availableSeats;
    private double pricePerSeat;

    public SeatClass(String className, int totalCapacity, double pricePerSeat) {
        setClassName(className);
        setTotalCapacity(totalCapacity);
        this.availableSeats = totalCapacity;
        setPricePerSeat(pricePerSeat);
    }

    // Getters
    public String getClassName() { return className; }
    public int getTotalCapacity() { return totalCapacity; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPricePerSeat() { return pricePerSeat; }

    // Setters with validation
    public void setClassName(String className) {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Class name must not be empty");
        }
        this.className = className;
    }

    public void setTotalCapacity(int totalCapacity) {
        if (totalCapacity < 0) {
            throw new IllegalArgumentException("Total capacity cannot be negative");
        }
        this.totalCapacity = totalCapacity;
    }

    public void setPricePerSeat(double pricePerSeat) {
        if (pricePerSeat < 0) {
            throw new IllegalArgumentException("Price per seat cannot be negative");
        }
        this.pricePerSeat = pricePerSeat;
    }

    // Reserve seats in the class (decreases the available seats)
    public boolean reserveSeats(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (availableSeats >= quantity) {
            availableSeats -= quantity;  // Decrease the available seats
            return true;
        } else {
            System.out.println("❌ Not enough seats available in " + className + " class. Available: " + availableSeats);
            return false;  // Not enough seats available
        }
    }

    // Release reserved seats (increases the available seats)
    public void releaseSeats(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (availableSeats + quantity > totalCapacity) {
            throw new IllegalArgumentException("Cannot release more than total capacity");
        }
        availableSeats += quantity;  // Increase the available seats
    }

    // Check if there are enough available seats
    public boolean isSeatAvailable(int quantity) {
        return availableSeats >= quantity;  // Returns true if enough seats are available
    }

    // Calculate the total price for the selected number of seats
    public double calculateTotalPrice(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return pricePerSeat * quantity;  // Total price for the given number of seats
    }
}