import java.util.Locale;

/**
 * MonthRecord = aik purane (archived) mahine ka record.
 * Isme us mahine ke kharche, payments aur final balances text ki shakal mai rakhe hain.
 */
public class MonthRecord {
    private String monthName;
    private double totalSpent;

    private String[] expenseLines = new String[100];
    private int expenseCount = 0;

    private String[] paymentLines = new String[100];
    private int paymentCount = 0;

    private String[] names = new String[10];
    private double[] balances = new double[10];
    private int personCount = 0;

    public MonthRecord(String monthName, double totalSpent) {
        this.monthName = monthName;
        this.totalSpent = totalSpent;
    }

    // ----- add methods -----
    public void addExpenseLine(String line) {
        if (expenseCount < 100) {
            expenseLines[expenseCount] = line;
            expenseCount++;
        }
    }

    public void addPaymentLine(String line) {
        if (paymentCount < 100) {
            paymentLines[paymentCount] = line;
            paymentCount++;
        }
    }

    public void addBalance(String name, double balance) {
        if (personCount < 10) {
            names[personCount] = name;
            balances[personCount] = balance;
            personCount++;
        }
    }

    // ----- getters -----
    public String getMonthName() { return monthName; }
    public double getTotalSpent() { return totalSpent; }
    public int getExpenseCount() { return expenseCount; }
    public String getExpenseLine(int i) { return expenseLines[i]; }
    public int getPaymentCount() { return paymentCount; }
    public String getPaymentLine(int i) { return paymentLines[i]; }
    public int getPersonCount() { return personCount; }
    public String getName(int i) { return names[i]; }
    public double getBalance(int i) { return balances[i]; }

    // History tab mai dikhane ke liye poora text banata hai
    public String getSummary() {
        String text = monthName.toUpperCase() + "\n";
        text += "==============================\n\n";

        text += "EXPENSES\n--------\n";
        if (expenseCount == 0) {
            text += "(none)\n";
        }
        for (int i = 0; i < expenseCount; i++) {
            text += (i + 1) + ". " + expenseLines[i] + "\n";
        }

        text += "\nPAYMENTS\n--------\n";
        if (paymentCount == 0) {
            text += "(none)\n";
        }
        for (int i = 0; i < paymentCount; i++) {
            text += (i + 1) + ". " + paymentLines[i] + "\n";
        }

        text += "\nFINAL BALANCES\n--------------\n";
        for (int i = 0; i < personCount; i++) {
            text += String.format(Locale.US, "%-10s %+10.2f\n", names[i], balances[i]);
        }

        text += "\nTOTAL SPENT: " + String.format(Locale.US, "%.2f", totalSpent) + "\n";
        return text;
    }
}
