import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * FileHelper = purane mahine "history.txt" file mai save aur load karta hai.
 *
 * File ka format (har line ek cheez):
 *   MONTH|March 2026|1200.0
 *   EXP|Khana - 900.00 paid by Ali (Equal)
 *   PAY|Usman paid Ali 200.00
 *   BAL|Ali|500.0
 *   END
 */
public class FileHelper {
    private static final String FILE_NAME = "history.txt";

    // Saare archived mahine file mai likhta hai. true = kamyab, false = masla aya
    public static boolean saveMonths(MonthRecord[] months, int count) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME));
            for (int i = 0; i < count; i++) {
                MonthRecord m = months[i];
                writer.println("MONTH|" + m.getMonthName() + "|" + m.getTotalSpent());
                for (int j = 0; j < m.getExpenseCount(); j++) {
                    writer.println("EXP|" + m.getExpenseLine(j));
                }
                for (int j = 0; j < m.getPaymentCount(); j++) {
                    writer.println("PAY|" + m.getPaymentLine(j));
                }
                for (int j = 0; j < m.getPersonCount(); j++) {
                    writer.println("BAL|" + m.getName(j) + "|" + m.getBalance(j));
                }
                writer.println("END");
            }
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println("File save error: " + e.getMessage());
            return false;
        }
    }

    // File se mahine parh kar months[] array mai bharta hai. Wapas mahino ki tadaad deta hai
    public static int loadMonths(MonthRecord[] months) {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return 0;   // file nahi hai to koi history nahi
        }

        int count = 0;
        MonthRecord current = null;
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("")) {
                    continue;
                }
                String[] parts = line.split("\\|");   // "|" par line ko tukron mai torna

                if (parts[0].equals("MONTH")) {
                    current = new MonthRecord(parts[1], Double.parseDouble(parts[2]));
                } else if (parts[0].equals("EXP") && current != null) {
                    current.addExpenseLine(parts[1]);
                } else if (parts[0].equals("PAY") && current != null) {
                    current.addPaymentLine(parts[1]);
                } else if (parts[0].equals("BAL") && current != null) {
                    current.addBalance(parts[1], Double.parseDouble(parts[2]));
                } else if (parts[0].equals("END")) {
                    if (current != null && count < months.length) {
                        months[count] = current;
                        count++;
                    }
                    current = null;
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("File load error: " + e.getMessage());
        }
        return count;
    }
}
