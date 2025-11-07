package games.phishingdefender;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Eine kleine, animierte "Karte" (Modern Toast Style), die dem Spieler
 * direktes Feedback (Richtig/Falsch) gibt.
 * Erscheint mit einem Fade-In Effekt.
 *
 * @author yusef03
 * @version 2.0 (Modern Dark Rework)
 */

public class FeedbackCard extends JPanel {

    private String text;
    private Color mainColor;
    private String icon;
    private float alpha = 0f;

    public FeedbackCard(String icon, String text, Color mainColor) {
        this.icon = icon;
        this.text = text;
        this.mainColor = mainColor;

        setOpaque(false);
        setPreferredSize(new Dimension(350, 50));
        setMaximumSize(new Dimension(400, 50));
    }

    public void showWithAnimation() {
        setVisible(true);
        alpha = 0f;

        Timer fadeTimer = new Timer(20, null);
        fadeTimer.addActionListener(e -> {
            alpha += 0.08f;
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

        if (!isVisible() || alpha <= 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cornerRadius = 20;

        // Alpha für Fade-In
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));


        // 1. Hintergrund (Dunkelgrau, fast wie Theme.PANEL_DARK)
        int bgAlpha = (int)(230 * alpha); // 230 = fast opak
        g2.setColor(new Color(30, 30, 30, bgAlpha));
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));

        // 2. Rand (in Akzentfarbe: Grün oder Rot)
        int borderAlpha = (int)(200 * alpha);
        g2.setColor(new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), borderAlpha));
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));

        // 3. Icon (in Akzentfarbe)
        g2.setColor(mainColor);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.drawString(icon, 15, 33);

        // 4. Text (Immer weiß)
        g2.setColor(Theme.COLOR_TEXT_PRIMARY); // Weiß
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString(text, 50, 32);

        g2.dispose();
    }
}