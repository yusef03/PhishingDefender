package games.phishingdefender;

import javax.swing.*;
import java.awt.*;

/**
 * Ein einfacher Splash Screen (JWindow), der beim Start des Spiels angezeigt wird,
 * während im Hintergrund geladen wird (gesteuert durch den SwingWorker in PhishingDefender).
 * Zeigt Logo, Titel und einen "Wird geladen..." Text.
 *
 * @author yusef03
 * @version 1.0
 */

public class SplashScreen extends JWindow {

    public SplashScreen() {
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(
                        0, 0, Theme.COLOR_BACKGROUND_DARK,
                        0, getHeight(), new Color(10, 10, 10)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BorderLayout());

        // Logo (Text-Version wenn Bild nicht geladen werden kann)
        JLabel logoLabel;
        try {
            //Lade als Ressource ab dem Classpath-Root (src)
            java.net.URL iconURL = SplashScreen.class.getResource("/games/phishingdefender/assets/images/logo.png");

            if (iconURL == null) {
                // Wenn die Ressource nicht gefunden wird, löse einen Fehler aus
                throw new Exception("Logo-Ressource nicht gefunden unter: /games/phishingdefender/assets/images/logo.png");
            }

            ImageIcon icon = new ImageIcon(iconURL);
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaled), JLabel.CENTER);

        } catch (Exception e) {
            // Fallback, wenn das Laden fehlschlägt
            System.err.println("Fehler beim Laden des Splash-Logos: " + e.getMessage()); // Fehlermeldung ausgeben!
            e.printStackTrace(); // Stacktrace für Details ausgeben!

            logoLabel = new JLabel("🛡️", JLabel.CENTER); // Fallback-Text
            logoLabel.setFont(new Font("Arial", Font.PLAIN, 80));
        }
        // Border etc. bleiben gleich
        logoLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        // Titel
        JLabel titleLabel = new JLabel("PHISHING DEFENDER", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Theme.COLOR_ACCENT_GREEN);

        // Loading Text mit Animation-Punkten
        JLabel loadingLabel = new JLabel("Wird geladen", JLabel.CENTER);
        loadingLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        loadingLabel.setForeground(new Color(150, 150, 150));
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Animierte Punkte
        Timer dotTimer = new Timer(400, null);
        final int[] dotCount = {0};
        dotTimer.addActionListener(e -> {
            dotCount[0] = (dotCount[0] + 1) % 4;
            String dots = ".".repeat(dotCount[0]);
            loadingLabel.setText("Wird geladen" + dots);
        });
        dotTimer.start();

        // Progress Bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(new Color(30, 35, 60));
        progressBar.setForeground(Theme.COLOR_ACCENT_GREEN);
        progressBar.setBorderPainted(false);

        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 40, 60));
        progressPanel.add(progressBar);

        // Content zusammenbauen
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(logoLabel);
        contentPanel.add(titleLabel);
        contentPanel.add(loadingLabel);

        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.SOUTH);

        add(panel);
    }
}