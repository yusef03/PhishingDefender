package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Eine visuelle, *dünne* Timer-Leiste.
 * Sie zeichnet NUR die Leiste, keinen Text.
 *
 * @author yusef03
 * @version 2.0
 */
public class TimerBarPanel extends JPanel {

    private int currentSeconds = 0;
    private int maxSeconds = 1;
    private boolean isFirewallActive = false;

    // NEU: Wir speichern den genauen Prozentsatz für eine flüssige Animation
    private double smoothPercent = 1.0;

    public TimerBarPanel() {
        super();
        // Wir setzen nur die Hintergrundfarbe. Die Größe wird vom Layout-Manager bestimmt.
        this.setOpaque(true);
        this.setBackground(Theme.COLOR_PANEL_DARK);
        // Die Leiste soll nicht hoch sein
        this.setPreferredSize(new Dimension(100, 10)); // 10px Höhe
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10)); // Max. 10px Höhe
    }

    /**
     * Aktualisiert die Leiste mit einem genauen Prozentsatz (0.0 - 1.0)
     * für eine flüssige Animation.
     */
    public void updateSmooth(double percent) {
        this.smoothPercent = percent;
        this.repaint(); // Löst paintComponent aus
    }

    /**
     * Aktualisiert die Leiste mit Sekunden (wird für die Farbauswahl benötigt)
     */
    public void updateTime(int aktuell, int max) {
        this.currentSeconds = aktuell;
        this.maxSeconds = Math.max(1, max);
        // repaint() wird durch updateSmooth() ausgelöst
    }

    /**
     * Teilt der Leiste mit, ob der Firewall-Bonus aktiv ist.
     */
    public void setFirewallActive(boolean isActive) {
        this.isFirewallActive = isActive;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Hintergrund zeichnen (die "leere" Leiste)
        g2.setColor(Theme.COLOR_PANEL_DARK);
        g2.fillRect(0, 0, width, height);

        // 2. Zeit-Prozentsatz und Füllbreite berechnen
        // Wir verwenden jetzt smoothPercent!
        double percent = Math.max(0, Math.min(1, smoothPercent));
        int barWidth = (int) (width * percent);

        // 3. Farbe für die Füllung wählen (basierend auf den Sekunden)
        double secondPercent = (double) currentSeconds / maxSeconds;
        Color barColor;
        if (isFirewallActive) {
            barColor = Theme.COLOR_ACCENT_BLUE; // Blau für Firewall
        } else if (secondPercent <= 0.25) {
            barColor = Theme.COLOR_TIMER_LOW; // Rot
        } else if (secondPercent <= 0.5) {
            barColor = Theme.COLOR_TIMER_MEDIUM; // Orange
        } else {
            barColor = Theme.COLOR_TIMER_HIGH; // Grün
        }
        g2.setColor(barColor);

        // 4. Füllung zeichnen
        // zeichnen sie nur 8px hoch in der Mitte
        int yOffset = (height - 8) / 2;
        g2.fillRoundRect(0, yOffset, barWidth, 8, 8, 8);

        g2.dispose();
    }

    public void setMaxTime(int maxSekunden) {
    }
}