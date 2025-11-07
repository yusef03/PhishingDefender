package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Eine Pop-up-Karte, die von OBEN ins Bild kommt,
 * um einen neuen Erfolg anzuzeigen.
 *
 * @author yusef03
 * @version 2.0
 */
public class AchievementCard extends JPanel {

    private JLabel textLabel;
    private static final Color ACH_COLOR = new Color(255, 215, 0);

    public AchievementCard() {
        setOpaque(false);
        setVisible(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));


        textLabel = new JLabel("🏆 Erfolg freigeschaltet: ???");
        textLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        textLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);
        textLabel.setIcon(new ImageIcon(new byte[0]));

        // Ein "Icon" direkt im Label-Text
        textLabel.setText("🏆 Erfolg freigeschaltet: ???");

        add(textLabel);


        setPreferredSize(new Dimension(800, 65));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
    }

    /**
     * Zeigt die Karte mit einem Namen an und lässt sie nach 3 Sek. verschwinden.
     */
    public void showAchievement(String name) {
        textLabel.setText("🏆 Erfolg freigeschaltet: " + name);
        setVisible(true); // Sichtbar machen

        // Ein Timer, der die Karte nach 3 Sekunden wieder versteckt
        Timer hideTimer = new Timer(3000, e -> {
            setVisible(false);
            if (getParent() != null) {
                getParent().revalidate();
                getParent().repaint();
            }
        });
        hideTimer.setRepeats(false); // Nur einmal ausführen
        hideTimer.start();

        // Dem Hauptfenster sagen, dass es sich neu zeichnen soll
        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Dieser Code wird nur ausgeführt, wenn setVisible(true) ist
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Hintergrund (semi-transparent)
        g2.setColor(new Color(30, 30, 30, 230));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 10, 15, 15);

        // Leuchtender Rand (in Gold)
        g2.setColor(new Color(ACH_COLOR.getRed(), ACH_COLOR.getGreen(), ACH_COLOR.getBlue(), 200));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 10, 15, 15);

        g2.dispose();
    }
}