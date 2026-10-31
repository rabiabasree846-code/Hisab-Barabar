import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ColorButton = rang wala gol (rounded) button.
 * Ye JButton ki CHILD class hai (INHERITANCE): JButton ke saare kaam karta hai,
 * bas apni shakal (paintComponent) khud banata hai.
 */
public class ColorButton extends JButton {
    private Color normalColor;
    private Color hoverColor;
    private boolean mouseOver = false;

    public ColorButton(String text, Color color) {
        super(text);
        this.normalColor = color;
        this.hoverColor = color.darker();   // mouse upar aane par thoda gehra rang

        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        // mouse button par aaye ya jaye to rang badlo
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                repaint();
            }
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                repaint();
            }
        });
    }

    // Button ki background shakal khud banana
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (mouseOver) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.dispose();
        super.paintComponent(g);   // text upar likhna
    }
}
