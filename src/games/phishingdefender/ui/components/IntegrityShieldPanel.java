package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Eine "High-Tech" Schild-Anzeige, die die Leben (System-Integrität) darstellt.
 * Das Schild "bricht" visuell, wenn der Spieler Schaden nimmt.
 * Ersetzt das einfache Herz-Label.
 *
 * @author yusef03
 * @version 1.0
 */
public class IntegrityShieldPanel extends JPanel {

    private int currentLives = 3;
    private int maxLives = 3;
    private Timer pulseTimer;
    private float pulseAlpha = 1.0f;
    private boolean isPulsingUp = false;

    // Vordefinierte Formen für das Schild und die Risse
    private Shape shieldShape;
    private Shape crackShape1;
    private Shape crackShape2;

    public IntegrityShieldPanel() {
        super();
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(100, 100)); // Feste Größe für das Schild

        // Timer für den "Puls"-Effekt
        this.pulseTimer = new Timer(50, e -> {
            if (isPulsingUp) {
                pulseAlpha += 0.05f;
                if (pulseAlpha >= 1.0f) {
                    pulseAlpha = 1.0f;
                    isPulsingUp = false;
                }
            } else {
                pulseAlpha -= 0.05f;
                if (pulseAlpha <= 0.5f) {
                    pulseAlpha = 0.5f;
                    isPulsingUp = true;
                }
            }
            repaint();
        });
        pulseTimer.start(); // Schild pulsiert immer
    }

    /**
     * Aktualisiert den Status des Schilds.
     */
    public void updateShield(int current, int max) {
        this.currentLives = current;
        this.maxLives = max;
        this.repaint();
    }

    /**
     * Definiert die Formen neu, wenn die Größe des Panels geändert wird.
     */
    private void updateShapes(int w, int h) {
        // --- 1. Das Schild (Polygon) ---
        // Wir verwenden einen Rand (padding), damit das Leuchten Platz hat
        int pad = 10;
        int innerW = w - (pad * 2);
        int innerH = h - (pad * 2);

        Path2D.Float shield = new Path2D.Float();
        shield.moveTo(pad + innerW / 2.0, pad); // Spitze Oben
        shield.lineTo(pad, pad + innerH * 0.2); // Ecke Oben-Links
        shield.lineTo(pad, pad + innerH * 0.6); // Ecke Mitte-Links
        shield.lineTo(pad + innerW / 2.0, pad + innerH); // Spitze Unten
        shield.lineTo(pad + innerW, pad + innerH * 0.6); // Ecke Mitte-Rechts
        shield.lineTo(pad + innerW, pad + innerH * 0.2); // Ecke Oben-Rechts
        shield.closePath();
        this.shieldShape = shield;

        // --- 2. Riss 1 (für 2 Leben) ---
        Path2D.Float crack1 = new Path2D.Float();
        crack1.moveTo(w * 0.3, h * 0.3);
        crack1.lineTo(w * 0.5, h * 0.5);
        crack1.lineTo(w * 0.45, h * 0.6);
        crack1.lineTo(w * 0.7, h * 0.8);
        this.crackShape1 = crack1;

        // --- 3. Riss 2 (für 1 Leben) ---
        Path2D.Float crack2 = new Path2D.Float();
        crack2.moveTo(w * 0.7, h * 0.2);
        crack2.lineTo(w * 0.55, h * 0.4);
        crack2.lineTo(w * 0.6, h * 0.5);
        crack2.lineTo(w * 0.3, h * 0.75);
        this.crackShape2 = crack2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Prüfen, ob Formen neu berechnet werden müssen
        if (shieldShape == null || shieldShape.getBounds().width != w - 20) {
            updateShapes(w, h);
        }

        // --- 1. Farbe wählen ---
        Color shieldColor;
        if (currentLives == 3) {
            shieldColor = Theme.COLOR_ACCENT_GREEN;
        } else if (currentLives == 2) {
            shieldColor = Theme.COLOR_TIMER_MEDIUM; // Orange
        } else {
            shieldColor = Theme.COLOR_BUTTON_RED; // Rot
        }

        // --- 2. Pulsierendes Leuchten (Fill) ---
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha * 0.4f)); // Schwacher Fill
        g2.setColor(shieldColor);
        g2.fill(shieldShape);

        // --- 3. Schild-Umrandung (Stroke) ---
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha)); // Starker Stroke
        g2.setColor(shieldColor);
        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(shieldShape);

        // --- 4. Risse zeichnen ---
        if (currentLives < 3) {
            g2.setColor(new Color(255, 255, 255, (int)(pulseAlpha * 150))); // Halb-transparenter weißer Riss
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(crackShape1);
        }
        if (currentLives < 2) {
            g2.draw(crackShape2);
        }

        g2.dispose();
    }
}