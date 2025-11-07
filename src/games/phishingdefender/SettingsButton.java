package games.phishingdefender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

/**
 * Ein kleiner, runder Button mit einem Zahnrad-Icon (⚙️),
 * der normalerweise unten rechts platziert wird, um den Einstellungsdialog zu öffnen.
 * Hat einen eigenen Hover- und Klick-Effekt (Glow, Alpha-Änderung).
 *
 * @author yusef03
 * @version 1.0
 */

public class SettingsButton extends JButton {

    private boolean isHovered = false;
    private float alpha = 0.7f;

    public SettingsButton() {
        setText("⚙️");
        setFont(new Font("Arial", Font.PLAIN, 28));
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(60, 60));

        // Hover-Effekt
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                alpha = 0.9f;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                alpha = 0.7f;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                alpha = 1.0f;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                alpha = isHovered ? 0.9f : 0.7f;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        // Schatten
        g2.setColor(new Color(0, 0, 0, (int)(100 * alpha)));
        g2.fill(new Ellipse2D.Float(x + 3, y + 3, size, size));

        // Glow beim Hover
        if (isHovered) {
            g2.setColor(new Color(0, 220, 120, (int)(60 * alpha)));
            g2.fill(new Ellipse2D.Float(x - 4, y - 4, size + 8, size + 8));
        }

        // Haupt-Circle (Gradient)
        GradientPaint gradient = new GradientPaint(
                x, y, Theme.COLOR_BUTTON_NEUTRAL,
                x, y + size, new Color(50, 50, 50, (int)(220 * alpha))
        );
        g2.setPaint(gradient);
        g2.fill(new Ellipse2D.Float(x, y, size, size));

        // Border
        g2.setColor(new Color(150, 150, 150, (int)(150 * alpha)));
        g2.setStroke(new BasicStroke(2));
        g2.draw(new Ellipse2D.Float(x, y, size, size));

        // Glanz
        GradientPaint gloss = new GradientPaint(
                x, y, new Color(255, 255, 255, (int)(40 * alpha)),
                x, y + size / 2, new Color(255, 255, 255, 0)
        );
        g2.setPaint(gloss);
        g2.fill(new Ellipse2D.Float(x, y, size, size / 2));

        g2.dispose();
        super.paintComponent(g);
    }
}