/**
 * Person = hostel ka aik member.
 * balance positive = isay paise milne hain, negative = isay paise dene hain.
 */
public class Person {
    private String name;
    private double balance;

    public Person(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().equals("")) {
            this.name = name.trim();
        }
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
