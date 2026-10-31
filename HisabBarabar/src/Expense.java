/**
 * Expense = CHILD class of Transaction (INHERITANCE).
 * Aik kharcha: kisne diya, split type kya tha, aur har bande ka hissa kitna tha.
 */
public class Expense extends Transaction {
    private int paidByIndex;        // persons[] array mai payer ka number
    private String splitType;       // "Equal", "Percentage" ya "Shares"
    private double[] shareAmounts;  // shareAmounts[i] = person i ka hissa (0 agar shamil nahi)

    public Expense(String description, double amount, int paidByIndex,
                   String splitType, double[] shareAmounts) {
        super(description, amount);   // parent class ka constructor
        this.paidByIndex = paidByIndex;
        this.splitType = splitType;
        this.shareAmounts = shareAmounts;
    }

    public int getPaidByIndex() {
        return paidByIndex;
    }

    public String getSplitType() {
        return splitType;
    }

    public double[] getShareAmounts() {
        return shareAmounts;
    }
}
