class SeatSelection {
    private final String className;
    private final int quantity;

    public SeatSelection(String className,  int quantity) {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.className = className;
        this.quantity = quantity;
    }

    public String getClassName() { return className; }
    public int getQuantity() { return quantity; }
}