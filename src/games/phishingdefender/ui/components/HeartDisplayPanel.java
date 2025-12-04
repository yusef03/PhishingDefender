package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Zeichnet Leben als pulsierende Herz-Icons im Pause-Menü.
 *
 * @author yusef03
 * @version 2.0
 */
public class HeartDisplayPanel extends JPanel {

    private int currentLives;
    private int maxLives;
    private final List<HeartIcon> hearts;

    // Statische Icons (Ressourcen schonen)
    private static final ImageIcon HEART_FULL = Theme.loadIcon("icon_heart_full.png", 32);
    private static final ImageIcon HEART_EMPTY = Theme.loadIcon("icon_heart_empty.png", 32);

    public HeartDisplayPanel(int initialLives, int maxLives) {
        this.currentLives = initialLives;
        this.maxLives = maxLives;
        this.hearts = new ArrayList<>();
        setOpaque(false);
        updateHearts();
    }

    public void updateLives(int lives) {
        this.currentLives = lives;
        updateHearts();
    }

    private void updateHearts() {
        hearts.clear();
        for (int i = 0; i < maxLives; i++) {
            ImageIcon icon = (i < currentLives) ? HEART_FULL : HEART_EMPTY;
            hearts.add(new HeartIcon(icon, this));
        }
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (HEART_FULL == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int iconW = HEART_FULL.getIconWidth();
        int iconH = HEART_FULL.getIconHeight();

        // Icons zentrieren
        int totalW = hearts.size() * iconW + (hearts.size() - 1) * 5;
        int x = (getWidth() - totalW) / 2;
        int y = (getHeight() - iconH) / 2;

        for (HeartIcon heart : hearts) {
            heart.paint(g2, x, y);
            x += iconW + 5;
        }

        g2.dispose();
    }

    // Interne Klasse für Puls-Animation pro Herz
    private static class HeartIcon {
        private final ImageIcon icon;
        private float scale = 1.0f;
        private boolean scalingUp = true;

        public HeartIcon(ImageIcon icon, Component parent) {
            this.icon = icon;

            // Eigener Timer für asynchrones Pulsieren
            Timer t = new Timer(70, e -> {
                scale += scalingUp ? 0.03f : -0.03f;
                if (scale >= 1.1f) scalingUp = false;
                if (scale <= 0.9f) scalingUp = true;

                if (parent != null && parent.isVisible()) {
                    parent.repaint();
                }
            });
            t.setInitialDelay((int)(Math.random() * 200)); // Varianz
            t.start();
        }

        public void paint(Graphics2D g, int x, int y) {
            if (icon == null) return;

            int w = icon.getIconWidth();
            int h = icon.getIconHeight();

            int sw = (int) (w * scale);
            int sh = (int) (h * scale);

            // Zentriert skalieren
            g.drawImage(icon.getImage(), x - (sw - w) / 2, y - (sh - h) / 2, sw, sh, null);
        }
    }
}