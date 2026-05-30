public interface PaymentMethod {
    boolean validateDetails();
    boolean process(double amount);
    String getMethodName();
}