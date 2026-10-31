/**
 * Transaction = PARENT class.
 * Expense aur Payment dono isse inherit karte hain,
 * is liye description aur amount ek hi jagah likhe hain.
 */
public class Transaction {
    // ENCAPSULATION: fields private hain, bahar se sirf getters/setters se milte hain
    private String description;
    private double amount;

    // Constructor: object banate waqt values set hoti hain
    public Transaction(String description, double amount) {
        setDescription(description);
        setAmount(amount);
    }

    public String getDescription() {
        return description;
    }

    // Setter mai check: description khali nahi honi chahiye
    public void setDescription(String description) {
        if (description != null && !description.trim().equals("")) {
            this.description = description.trim();
        }
    }

    public double getAmount() {
        return amount;
    }

    // Setter mai check: amount 0 se bara hona chahiye
    public void setAmount(double amount) {
        if (amount > 0) {
            this.amount = amount;
        }
    }
}
