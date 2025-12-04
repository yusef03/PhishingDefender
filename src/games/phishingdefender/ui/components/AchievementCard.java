package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Popup-Benachrichtigung für freigeschaltete Erfolge.
 *
 * @author yusef03
 * @version 2.0
 */
public class AchievementCard extends JPanel {

    private final JLabel textLabel;
    private static final Color GOLD_COLOR = new Color(255, 215, 0);

    public AchievementCard() {
        setOpaque(false);
        setVisible(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        textLabel = new JLabel();
        textLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        textLabel.setForeground(Theme.COLOR_TEXT_PRIMARY);
        textLabel.setIcon(Theme.loadIcon("icon_trophy.png", 24));

        add(textLabel);

        setPreferredSize(new Dimension(800, 65));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
    }

    public void showAchievement(String name) {
        textLabel.setText("Erfolg freigeschaltet: " + name);
        setVisible(true);

        // Timer zum Ausblenden (3 Sekunden)
        Timer hideTimer = new Timer(3000, e -> {
            setVisible(false);
            if (getParent() != null) {
                getParent().repaint();
            }
        });
        hideTimer.setRepeats(false);
        hideTimer.start();

        if (getParent() != null) {
            getParent().revalidate();
            getParent().repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) return;

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Hintergrund (Dunkel)
        g2.setColor(new Color(30, 30, 30, 230));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 10, 15, 15);

        // Rand (Gold)
        g2.setColor(new Color(GOLD_COLOR.getRed(), GOLD_COLOR.getGreen(), GOLD_COLOR.getBlue(), 200));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 10, 15, 15);

        g2.dispose();
    }
}