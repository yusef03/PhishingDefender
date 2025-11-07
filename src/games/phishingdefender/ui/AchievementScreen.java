package games.phishingdefender.ui;

import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.PhishingDefender;
import games.phishingdefender.ui.components.Theme;
import games.phishingdefender.data.Achievement;
import games.phishingdefender.managers.AchievementManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Zeigt eine Liste aller verfügbaren Achievements (Erfolge) an.
 * Unterscheidet visuell zwischen freigeschalteten und gesperrten Erfolgen.
 * Lädt die Daten über den AchievementManager für den aktuellen Spieler.
 *
 * @author yusef03
 * @version 1.0
 */
public class AchievementScreen extends JPanel {

    private PhishingDefender hauptFenster;
    private AchievementManager manager;

    public AchievementScreen(PhishingDefender hauptFenster) {
        this.hauptFenster = hauptFenster;
        // WICHTIG: Manager für den aktuellen Spieler laden!
        this.manager = new AchievementManager(hauptFenster.getSpielerName());

        setLayout(new BorderLayout());
        setupUI();
    }

    private void setupUI() {
        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60)); // Standard-Padding

        // === TOP PANEL (Titel & Fortschritt) ===
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🏆 ERFOLGE 🏆", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(new Color(255, 215, 0)); // Goldfarbe
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Zähle, wie viele freigeschaltet sind
        long unlockedCount = manager.getAllAchievements().stream().filter(Achievement::isUnlocked).count();
        long totalCount = manager.getAllAchievements().size();

        JLabel subtitleLabel = new JLabel(
                ">>> " + unlockedCount + " / " + totalCount + " FREIGESCHALTET <<<",
                JLabel.CENTER
        );
        subtitleLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        subtitleLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        topPanel.add(subtitleLabel);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        // === CENTER (Liste der Erfolge) ===
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        List<Achievement> allAchievements = manager.getAllAchievements();

        if (allAchievements.isEmpty()) {
            JLabel emptyLabel = new JLabel("Keine Erfolge definiert.", JLabel.CENTER);
            emptyLabel.setFont(Theme.FONT_BUTTON_MEDIUM);
            emptyLabel.setForeground(Theme.COLOR_TEXT_SECONDARY);
            listPanel.add(emptyLabel);
        } else {
            for (Achievement ach : allAchievements) {
                listPanel.add(createAchievementCard(ach));
                listPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Abstand
            }
        }

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // === BOTTOM (Zurück-Button) ===
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        footerPanel.setOpaque(false);

        JButton backBtn = Theme.createStyledButton(
                "← Zurück zum Menü",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        backBtn.addActionListener(e -> hauptFenster.zeigeWelcomeScreen());

        footerPanel.add(backBtn);
        backgroundPanel.add(footerPanel, BorderLayout.SOUTH);

        // Alles zum Hauptpanel hinzufügen
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Erstellt eine einzelne "Karte" für die Achievement-Liste.
     * Stellt freigeschaltete Erfolge (farbig) und gesperrte (grau) unterschiedlich dar.
     */
    private JPanel createAchievementCard(Achievement ach) {

        JPanel card = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int cornerRadius = 15;

                if (ach.isUnlocked()) {
                    g2.setColor(Theme.COLOR_PANEL_DARK);
                    g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
                    g2.setColor(Theme.COLOR_ACCENT_GREEN);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
                } else {
                    g2.setColor(new Color(30, 30, 30, 200)); // Dunkler als PANEL_DARK
                    g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
                    g2.setColor(new Color(80, 80, 80));
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
                }
                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        // Verhindert, dass Karten in die Höhe wachsen
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // === LINKS: Icon ===
        JLabel iconLabel;
        if (ach.isUnlocked()) {
            iconLabel = new JLabel(ach.getIcon());
            iconLabel.setFont(new Font("Arial", Font.PLAIN, 40));
            iconLabel.setForeground(new Color(255, 215, 0)); // Gold
        } else {
            iconLabel = new JLabel("🔒");
            iconLabel.setFont(new Font("Arial", Font.PLAIN, 40));
            iconLabel.setForeground(Theme.COLOR_BUTTON_GREY); // Grau
        }
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        card.add(iconLabel, BorderLayout.WEST);

        // === MITTE: Text (Name & Beschreibung) ===
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Vertikal zentrieren

        JLabel nameLabel;
        if (ach.isUnlocked()) {
            nameLabel = new JLabel(ach.getName());
            nameLabel.setFont(Theme.FONT_BUTTON_MEDIUM); // 18px Bold
            nameLabel.setForeground(Color.WHITE);
        } else {
            nameLabel = new JLabel("???????"); // Name versteckt
            nameLabel.setFont(Theme.FONT_BUTTON_MEDIUM);
            nameLabel.setForeground(Theme.COLOR_BUTTON_GREY);
        }

        // Beschreibung ist immer sichtbar, damit der Spieler weiß, was zu tun ist
        JLabel descLabel = new JLabel(ach.getDescription());
        descLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        descLabel.setForeground(Theme.COLOR_TEXT_SECONDARY);

        textPanel.add(nameLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }
}