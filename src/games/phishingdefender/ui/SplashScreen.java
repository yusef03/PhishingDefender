package games.phishingdefender.ui;

import games.phishingdefender.ui.components.Theme;
import javax.swing.*;
import java.awt.*;

/**
 * Ladebildschirm beim Start.
 *
 * @author yusef03
 * @version 2.0
 */
public class SplashScreen extends JWindow {

    public SplashScreen() {
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, Theme.COLOR_BACKGROUND_DARK, 0, getHeight(), new Color(10, 10, 10)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Logo
        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        try {
            ImageIcon icon = Theme.loadIcon("logo.png", 120);
            if (icon != null) logo.setIcon(icon);
            else logo.setText("🛡️");
        } catch (Exception e) {
            logo.setText("🛡️");
        }

        JLabel title = new JLabel("PHISHING DEFENDER", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Theme.COLOR_ACCENT_GREEN);

        JLabel status = new JLabel("Wird geladen...", JLabel.CENTER);
        status.setForeground(Color.GRAY);

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setBackground(new Color(30, 35, 60));
        bar.setForeground(Theme.COLOR_ACCENT_GREEN);
        bar.setBorderPainted(false);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        logo.setAlignmentX(CENTER_ALIGNMENT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        status.setAlignmentX(CENTER_ALIGNMENT);

        center.add(logo);
        center.add(title);
        center.add(Box.createVerticalStrut(10));
        center.add(status);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));
        bottom.add(bar, BorderLayout.CENTER);

        panel.add(center, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        add(panel);
    }
}