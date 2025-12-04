package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * High-Tech Hexagon-Anzeige für den Score.
 * Pulsierender Rand-Effekt zur visuellen Hervorhebung.
 *
 * @author yusef03
 * @version 2.0
 */
public class ScoreDisplayPanel extends JPanel {

    private String scoreText = "0";
    private float pulseAlpha = 1.0f;
    private boolean pulsingUp = false;
    private Shape hexagonShape;

    private static final Color SCORE_COLOR = new Color(255, 215, 100);

    public ScoreDisplayPanel() {
        super();
        setOpaque(false);
        setPreferredSize(new Dimension(100, 100));

        // Animationstimer
        new Timer(50, e -> {
            pulseAlpha += pulsingUp ? 0.05f : -0.05f;
            if (pulseAlpha >= 1.0f) pulsingUp = false;
            if (pulseAlpha <= 0.5f) pulsingUp = true;
            repaint();
        }).start();
    }

    public void setScore(int score) {
        this.scoreText = String.valueOf(score);
        repaint();
    }

    // Erstellt die Hexagon-Form
    private void updateShape(int w, int h) {
        int pad = 10;
        int iw = w - 20;
        int ih = h - 20;

        Path2D.Float hexagon = new Path2D.Float();
        hexagon.moveTo(pad + iw / 2.0, pad);
        hexagon.lineTo(pad + iw, pad + ih * 0.25);
        hexagon.lineTo(pad + iw, pad + ih * 0.75);
        hexagon.lineTo(pad + iw / 2.0, pad + ih);
        hexagon.lineTo(pad, pad + ih * 0.75);
        hexagon.lineTo(pad, pad + ih * 0.25);
        hexagon.closePath();
        this.hexagonShape = hexagon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (hexagonShape == null || hexagonShape.getBounds().width != w - 20) {
            updateShape(w, h);
        }

        // 1. Füllung (Leuchten)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha * 0.3f));
        g2.setColor(SCORE_COLOR);
        g2.fill(hexagonShape);

        // 2. Rahmen
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha));
        g2.setColor(SCORE_COLOR);
        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(hexagonShape);

        // 3. Text
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Dynamische Schriftgröße
        int fontSize = scoreText.length() > 4 ? 32 : (scoreText.length() > 3 ? 40 : 48);
        g2.setFont(new Font("Monospace", Font.BOLD, fontSize));

        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(scoreText)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

        g2.drawString(scoreText, tx, ty);
        g2.dispose();
    }
}