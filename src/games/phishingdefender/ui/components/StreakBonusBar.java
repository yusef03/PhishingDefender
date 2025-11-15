package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Eine Fortschrittsleiste, die die aktuelle "Streak" (Serie) anzeigt,
 * die für den Firewall-Bonus benötigt wird.
 *
 * @author yusef03
 * @version 1.0
 */
public class StreakBonusBar extends JPanel {

    private int currentStreak;
    private int maxStreak;
    private Timer pulseTimer;
    private boolean isPulsing = false;
    private float pulseAlpha = 1.0f; // Für den Leucht-Effekt

    public StreakBonusBar() {
        super();
        this.currentStreak = 0;
        this.maxStreak = 5; // Standardwert, wird von GameScreen überschrieben
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(100, 12));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));

        // Timer für den "Puls"-Effekt, wenn die Leiste voll ist
        this.pulseTimer = new Timer(50, e -> {
            // Lässt die Helligkeit (Alpha) auf- und abschwellen
            if (isPulsing) {
                pulseAlpha += 0.05f;
                if (pulseAlpha >= 1.0f) {
                    pulseAlpha = 1.0f;
                    isPulsing = false;
                }
            } else {
                pulseAlpha -= 0.05f;
                if (pulseAlpha <= 0.4f) {
                    pulseAlpha = 0.4f;
                    isPulsing = true;
                }
            }
            repaint();
        });
    }

    /**
     * Aktualisiert die Leiste mit dem neuen Serien-Status.
     */
    public void updateStreak(int current, int max) {
        this.currentStreak = current;
        this.maxStreak = Math.max(1, max); // Verhindert Division durch Null

        if (this.currentStreak >= this.maxStreak) {
            // Serie ist voll! Starte Puls-Timer.
            if (!pulseTimer.isRunning()) {
                pulseTimer.start();
            }
        } else {
            // Serie unterbrochen. Stoppe Puls-Timer.
            if (pulseTimer.isRunning()) {
                pulseTimer.stop();
            }
            pulseAlpha = 1.0f; // Setze Helligkeit zurück
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Hintergrund der Leiste (dunkelgrau)
        g2.setColor(new Color(10, 10, 10));
        g2.fillRoundRect(0, 0, width, height, height, height);

        // 2. Fortschritt berechnen
        double percent = (double) currentStreak / maxStreak;
        percent = Math.max(0, Math.min(1, percent));
        int barWidth = (int) (width * percent);

        // 3. Farbe wählen
        Color streakColor = Theme.COLOR_ACCENT_ORANGE; // (ist in Theme.java auf Grün gesetzt)

        if (currentStreak >= maxStreak) {
            // Wenn voll, nutze den Puls-Alpha-Wert für die Farbe
            g2.setColor(new Color(
                    streakColor.getRed(),
                    streakColor.getGreen(),
                    streakColor.getBlue(),
                    (int) (pulseAlpha * 255) // Alpha-Wert
            ));
        } else {
            g2.setColor(streakColor);
        }

        // 4. Füllung zeichnen
        g2.fillRoundRect(0, 0, barWidth, height, height, height);

        // 5. Einen sauberen Rand zeichnen
        g2.setColor(Theme.COLOR_BUTTON_NEUTRAL);
        g2.drawRoundRect(0, 0, width - 1, height - 1, height, height);

        g2.dispose();
    }
}