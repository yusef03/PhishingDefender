package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Animiertes Popup für direktes Richtig/Falsch-Feedback (Toast-Style).
 *
 * @author yusef03
 * @version 2.0
 */
public class FeedbackCard extends JPanel {

    private String text;
    private Color mainColor;
    private ImageIcon icon;
    private float alpha = 0f;

    public FeedbackCard(ImageIcon icon, String text, Color mainColor) {
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

        // Transparenz setzen
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int w = getWidth();
        int h = getHeight();
        int cornerRadius = 20;

        // 1. Hintergrund (Dunkelgrau)
        int bgAlpha = (int)(230 * alpha);
        g2.setColor(new Color(30, 30, 30, bgAlpha));
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));

        // 2. Rand (Farbig)
        int borderAlpha = (int)(200 * alpha);
        g2.setColor(new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), borderAlpha));
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, cornerRadius, cornerRadius));

        // 3. Icon
        if (icon != null) {
            int yPos = (h - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2, 15, yPos);
        }

        // 4. Text
        g2.setColor(Theme.COLOR_TEXT_PRIMARY);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString(text, 50, 32);

        g2.dispose();
    }

    // --- Setter ---
    public void setText(String text) { this.text = text; }
    public void setColor(Color mainColor) { this.mainColor = mainColor; }
    public void setIcon(ImageIcon icon) { this.icon = icon; }
}