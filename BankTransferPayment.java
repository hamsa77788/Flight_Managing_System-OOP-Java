public class BankTransferPayment implements PaymentMethod {
    private String bankAccountNumber;
    private String bankName;
    private String swiftCode;

    public BankTransferPayment(String bankAccountNumber, String bankName, String swiftCode) {
        this.bankAccountNumber = bankAccountNumber;
        this.bankName = bankName;
        this.swiftCode = swiftCode;
    }

    @Override
    public boolean validateDetails() {
        return bankAccountNumber != null && bankAccountNumber.length() >= 10
                && swiftCode != null && !swiftCode.trim().isEmpty();
    }

    @Override
    public boolean process(double amount) {
        System.out.println("Processing bank transfer of $" + amount + " to " + bankName);
        return true;
    }

    @Override
    public String getMethodName() {
        return "Bank Transfer";
    }
}