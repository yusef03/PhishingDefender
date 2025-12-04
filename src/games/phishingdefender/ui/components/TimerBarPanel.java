package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Schlanke Timer-Leiste (ohne Text).
 * Ändert Farbe basierend auf verbleibender Zeit (Grün -> Orange -> Rot).
 *
 * @author yusef03
 * @version 2.0
 */
public class TimerBarPanel extends JPanel {

    private double smoothPercent = 1.0;
    private int currentSeconds = 0;
    private int maxSeconds = 1;
    private boolean isFirewallActive = false;

    public TimerBarPanel() {
        super();
        setOpaque(true);
        setBackground(Theme.COLOR_PANEL_DARK);
        setPreferredSize(new Dimension(100, 10)); // Höhe fixiert
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
    }

    // Für flüssige Animationen (wird vom Timer mit ms-Genauigkeit aufgerufen)
    public void updateSmooth(double percent) {
        this.smoothPercent = percent;
        repaint();
    }

    // Setzt Metadaten für Farbwahl
    public void updateTime(int current, int max) {
        this.currentSeconds = current;
        this.maxSeconds = Math.max(1, max);
    }

    public void setFirewallActive(boolean isActive) {
        this.isFirewallActive = isActive;
    }

    public void setMaxTime(int max) {
        this.maxSeconds = max;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Hintergrund
        g2.setColor(Theme.COLOR_PANEL_DARK);
        g2.fillRect(0, 0, w, h);

        // 2. Balkenbreite
        double percent = Math.max(0, Math.min(1, smoothPercent));
        int barWidth = (int) (w * percent);

        // 3. Farbe bestimmen
        Color color = Theme.COLOR_TIMER_HIGH; // Standard Grün

        if (isFirewallActive) {
            color = Theme.COLOR_ACCENT_BLUE;
        } else {
            double ratio = (double) currentSeconds / maxSeconds;
            if (ratio <= 0.25) color = Theme.COLOR_TIMER_LOW;      // Rot
            else if (ratio <= 0.5) color = Theme.COLOR_TIMER_MEDIUM; // Orange
        }

        g2.setColor(color);

        // 4. Zeichnen (mittig zentriert, 8px hoch)
        int y = (h - 8) / 2;
        g2.fillRoundRect(0, y, barWidth, 8, 8, 8);

        g2.dispose();
    }
}