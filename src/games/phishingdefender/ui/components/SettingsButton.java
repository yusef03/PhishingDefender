package games.phishingdefender.ui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.net.URL;
import javax.swing.*;

/**
 * Setting Button
 * @author yusef03
 * @version 3.0 (Spinning Animation)
 */
public class SettingsButton extends JButton {

    private double rotationAngle = 0;
    private Timer rotationTimer;
    private Image iconImage;
    private boolean isHovered = false;

    public SettingsButton() {
        // 1. Icon laden 
        try {
            URL url = getClass().getResource("/games/phishingdefender/assets/images/icon_gear.png");
            if (url != null) {
                this.iconImage = new ImageIcon(url).getImage();
            }
        } catch (Exception e) {
            setText("⚙"); // Fallback
        }

        // Button Grundeinstellungen
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(60, 60));

        // 2. Animations-Timer 
        rotationTimer = new Timer(16, e -> {
            if (isHovered) {
                rotationAngle += 5; // Schneller drehen bei Hover
            } else {
                // zurückdrehen oder weiterdrehen 
                if (rotationAngle % 360 != 0) {
                    rotationAngle += 2; // Langsam in Position drehen
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
            if (rotationAngle >= 360) rotationAngle -= 360;
            repaint();
        });

        // 3. Maus-Events
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                rotationTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                // Timer läuft weiter, bis Winkel wieder bei 0 
                // oder lassen  einfach stoppen:
                rotationTimer.stop();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (iconImage == null) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();
        int iconSize = 42;

        // Drehpunkt berechnen 
        int centerX = w / 2;
        int centerY = h / 2;

        // Rotation anwenden
        AffineTransform old = g2.getTransform();
        g2.rotate(Math.toRadians(rotationAngle), centerX, centerY);

        // Bild zeichnen (zentriert)
        int x = (w - iconSize) / 2;
        int y = (h - iconSize) / 2;

        g2.drawImage(iconImage, x, y, iconSize, iconSize, null);

        // Reset Transformation
        g2.setTransform(old);
        g2.dispose();
    }
}