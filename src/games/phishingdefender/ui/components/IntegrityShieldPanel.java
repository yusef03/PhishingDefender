package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * HUD-Element: High-Tech Schildanzeige für Leben.
 * Ändert Farbe und Form (Risse) basierend auf Lebenspunkten.
 *
 * @author yusef03
 * @version 2.0
 */
public class IntegrityShieldPanel extends JPanel {

    private int currentLives = 3;
    private float pulseAlpha = 1.0f;
    private boolean pulsingUp = false;

    // Gecachte Formen (Performance)
    private Shape shieldShape, crack1, crack2;

    public IntegrityShieldPanel() {
        super();
        setOpaque(false);
        setPreferredSize(new Dimension(100, 100));

        // Zentraler Animations-Timer (50ms = 20 FPS)
        new Timer(50, e -> {
            pulseAlpha += pulsingUp ? 0.05f : -0.05f;
            if (pulseAlpha >= 1.0f) pulsingUp = false;
            if (pulseAlpha <= 0.5f) pulsingUp = true;
            repaint();
        }).start();
    }

    public void updateShield(int current, int max) {
        this.currentLives = current;
        repaint();
    }

    private void updateShapes(int w, int h) {
        int pad = 10;
        int iw = w - 20;
        int ih = h - 20;

        // Schild-Kontur
        Path2D.Float shield = new Path2D.Float();
        shield.moveTo(pad + iw / 2.0, pad);
        shield.lineTo(pad, pad + ih * 0.2);
        shield.lineTo(pad, pad + ih * 0.6);
        shield.lineTo(pad + iw / 2.0, pad + ih);
        shield.lineTo(pad + iw, pad + ih * 0.6);
        shield.lineTo(pad + iw, pad + ih * 0.2);
        shield.closePath();
        this.shieldShape = shield;

        // Riss 1
        Path2D.Float c1 = new Path2D.Float();
        c1.moveTo(w * 0.3, h * 0.3);
        c1.lineTo(w * 0.5, h * 0.5);
        c1.lineTo(w * 0.45, h * 0.6);
        c1.lineTo(w * 0.7, h * 0.8);
        this.crack1 = c1;

        // Riss 2
        Path2D.Float c2 = new Path2D.Float();
        c2.moveTo(w * 0.7, h * 0.2);
        c2.lineTo(w * 0.55, h * 0.4);
        c2.lineTo(w * 0.6, h * 0.5);
        c2.lineTo(w * 0.3, h * 0.75);
        this.crack2 = c2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (shieldShape == null || shieldShape.getBounds().width != w - 20) {
            updateShapes(w, h);
        }

        // Farbe je nach Zustand
        Color color = Theme.COLOR_ACCENT_GREEN;
        if (currentLives == 2) color = Theme.COLOR_TIMER_MEDIUM;
        if (currentLives <= 1) color = Theme.COLOR_BUTTON_RED;

        // 1. Fill (Leuchten)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha * 0.4f));
        g2.setColor(color);
        g2.fill(shieldShape);

        // 2. Stroke (Rand)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha));
        g2.setColor(color);
        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(shieldShape);

        // 3. Risse (bei Schaden)
        if (currentLives < 3) {
            g2.setColor(new Color(255, 255, 255, (int)(pulseAlpha * 150)));
            g2.setStroke(new BasicStroke(2.0f));
            g2.draw(crack1);
        }
        if (currentLives < 2) {
            g2.draw(crack2);
        }

        g2.dispose();
    }
}