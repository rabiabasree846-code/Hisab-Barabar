/**
 * Payment = dusri CHILD class of Transaction (INHERITANCE).
 * Udhar wapas dena: "from" ne "to" ko amount de diya.
 */
public class Payment extends Transaction {
    private int fromIndex;  // paise dene wala
    private int toIndex;    // paise lene wala

    public Payment(String description, double amount, int fromIndex, int toIndex) {
        super(description, amount);
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }
}
