package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Einblendbarer Hinweis für den Spieler ("Toast"-Nachricht).
 * Passt die Höhe dynamisch an den Textinhalt an.
 *
 * @author yusef03
 * @version 2.0
 */
public class TippCard extends JPanel {

    private final JTextArea textArea;
    private float alpha = 0f;
    private static final Color COLOR_GOLD = new Color(255, 200, 50);

    public TippCard() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 0));
        setBorder(BorderFactory.createEmptyBorder(15, 22, 15, 22));

        // Icon
        JLabel icon = new JLabel();
        icon.setIcon(Theme.loadIcon("icon_lightbulb.png", 32));
        icon.setVerticalAlignment(JLabel.TOP);
        add(icon, BorderLayout.WEST);

        // Text (Mehrzeilig)
        textArea = new JTextArea("TIPP: ...");
        textArea.setFont(new Font("SansSerif", Font.BOLD, 16));
        textArea.setForeground(Theme.COLOR_TEXT_PRIMARY);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(textArea, BorderLayout.CENTER);
    }

    public void showTipp(String text) {
        textArea.setText("TIPP: " + text);
        setVisible(true);
        alpha = 0f;

        // Fade-In Animation
        Timer t = new Timer(25, null);
        t.addActionListener(e -> {
            alpha += 0.1f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                t.stop();
            }
            repaint();
        });
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Erst prüfen, ob sichtbar
        if (!isVisible() || alpha <= 0) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int r = 15; // Radius

        // Fade-Effekt anwenden
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // 1. Hintergrund
        g2.setColor(new Color(30, 30, 30, (int)(230 * alpha)));
        g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, r, r));

        // 2. Rand (Gold)
        g2.setColor(new Color(COLOR_GOLD.getRed(), COLOR_GOLD.getGreen(), COLOR_GOLD.getBlue(), (int)(200 * alpha)));
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, r, r));

        g2.dispose();

        // Kinder zeichnen (Text & Icon)
        super.paintComponent(g);
    }
}