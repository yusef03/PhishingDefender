package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Fortschrittsleiste für die "Streak" (Serie).
 * Pulsiert, wenn die Serie voll ist (Bereit für Bonus).
 *
 * @author yusef03
 * @version 2.0
 */
public class StreakBonusBar extends JPanel {

    private int currentStreak;
    private int maxStreak = 5;
    private Timer pulseTimer;
    private boolean pulseUp = false;
    private float alpha = 1.0f;

    public StreakBonusBar() {
        super();
        setOpaque(false);
        setPreferredSize(new Dimension(100, 12));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));

        // Puls-Timer für volle Leiste
        pulseTimer = new Timer(50, e -> {
            alpha += pulseUp ? 0.05f : -0.05f;
            if (alpha >= 1.0f) pulseUp = false;
            if (alpha <= 0.4f) pulseUp = true;
            repaint();
        });
    }

    public void updateStreak(int current, int max) {
        this.currentStreak = current;
        this.maxStreak = Math.max(1, max);

        if (this.currentStreak >= this.maxStreak) {
            if (!pulseTimer.isRunning()) pulseTimer.start();
        } else {
            if (pulseTimer.isRunning()) pulseTimer.stop();
            alpha = 1.0f; // Reset
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Hintergrund
        g2.setColor(new Color(10, 10, 10));
        g2.fillRoundRect(0, 0, w, h, h, h);

        // 2. Fortschritt
        double percent = Math.min(1.0, (double) currentStreak / maxStreak);
        int barWidth = (int) (w * percent);

        // 3. Füllung
        Color c = Theme.COLOR_ACCENT_ORANGE;
        if (currentStreak >= maxStreak) {
            // Pulsierendes Leuchten
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha * 255)));
        } else {
            g2.setColor(c);
        }
        g2.fillRoundRect(0, 0, barWidth, h, h, h);

        // 4. Rand
        g2.setColor(Theme.COLOR_BUTTON_NEUTRAL);
        g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

        g2.dispose();
    }
}