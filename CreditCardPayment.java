public class CreditCardPayment implements PaymentMethod {
    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private String cvv;

    public CreditCardPayment(String cardNumber, String cardHolder, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    @Override
    public boolean validateDetails() {
        return cardNumber != null && cardNumber.length() == 16
                && cvv != null && cvv.length() == 3;
    }

    @Override
    public boolean process(double amount) {
        System.out.println("Processing credit card payment of $" + amount);
        return true;
    }

    @Override
    public String getMethodName() {
        return "Credit Card";
    }
}