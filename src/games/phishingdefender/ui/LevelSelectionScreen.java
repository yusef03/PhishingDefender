package games.phishingdefender.ui;

import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.ui.components.LevelConfig;
import games.phishingdefender.PhishingDefender;
import games.phishingdefender.ui.components.Theme;
import games.phishingdefender.managers.StarsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;


/**
 * Der Bildschirm, auf dem der Spieler die Mission (Level 1-3) auswählt.
 * Zeigt für jedes Level eine Karte mit Namen, Schwierigkeit, E-Mail-Anzahl,
 * Zeitlimit und den bereits erreichten Sternen an. Schaltet Level basierend
 * auf dem Spielerfortschritt frei.
 *
 * @author yusef03
 * @version 1.0
 */

public class LevelSelectionScreen extends JPanel {

    private PhishingDefender hauptFenster;
    private StarsManager starsManager;

    public LevelSelectionScreen(PhishingDefender hauptFenster, StarsManager starsManager) { // Manager wird übergeben
        this.hauptFenster = hauptFenster;
        this.starsManager = starsManager; // Wir benutzen den zentralen Manager
        // this.starsManager = new StarsManager(hauptFenster.getSpielerName()); // WIRD GELÖSCHT
        setLayout(new BorderLayout());
        setupUI();
    }

    private void setupUI() {
        // Animierter Hintergrund
        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 20, 50));

        // Begrüßung
        JLabel greetingLabel = new JLabel("Hallo " + hauptFenster.getSpielerName() + "! 🕵️", JLabel.CENTER);
        greetingLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        greetingLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
        greetingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Titel
        JLabel titleLabel = new JLabel("🎮 WÄHLE DEINE MISSION", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Progress Bar
        // Wir fragen nicht mehr das Hauptfenster, wir wissen es selbst!
        int hoechstes = 1;
        if (starsManager.getStarsForLevel(1) > 0) hoechstes = 2;
        if (starsManager.getStarsForLevel(2) > 0) hoechstes = 3;

        JPanel progressPanel = createProgressBar(hoechstes, 3);

        topPanel.add(greetingLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        topPanel.add(progressPanel);

        // === CENTER: Level-Karten ===
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 30));
        cardsPanel.setOpaque(false);

        // Level 1
        cardsPanel.add(createLevelCard(
                LevelConfig.L1_LEVEL_NUM,
                LevelConfig.L1_NAME,
                LevelConfig.L1_ICON,
                LevelConfig.L1_GESAMT_EMAILS + " E-Mails",
                LevelConfig.L1_ZEIT + " Sek. / E-Mail",
                LevelConfig.L1_SCHWIERIGKEIT,
                LevelConfig.L1_FARBE,
                true // Level 1 ist immer frei
        ));

        // Level 2
        // Wir prüfen die Freischaltung jetzt direkt hier, mit dem frischen Manager!
        boolean level2Unlocked = starsManager.getStarsForLevel(1) > 0;
        cardsPanel.add(createLevelCard(
                LevelConfig.L2_LEVEL_NUM,
                LevelConfig.L2_NAME,
                LevelConfig.L2_ICON,
                LevelConfig.L2_GESAMT_EMAILS + " E-Mails",
                LevelConfig.L2_ZEIT + " Sek. / E-Mail",
                LevelConfig.L2_SCHWIERIGKEIT,
                LevelConfig.L2_FARBE,
                level2Unlocked
        ));

        // Level 3
        boolean level3Unlocked = starsManager.getStarsForLevel(2) > 0;
        cardsPanel.add(createLevelCard(
                LevelConfig.L3_LEVEL_NUM,
                LevelConfig.L3_NAME,
                LevelConfig.L3_ICON,
                LevelConfig.L3_GESAMT_EMAILS + " E-Mails",
                LevelConfig.L3_ZEIT + " Sek. / E-Mail",
                LevelConfig.L3_SCHWIERIGKEIT,
                LevelConfig.L3_FARBE,
                level3Unlocked
        ));

        // === Zurück-Button ===
        JButton zurueckButton = Theme.createStyledButton(
                "← ZURÜCK",
                Theme.FONT_BUTTON_MEDIUM, // 18px
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_MEDIUM // 12px padding
        );
        zurueckButton.setPreferredSize(new Dimension(200, 55));
        zurueckButton.addActionListener(e -> hauptFenster.zeigeWelcomeScreen());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        bottomPanel.add(zurueckButton);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);
        backgroundPanel.add(cardsPanel, BorderLayout.CENTER);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(backgroundPanel);
    }

    private JPanel createLevelCard(int level, String name, String icon,
                                   String emails, String time, String difficulty,
                                   Color accentColor, boolean unlocked) {

        JPanel card = new JPanel() {
            private boolean hovered = false;
            private float glowAlpha = 0f;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (unlocked) {
                            hovered = true;
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                            animateGlow(true);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        animateGlow(false);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (unlocked) {
                            hauptFenster.starteLevel(level);
                        }
                    }
                });
            }

            private void animateGlow(boolean fadeIn) {
                Timer timer = new Timer(30, null);
                timer.addActionListener(e -> {
                    if (fadeIn) {
                        glowAlpha += 0.1f;
                        if (glowAlpha >= 1.0f) {
                            glowAlpha = 1.0f;
                            timer.stop();
                        }
                    } else {
                        glowAlpha -= 0.1f;
                        if (glowAlpha <= 0f) {
                            glowAlpha = 0f;
                            timer.stop();
                        }
                    }
                    repaint();
                });
                timer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Glow Effekt beim Hover
                if (hovered && unlocked) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(),
                            accentColor.getBlue(), (int)(100 * glowAlpha)));
                    g2.fill(new RoundRectangle2D.Float(-5, -5, w + 10, h + 10, 25, 25));
                }

                // Schatten
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fill(new RoundRectangle2D.Float(5, 5, w - 5, h - 5, 20, 20));

                // Hauptkarte
                if (unlocked) {
                    GradientPaint gradient = new GradientPaint(
                            0, 0, Theme.COLOR_PANEL_DARK,
                            0, h, new Color(25, 25, 25) // Unten etwas dunkler
                    );
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(new Color(30, 30, 40, 150));
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 5, h - 5, 20, 20));

                g2.setColor(unlocked ? accentColor : new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 5, h - 5, 20, 20));

                // Highlight oben
                if (unlocked) {
                    GradientPaint highlight = new GradientPaint(
                            0, 0, new Color(255, 255, 255, 30),
                            0, h / 3, new Color(255, 255, 255, 0)
                    );
                    g2.setPaint(highlight);
                    g2.fill(new RoundRectangle2D.Float(0, 0, w - 5, h / 3, 20, 20));
                }

                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 380));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        // Icon/Emoji
        JLabel iconLabel = new JLabel(unlocked ? icon : "🔒", JLabel.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 80));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Mission Number
        JLabel missionLabel = new JLabel("MISSION " + level, JLabel.CENTER);
        missionLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        missionLabel.setForeground(unlocked ? accentColor : new Color(100, 100, 100));
        missionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Name
        JLabel nameLabel = new JLabel(name, JLabel.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(unlocked ? Color.WHITE : new Color(120, 120, 120));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // === STERNE aus StarsManager ===
        // Diese Zeile funktioniert jetzt, weil wir den zentralen, frischen starsManager benutzen!
        int sterne = starsManager.getStarsForLevel(level);

        JPanel sternePanel = new JPanel();
        sternePanel.setOpaque(false);
        sternePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 0));
        sternePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 1; i <= 3; i++) {
            boolean erreicht = (i <= sterne);
            String sternEmoji = erreicht ? "⭐" : "☆";

            JLabel sternLabel = new JLabel(sternEmoji);
            sternLabel.setFont(new Font("Arial", Font.PLAIN, 22));
            sternLabel.setForeground(erreicht ? new Color(255, 215, 0) : new Color(70, 70, 70));
            sternePanel.add(sternLabel);
        }

        // Details
        JLabel emailsLabel = new JLabel("📧 " + emails, JLabel.CENTER);
        emailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        emailsLabel.setForeground(new Color(180, 180, 180));
        emailsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timeLabel = new JLabel("⏱️ " + time, JLabel.CENTER);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        timeLabel.setForeground(new Color(180, 180, 180));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Difficulty Badge
        JLabel diffLabel = new JLabel(difficulty, JLabel.CENTER);
        diffLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        diffLabel.setForeground(unlocked ? accentColor : new Color(100, 100, 100));
        diffLabel.setOpaque(true);
        diffLabel.setBackground(unlocked ? new Color(accentColor.getRed(), accentColor.getGreen(),
                accentColor.getBlue(), 30) : new Color(50, 50, 50));
        diffLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        diffLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Zusammenbauen
        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(missionLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(sternePanel);  // ← sternePanel statt starsLabel!
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(emailsLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(timeLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(diffLabel);

        return card;
    }

    // Progress Bar
    private JPanel createProgressBar(int current, int total) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(400, 40));

        for (int i = 1; i <= total; i++) {
            JLabel dot = new JLabel(i <= current ? "●" : "○");
            dot.setFont(new Font("Arial", Font.PLAIN, 24));
            dot.setForeground(i <= current ? Theme.COLOR_ACCENT_GREEN : new Color(80, 80, 80));
            panel.add(dot);
        }

        return panel;
    }
}