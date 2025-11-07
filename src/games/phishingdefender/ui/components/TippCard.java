package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Eine animierte Karte (Modern Toast Style), die einen spezifischen Tipp
 * zur E-Mail anzeigt.
 * Erscheint mit einem Fade-In Effekt.
 *
 * @author yusef03
 * @version 2.0
 */

public class TippCard extends JPanel {

    private String tippText;
    private float alpha = 0f;
    // Die neue Akzentfarbe für Tipps (Gold/Gelb)
    private static final Color TIPP_COLOR = new Color(255, 200, 50);

    public TippCard() {
        setOpaque(false);
        setPreferredSize(new Dimension(800, 80));
        setMaximumSize(new Dimension(850, 80));
    }

    public void showTipp(String text) {
        this.tippText = text;
        setVisible(true);
        alpha = 0f;

        Timer fadeTimer = new Timer(25, null);
        fadeTimer.addActionListener(e -> {
            alpha += 0.1f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadeTimer.stop();
            }
            repaint();
        });
        fadeTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isVisible() || alpha <= 0 || tippText == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cornerRadius = 15;

        // Alpha für Fade-In
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));


        // 1. Hintergrund (Dunkelgrau)
        int bgAlpha = (int)(230 * alpha);
        g2.setColor(new Color(30, 30, 30, bgAlpha));
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));

        // 2. Leuchtender Rand (in Gold/Gelb)
        int borderAlpha = (int)(200 * alpha);
        g2.setColor(new Color(TIPP_COLOR.getRed(), TIPP_COLOR.getGreen(), TIPP_COLOR.getBlue(), borderAlpha));
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));

        // 3. Icon (in Gold/Gelb)
        g2.setFont(new Font("Arial", Font.PLAIN, 32));
        g2.setColor(TIPP_COLOR);
        g2.drawString("💡", 22, 52);

        // 4. Text (Weiß)
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.setColor(Theme.COLOR_TEXT_PRIMARY); // Weiß

        // Text umbrechen
        FontMetrics fm = g2.getFontMetrics();
        int maxWidth = w - 90;
        String displayText = tippText;

        if (fm.stringWidth(tippText) > maxWidth) {
            displayText = "TIPP: " + tippText.substring(0, Math.min(80, tippText.length())) + "...";
        } else {
            displayText = "TIPP: " + tippText;
        }

        g2.drawString(displayText, 70, 52);

        g2.dispose();
    }
}