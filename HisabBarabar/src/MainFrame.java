import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * MainFrame = Hisab Barabar ki poori window (GUI) aur saare buttons ka kaam.
 * 4 tabs: Friends, Add Expense, Balances, History.
 */
public class MainFrame extends JFrame {

    // ---------- RANG (colors) ----------
    private static final Color PRIMARY = new Color(79, 70, 229);    // indigo
    private static final Color BG = new Color(243, 244, 246);       // halka grey
    private static final Color GREEN = new Color(16, 185, 129);
    private static final Color RED = new Color(239, 68, 68);
    private static final Color TEAL = new Color(14, 165, 233);
    private static final Color BORDER_GREY = new Color(220, 224, 230);
    // dost ke naam ke aage wale gol dot ke rang
    private static final String[] DOT_COLORS = {
        "#4F46E5", "#EC4899", "#F59E0B", "#10B981", "#06B6D4",
        "#8B5CF6", "#EF4444", "#84CC16", "#F97316", "#14B8A6"
    };

    // ---------- DATA (sab arrays mai, RAM mai) ----------
    private Person[] persons = new Person[10];
    private int personCount = 0;

    private Expense[] expenses = new Expense[100];
    private int expenseCount = 0;

    private Payment[] payments = new Payment[100];
    private int paymentCount = 0;

    private MonthRecord[] months = new MonthRecord[12];
    private int monthCount = 0;

    // ---------- GUI parts: Friends tab ----------
    private JTextField nameField = styledField(15);
    private JEditorPane membersView = htmlPane();

    // ---------- GUI parts: Add Expense tab ----------
    private JTextField descField = styledField(20);
    private JTextField amountField = styledField(20);
    private JComboBox<String> paidByBox = new JComboBox<String>();
    private JComboBox<String> splitBox =
            new JComboBox<String>(new String[]{"Equal", "Percentage", "Shares"});
    private JPanel participantsPanel = new JPanel();
    private JCheckBox[] checks = new JCheckBox[10];
    private JTextField[] valueFields = new JTextField[10];
    private JLabel hintLabel = new JLabel(" ");

    // ---------- GUI parts: Balances tab ----------
    private JEditorPane balanceView = htmlPane();
    private JComboBox<String> fromBox = new JComboBox<String>();
    private JComboBox<String> toBox = new JComboBox<String>();
    private JTextField payAmountField = styledField(8);

    // ---------- GUI parts: History tab ----------
    private JComboBox<String> monthBox = new JComboBox<String>();
    private JEditorPane historyView = htmlPane();

    // ================= CONSTRUCTOR =================
    public MainFrame() {
        super("Hisab Barabar - Split expenses with friends");
        setSize(820, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        paidByBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        splitBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fromBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        monthBox.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.addTab("Friends", buildMembersTab());
        tabs.addTab("Add Expense", buildExpenseTab());
        tabs.addTab("Balances", buildBalancesTab());
        tabs.addTab("History", buildHistoryTab());

        add(buildHeader(), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        monthCount = FileHelper.loadMonths(months);   // purani history file se
        refreshMembers();
        refreshBalances();
        refreshMonthBox();
    }

    // ================= LOOK & FEEL HELPERS =================

    // Upar ka rangeen banner
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        JLabel title = new JLabel("Hisab Barabar");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Dosto ka hisab, hamesha barabar!");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(224, 231, 255));

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);
        header.add(text, BorderLayout.WEST);
        return header;
    }

    // Har tab ka base panel (grey background + padding)
    private JPanel tabPanel() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return p;
    }

    // Safaid "card" panel jis par border ho
    private JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GREY),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        return p;
    }

    // Khoobsurat text box
    private JTextField styledField(int columns) {
        JTextField f = new JTextField(columns);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return f;
    }

    // HTML dikhane wala panel (rang aur table ke liye)
    private JEditorPane htmlPane() {
        JEditorPane p = new JEditorPane();
        p.setContentType("text/html");
        p.setEditable(false);
        p.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        p.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return p;
    }

    private JScrollPane scroll(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_GREY));
        return sp;
    }

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        return l;
    }

    // ================= TAB BUILDERS =================

    // Tab 1: Friends
    private JPanel buildMembersTab() {
        JPanel panel = tabPanel();

        JPanel top = card(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.add(boldLabel("Friend's name:"));
        top.add(nameField);
        ColorButton addBtn = new ColorButton("Add Friend", PRIMARY);
        addBtn.addActionListener(e -> addMember());
        top.add(addBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll(membersView), BorderLayout.CENTER);
        return panel;
    }

    // Tab 2: Add Expense
    private JPanel buildExpenseTab() {
        JPanel panel = tabPanel();

        JPanel form = card(new GridLayout(4, 2, 10, 10));
        form.add(boldLabel("What was it for?"));
        form.add(descField);
        form.add(boldLabel("Amount:"));
        form.add(amountField);
        form.add(boldLabel("Paid by:"));
        form.add(paidByBox);
        form.add(boldLabel("Split type:"));
        form.add(splitBox);
        splitBox.addActionListener(e -> updateSplitView());

        participantsPanel.setLayout(new BoxLayout(participantsPanel, BoxLayout.Y_AXIS));
        participantsPanel.setBackground(Color.WHITE);
        participantsPanel.setBorder(BorderFactory.createTitledBorder("Split between"));

        hintLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        hintLabel.setForeground(new Color(107, 114, 128));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setOpaque(false);
        ColorButton saveBtn = new ColorButton("Save Expense", GREEN);
        saveBtn.addActionListener(e -> saveExpense());
        btnRow.add(saveBtn);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        bottom.add(hintLabel, BorderLayout.NORTH);
        bottom.add(btnRow, BorderLayout.CENTER);

        panel.add(form, BorderLayout.NORTH);
        panel.add(scroll(participantsPanel), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // Tab 3: Balances
    private JPanel buildBalancesTab() {
        JPanel panel = tabPanel();
        panel.add(scroll(balanceView), BorderLayout.CENTER);

        JPanel payRow = card(new FlowLayout(FlowLayout.LEFT, 8, 6));
        payRow.add(boldLabel("Payment:"));
        payRow.add(fromBox);
        payRow.add(new JLabel("paid"));
        payRow.add(toBox);
        payRow.add(payAmountField);
        ColorButton payBtn = new ColorButton("Record Payment", TEAL);
        payBtn.addActionListener(e -> recordPayment());
        payRow.add(payBtn);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        btnRow.setOpaque(false);
        ColorButton resetBtn = new ColorButton("Reset Month", RED);
        resetBtn.addActionListener(e -> resetMonth());
        btnRow.add(resetBtn);

        JPanel south = new JPanel(new GridLayout(2, 1, 0, 6));
        south.setOpaque(false);
        south.add(payRow);
        south.add(btnRow);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // Tab 4: History
    private JPanel buildHistoryTab() {
        JPanel panel = tabPanel();

        JPanel top = card(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.add(boldLabel("Select month:"));
        top.add(monthBox);
        ColorButton viewBtn = new ColorButton("View", PRIMARY);
        viewBtn.addActionListener(e -> viewMonth());
        top.add(viewBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll(historyView), BorderLayout.CENTER);
        return panel;
    }

    // ================= BUTTON LOGIC =================

    // [Add Friend]
    private void addMember() {
        String name = nameField.getText().trim();
        if (name.equals("")) {
            showError("Name cannot be empty");
            return;
        }
        if (name.contains("|")) {
            showError("Name cannot contain the | symbol");
            return;
        }
        if (personCount >= 10) {
            showError("Maximum 10 friends allowed");
            return;
        }
        for (int i = 0; i < personCount; i++) {
            if (persons[i].getName().equalsIgnoreCase(name)) {
                showError("This friend already exists");
                return;
            }
        }
        persons[personCount] = new Person(name, 0);
        personCount++;
        nameField.setText("");
        refreshMembers();
        refreshBalances();
    }

    // [Save Expense]
    private void saveExpense() {
        if (personCount == 0) {
            showError("Add friends first");
            return;
        }
        if (expenseCount >= 100) {
            showError("Maximum 100 expenses per month. Please reset the month.");
            return;
        }

        String desc = descField.getText().trim().replace("|", "-");
        if (desc.equals("")) {
            showError("Please enter a description");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Enter a valid amount greater than 0");
            return;
        }
        amount = round2(amount);
        if (amount <= 0) {
            showError("Enter a valid amount greater than 0");
            return;
        }

        int paidBy = paidByBox.getSelectedIndex();
        int selected = 0;
        int last = -1;
        for (int i = 0; i < personCount; i++) {
            if (checks[i].isSelected()) {
                selected++;
                last = i;
            }
        }
        if (selected == 0) {
            showError("Select at least one person");
            return;
        }

        String type = (String) splitBox.getSelectedItem();
        double[] shares = new double[personCount];

        if (type.equals("Equal")) {
            for (int i = 0; i < personCount; i++) {
                if (checks[i].isSelected()) {
                    shares[i] = round2(amount / selected);
                }
            }
        } else {
            double[] values = new double[personCount];
            double totalValues = 0;
            for (int i = 0; i < personCount; i++) {
                if (checks[i].isSelected()) {
                    try {
                        values[i] = Double.parseDouble(valueFields[i].getText().trim());
                    } catch (NumberFormatException ex) {
                        showError("Enter a valid number for " + persons[i].getName());
                        return;
                    }
                    if (values[i] <= 0) {
                        showError("Value must be greater than 0 for " + persons[i].getName());
                        return;
                    }
                    totalValues += values[i];
                }
            }

            if (type.equals("Percentage")) {
                if (Math.abs(totalValues - 100) > 0.01) {
                    showError("Percentages must add up to 100 (now " + money(totalValues) + ")");
                    return;
                }
                for (int i = 0; i < personCount; i++) {
                    if (checks[i].isSelected()) {
                        shares[i] = round2(amount * values[i] / 100);
                    }
                }
            } else {
                for (int i = 0; i < personCount; i++) {
                    if (checks[i].isSelected()) {
                        shares[i] = round2(amount * values[i] / totalValues);
                    }
                }
            }
        }

        // ROUNDING FIX: aakhri banda bacha hua baqi paisa le leta hai
        double others = 0;
        for (int i = 0; i < personCount; i++) {
            if (checks[i].isSelected() && i != last) {
                others += shares[i];
            }
        }
        shares[last] = round2(amount - others);

        Expense exp = new Expense(desc, amount, paidBy, type, shares);
        expenses[expenseCount] = exp;
        expenseCount++;
        applyExpense(exp);

        descField.setText("");
        amountField.setText("");
        for (int i = 0; i < personCount; i++) {
            valueFields[i].setText("");
        }
        refreshBalances();

        String msg = "Expense saved!\n";
        for (int i = 0; i < personCount; i++) {
            if (checks[i].isSelected()) {
                msg += persons[i].getName() + ": " + money(shares[i]) + "\n";
            }
        }
        showInfo(msg);
    }

    // [Record Payment]
    private void recordPayment() {
        if (personCount < 2) {
            showError("You need at least 2 friends");
            return;
        }
        if (paymentCount >= 100) {
            showError("Maximum 100 payments per month. Please reset the month.");
            return;
        }
        int from = fromBox.getSelectedIndex();
        int to = toBox.getSelectedIndex();
        if (from == to) {
            showError("Choose two different people");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(payAmountField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Enter a valid payment amount");
            return;
        }
        amount = round2(amount);
        if (amount <= 0) {
            showError("Payment must be greater than 0");
            return;
        }

        payments[paymentCount] = new Payment("Payment", amount, from, to);
        paymentCount++;

        persons[from].setBalance(round2(persons[from].getBalance() + amount));
        persons[to].setBalance(round2(persons[to].getBalance() - amount));

        payAmountField.setText("");
        refreshBalances();
        showInfo("Payment recorded: " + persons[from].getName() + " paid "
                + persons[to].getName() + " " + money(amount));
    }

    // [Reset Month] : pehle archive, phir naya mahina
    private void resetMonth() {
        if (expenseCount == 0 && paymentCount == 0) {
            showError("Nothing to archive. This month is empty.");
            return;
        }
        if (monthCount >= 12) {
            showError("History is full (12 months).");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Archive this month and start a new month?",
                "Reset Month", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        String monthName = JOptionPane.showInputDialog(this, "Enter month name (e.g. March 2026):");
        if (monthName == null) {
            return;
        }
        monthName = monthName.trim().replace("|", "-");
        if (monthName.equals("")) {
            showError("Month name cannot be empty");
            return;
        }

        double total = 0;
        for (int i = 0; i < expenseCount; i++) {
            total += expenses[i].getAmount();
        }

        MonthRecord record = new MonthRecord(monthName, round2(total));
        for (int i = 0; i < expenseCount; i++) {
            record.addExpenseLine(expenseLine(expenses[i]));
        }
        for (int i = 0; i < paymentCount; i++) {
            record.addPaymentLine(paymentLine(payments[i]));
        }
        for (int i = 0; i < personCount; i++) {
            record.addBalance(persons[i].getName(), round2(persons[i].getBalance()));
        }
        months[monthCount] = record;
        monthCount++;

        boolean saved = FileHelper.saveMonths(months, monthCount);
        if (!saved) {
            showError("Could not save history.txt file!");
        }

        for (int i = 0; i < expenseCount; i++) {
            expenses[i] = null;
        }
        expenseCount = 0;
        for (int i = 0; i < paymentCount; i++) {
            payments[i] = null;
        }
        paymentCount = 0;
        for (int i = 0; i < personCount; i++) {
            persons[i].setBalance(0);
        }

        refreshBalances();
        refreshMonthBox();
        monthBox.setSelectedIndex(monthCount - 1);
        showInfo("Month archived. A new month has started.");
    }

    // [View] in History tab
    private void viewMonth() {
        int index = monthBox.getSelectedIndex();
        if (index < 0) {
            showError("No archived months yet");
            return;
        }
        MonthRecord m = months[index];

        String html = "<html><body style='font-family:SansSerif'>";
        html += "<h2><font color='#4F46E5'>" + esc(m.getMonthName()) + "</font></h2>";
        html += "<table width='100%' cellpadding='10'><tr><td bgcolor='#EEF2FF'>"
                + "<font color='#4F46E5'>Total spent</font><br><font size='5'><b>"
                + money(m.getTotalSpent()) + "</b></font></td></tr></table>";

        html += "<h3>Expenses</h3>";
        if (m.getExpenseCount() == 0) {
            html += "<p><i>none</i></p>";
        }
        for (int i = 0; i < m.getExpenseCount(); i++) {
            html += "<p>" + (i + 1) + ". " + esc(m.getExpenseLine(i)) + "</p>";
        }

        html += "<h3>Payments</h3>";
        if (m.getPaymentCount() == 0) {
            html += "<p><i>none</i></p>";
        }
        for (int i = 0; i < m.getPaymentCount(); i++) {
            html += "<p>" + (i + 1) + ". " + esc(m.getPaymentLine(i)) + "</p>";
        }

        html += "<h3>Final balances</h3><table width='100%' cellpadding='8'>";
        for (int i = 0; i < m.getPersonCount(); i++) {
            html += "<tr bgcolor='" + rowColor(i) + "'><td>" + esc(m.getName(i)) + "</td>"
                    + "<td align='right'>" + balanceHtml(m.getBalance(i)) + "</td></tr>";
        }
        html += "</table></body></html>";

        historyView.setText(html);
        historyView.setCaretPosition(0);
    }

    // ================= HELPER METHODS =================

    // Aik kharche ka asar balances par: payer ka plus, hissedaron ka minus
    private void applyExpense(Expense e) {
        Person payer = persons[e.getPaidByIndex()];
        payer.setBalance(round2(payer.getBalance() + e.getAmount()));
        double[] s = e.getShareAmounts();
        for (int i = 0; i < s.length; i++) {
            persons[i].setBalance(round2(persons[i].getBalance() - s[i]));
        }
    }

    // Balances tab (rangeen HTML) dobara banata hai. Har change ke baad khud chalta hai
    private void refreshBalances() {
        double total = 0;
        for (int i = 0; i < expenseCount; i++) {
            total += expenses[i].getAmount();
        }

        String html = "<html><body style='font-family:SansSerif'>";

        // upar ke 2 summary cards
        html += "<table width='100%' cellpadding='12'><tr>"
                + "<td bgcolor='#EEF2FF' width='50%'><font color='#4F46E5'>Total spent this month</font>"
                + "<br><font size='6'><b>" + money(total) + "</b></font></td>"
                + "<td bgcolor='#ECFDF5' width='50%'><font color='#059669'>Expenses added</font>"
                + "<br><font size='6'><b>" + expenseCount + "</b></font></td>"
                + "</tr></table>";

        // net balances
        html += "<h3><font color='#4F46E5'>Net balances</font></h3>";
        if (personCount == 0) {
            html += "<p><i>No friends yet. Add your first friend in the Friends tab!</i></p>";
        } else {
            html += "<table width='100%' cellpadding='8'>";
            for (int i = 0; i < personCount; i++) {
                double b = round2(persons[i].getBalance());
                String note = "settled";
                if (b > 0.005) {
                    note = "gets money";
                } else if (b < -0.005) {
                    note = "owes money";
                }
                html += "<tr bgcolor='" + rowColor(i) + "'><td><b>" + esc(persons[i].getName())
                        + "</b></td><td align='right'>" + balanceHtml(b) + "</td><td>"
                        + note + "</td></tr>";
            }
            html += "</table>";
        }

        // WHO PAYS WHOM
        html += "<h3><font color='#4F46E5'>Who pays whom</font></h3>";
        boolean any = false;
        if (personCount > 0) {
            double[] temp = new double[personCount];
            for (int i = 0; i < personCount; i++) {
                temp[i] = round2(persons[i].getBalance());
            }
            for (int round = 0; round < 100; round++) {
                int minI = 0;
                int maxI = 0;
                for (int i = 0; i < personCount; i++) {
                    if (temp[i] < temp[minI]) {
                        minI = i;
                    }
                    if (temp[i] > temp[maxI]) {
                        maxI = i;
                    }
                }
                if (temp[minI] > -0.005 || temp[maxI] < 0.005) {
                    break;
                }
                double amt = round2(Math.min(-temp[minI], temp[maxI]));
                html += "<table width='100%' cellpadding='8'><tr><td bgcolor='#FEF3C7'><b>"
                        + esc(persons[minI].getName()) + "</b> pays <b>"
                        + esc(persons[maxI].getName()) + "</b> &nbsp; <font color='#4F46E5'><b>"
                        + money(amt) + "</b></font></td></tr></table><br>";
                any = true;
                temp[minI] += amt;
                temp[maxI] -= amt;
            }
        }
        if (!any) {
            html += "<p><font color='#059669'><b>Everyone is settled.</b></font></p>";
        }

        // expenses list
        html += "<h3><font color='#4F46E5'>Expenses this month</font></h3>";
        if (expenseCount == 0) {
            html += "<p><i>none yet</i></p>";
        }
        for (int i = 0; i < expenseCount; i++) {
            html += "<p>" + (i + 1) + ". " + esc(expenseLine(expenses[i])) + "</p>";
        }

        html += "<h3><font color='#4F46E5'>Payments this month</font></h3>";
        if (paymentCount == 0) {
            html += "<p><i>none yet</i></p>";
        }
        for (int i = 0; i < paymentCount; i++) {
            html += "<p>" + (i + 1) + ". " + esc(paymentLine(payments[i])) + "</p>";
        }

        html += "</body></html>";
        balanceView.setText(html);
        balanceView.setCaretPosition(0);
    }

    // Friends list, dropdowns aur checkboxes dobara banata hai
    private void refreshMembers() {
        String html = "<html><body style='font-family:SansSerif'>";
        if (personCount == 0) {
            html += "<p><i>No friends added yet. Type a name above and click Add Friend!</i></p>";
        }
        for (int i = 0; i < personCount; i++) {
            html += "<p><font color='" + DOT_COLORS[i % DOT_COLORS.length] + "' size='5'>\u25CF</font>"
                    + " &nbsp;<font size='4'><b>" + esc(persons[i].getName()) + "</b></font></p>";
        }
        html += "</body></html>";
        membersView.setText(html);
        membersView.setCaretPosition(0);

        paidByBox.removeAllItems();
        fromBox.removeAllItems();
        toBox.removeAllItems();
        for (int i = 0; i < personCount; i++) {
            paidByBox.addItem(persons[i].getName());
            fromBox.addItem(persons[i].getName());
            toBox.addItem(persons[i].getName());
        }
        rebuildParticipants();
    }

    // Har friend ke liye checkbox + number box banata hai
    private void rebuildParticipants() {
        participantsPanel.removeAll();
        for (int i = 0; i < personCount; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBackground(Color.WHITE);
            checks[i] = new JCheckBox(persons[i].getName(), true);
            checks[i].setBackground(Color.WHITE);
            checks[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
            valueFields[i] = styledField(6);
            row.add(checks[i]);
            row.add(valueFields[i]);
            participantsPanel.add(row);
        }
        updateSplitView();
        participantsPanel.revalidate();
        participantsPanel.repaint();
    }

    // Split type ke hisab se number boxes dikhana ya chhupana
    private void updateSplitView() {
        String type = (String) splitBox.getSelectedItem();
        boolean showBoxes = !type.equals("Equal");
        for (int i = 0; i < personCount; i++) {
            valueFields[i].setVisible(showBoxes);
        }
        if (type.equals("Equal")) {
            hintLabel.setText("Equal: amount is divided equally between ticked people.");
        } else if (type.equals("Percentage")) {
            hintLabel.setText("Percentage: type a % for each ticked person (total must be 100).");
        } else {
            hintLabel.setText("Shares: type shares for each ticked person (e.g. 2, 1, 1).");
        }
    }

    // History tab ka dropdown dobara bharna
    private void refreshMonthBox() {
        monthBox.removeAllItems();
        for (int i = 0; i < monthCount; i++) {
            monthBox.addItem(months[i].getMonthName());
        }
    }

    // Balance ko hara (+) ya laal (-) rang mai likhna
    private String balanceHtml(double b) {
        b = round2(b);
        if (b > 0.005) {
            return "<font color='#059669'><b>+" + money(b) + "</b></font>";
        } else if (b < -0.005) {
            return "<font color='#DC2626'><b>" + money(b) + "</b></font>";
        }
        return "<font color='#6B7280'><b>0.00</b></font>";
    }

    // Table ki rows ke halke alag rang
    private String rowColor(int i) {
        if (i % 2 == 0) {
            return "#F9FAFB";
        }
        return "#F3F4F6";
    }

    // HTML mai khaas symbols (<, >, &) ko safe banana
    private String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String expenseLine(Expense e) {
        return e.getDescription() + " - " + money(e.getAmount()) + " paid by "
                + persons[e.getPaidByIndex()].getName() + " (" + e.getSplitType() + ")";
    }

    private String paymentLine(Payment p) {
        return persons[p.getFromIndex()].getName() + " paid "
                + persons[p.getToIndex()].getName() + " " + money(p.getAmount());
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

    private String money(double x) {
        return String.format(Locale.US, "%.2f", x);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Hisab Barabar", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= PROGRAM YAHAN SE SHURU =================
    public static void main(String[] args) {
        try {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // agar look and feel na bane to default chalega
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
