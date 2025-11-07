package games.phishingdefender.ui;

import games.phishingdefender.*;
import games.phishingdefender.data.HighscoreEntry;
import games.phishingdefender.managers.AchievementManager;
import games.phishingdefender.managers.HighscoreManager;
import games.phishingdefender.managers.StarsManager;
import games.phishingdefender.ui.components.AchievementCard;
import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.ui.components.LevelConfig;
import games.phishingdefender.ui.components.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Zeigt das Ergebnis nach Abschluss (oder Scheitern) eines Levels an.
 * Stellt dar, ob gewonnen/verloren, erreichte Punkte, verbleibende Leben,
 * Genauigkeit und Rang. Berechnet und zeigt die verdienten Sterne an.
 * Bietet Buttons zum Wiederholen, nächsten Level oder zur Levelauswahl.
 *
 * @author yusef03
 * @version 1.0
 */

public class ResultScreen extends JPanel {

    private PhishingDefender hauptFenster;
    private int level;
    private int score;
    private int leben;
    private int maxLeben;
    private int gesamtEmails;
    private boolean gewonnen;
    private int erreichteSterne;
    private StarsManager starsManager;
    private AchievementManager achievementManager;
    private AchievementCard achievementCard;

    public ResultScreen(PhishingDefender hauptFenster, int level, int score,
                        int leben, int maxLeben, int gesamtEmails, boolean gewonnen, AchievementManager manager) {
        this.hauptFenster = hauptFenster;
        this.level = level;
        this.score = score;
        this.leben = leben;
        this.maxLeben = maxLeben;
        this.gesamtEmails = gesamtEmails;
        this.gewonnen = gewonnen;

        // Sterne berechnen (mit Spieler-Name!)
        starsManager = new StarsManager(hauptFenster.getSpielerName());
        int richtigeAntworten = score / 10; // 10 Punkte pro richtige Antwort
        this.erreichteSterne = gewonnen ? StarsManager.berechneSterne(richtigeAntworten, gesamtEmails, leben, maxLeben) : 0;


        this.achievementManager = manager;
        // Sterne speichern (nur wenn gewonnen!)
        if (gewonnen) {
            starsManager.updateStars(level, erreichteSterne);

            // === HIER ACHIEVEMENTS VERGEBEN & POPUP ZEITLICH STEUERN ===
            boolean levelCompleteNeu = achievementManager.unlockAchievement("L" + level + "_COMPLETE");
            boolean levelPerfectNeu = false;
            if (erreichteSterne == 3) {
                levelPerfectNeu = achievementManager.unlockAchievement("L" + level + "_PERFECT");
            }

            final boolean finalLevelPerfectNeu = levelPerfectNeu;

            Timer achTimer1 = new Timer(1500, e -> { // 1.5 Sek. Verzögerung
                if (levelCompleteNeu) {
                    achievementCard.showAchievement(getLevelAchievementName(level, false));
                }
            });
            achTimer1.setRepeats(false);
            achTimer1.start();

            // NUR wenn 3 Sterne NEU sind, starte den zweiten Timer
            if (finalLevelPerfectNeu) { // <-- Prüfe die Variable
                Timer achTimer2 = new Timer(4500, e -> {
                    achievementCard.showAchievement(getLevelAchievementName(level, true));
                });
                achTimer2.setRepeats(false);
                achTimer2.start();
            }
            // === ENDE ===

            // === Meta-Achievements prüfen (Makellos & Legende) ===

            //1. "Makellos"-Achievement (kein Leben verloren)
            boolean makellosNeu = false;
            if (leben == maxLeben) { // Prüft auf volle Leben
                // Wir benutzen "NO_MISTAKES_L" + die aktuelle Level-Nummer
                makellosNeu = achievementManager.unlockAchievement("NO_MISTAKES_L" + level);
            }

            // 2. "Cyber-Legende"-Achievement (alle 3-Sterne-Erfolge geholt)
            boolean legendeNeu = false;
            //prüfen, ob die 3 "Perfekt"-Erfolge (die schon existieren) alle freigeschaltet sind
            if (achievementManager.isUnlocked("L1_PERFECT") &&
                    achievementManager.isUnlocked("L2_PERFECT") &&
                    achievementManager.isUnlocked("L3_PERFECT"))
            {
                legendeNeu = achievementManager.unlockAchievement("CYBER_LEGEND");
            }


            final boolean finalMakellosNeu = makellosNeu;
            final boolean finalLegendeNeu = legendeNeu;

            Timer achTimer3 = new Timer(6500, e -> {
                if (finalMakellosNeu) {
                    achievementCard.showAchievement("Makellos (Level " + level + ")");
                }
            });
            achTimer3.setRepeats(false);
            achTimer3.start();

            Timer achTimer4 = new Timer(8500, e -> {
                if (finalLegendeNeu) {
                    achievementCard.showAchievement("Cyber-Legende!");
                }
            });
            achTimer4.setRepeats(false);
            achTimer4.start();

            achievementManager.unlockAchievement("L" + level + "_COMPLETE");
            if (erreichteSterne == 3) {
                achievementManager.unlockAchievement("L" + level + "_PERFECT");
            }

            // Highscore hinzufügen!
            HighscoreManager highscoreManager = new HighscoreManager();
            int genauigkeit = Math.min(100, (richtigeAntworten * 100) / gesamtEmails);
            highscoreManager.hinzufuegen(hauptFenster.getSpielerName(), score, genauigkeit, level);
        }

        setLayout(new BorderLayout());
        setupUI();
    }

    private void setupUI() {
        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // === TOP: Großer Titel ===
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 30, 50));

        String titelText = gewonnen ? "🎉 LEVEL GESCHAFFT! 🎉" : "💔 LEVEL VERLOREN 💔";
        Color titelFarbe = gewonnen ? new Color(80, 200, 120) : new Color(255, 100, 100);

        JLabel titelLabel = new JLabel(titelText, JLabel.CENTER);
        titelLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        titelLabel.setForeground(titelFarbe);
        titelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel levelLabel = new JLabel("Mission " + level, JLabel.CENTER);
        levelLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        levelLabel.setForeground(new Color(180, 180, 180));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titelLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(levelLabel);

        // === CENTER: Stats Cards ===
        JPanel statsWrapper = new JPanel(new GridBagLayout());
        statsWrapper.setOpaque(false);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 2, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(700, 300));

        // Genauigkeit berechnen
        int genauigkeit = gesamtEmails > 0 ? (int)((double)(score / 10) / gesamtEmails * 100) : 0;

        // Rang berechnen
        int rang = getRangPlatzierung(score);

        // Stats Cards erstellen
        statsPanel.add(createStatCard("⭐", "PUNKTE", String.valueOf(score), new Color(255, 200, 80)));
        statsPanel.add(createStatCard("❤️", "LEBEN", leben + "/" + maxLeben, new Color(255, 100, 100)));
        statsPanel.add(createStatCard("🎯", "GENAUIGKEIT", genauigkeit + "%", Theme.COLOR_ACCENT_GREEN)); // <-- Grün
        statsPanel.add(createStatCard("🏆", "RANG", "#" + rang + " von " + LevelConfig.GESAMT_EINZIGARTIGE_EMAILS_DB, Theme.COLOR_TEXT_SECONDARY)); // <-- Grau
        // === STERNE PANEL (nur wenn gewonnen!) ===
        JPanel sterneWrapper = new JPanel();
        sterneWrapper.setOpaque(false);
        sterneWrapper.setLayout(new BoxLayout(sterneWrapper, BoxLayout.Y_AXIS));
        sterneWrapper.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));

        if (gewonnen) {
            // Titel
            JLabel sterneTitel = new JLabel("ERREICHTE STERNE", JLabel.CENTER);
            sterneTitel.setFont(new Font("SansSerif", Font.BOLD, 18));
            sterneTitel.setForeground(new Color(180, 180, 180));
            sterneTitel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Sterne Container
            JPanel sternePanel = new JPanel();
            sternePanel.setOpaque(false);
            sternePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

            // 3 Sterne erstellen
            for (int i = 1; i <= 3; i++) {
                boolean erreicht = (i <= erreichteSterne);
                JLabel stern = createSternLabel(erreicht, i);
                sternePanel.add(stern);
            }

            // Text unter Sternen
            String sterneText = getSterneText(erreichteSterne);
            JLabel sterneTextLabel = new JLabel(sterneText, JLabel.CENTER);
            sterneTextLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            sterneTextLabel.setForeground(new Color(255, 215, 0));
            sterneTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            sterneWrapper.add(sterneTitel);
            sterneWrapper.add(Box.createRigidArea(new Dimension(0, 10)));
            sterneWrapper.add(sternePanel);
            sterneWrapper.add(Box.createRigidArea(new Dimension(0, 10)));
            sterneWrapper.add(sterneTextLabel);

        }


        // === BOTTOM: Buttons ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 30));
        buttonPanel.setOpaque(false);

        if (gewonnen && level < 3) {
            // GEWONNEN + nicht letztes Level → Nächstes Level + Level Auswahl
            JButton naechstesButton = Theme.createStyledButton(
                    "▶ NÄCHSTES LEVEL",
                    Theme.FONT_BUTTON_LARGE,
                    Theme.COLOR_BUTTON_GREEN,
                    Theme.COLOR_BUTTON_GREEN_HOVER,
                    Theme.PADDING_BUTTON_LARGE
            );
            naechstesButton.setPreferredSize(new Dimension(280, 65));
            naechstesButton.addActionListener(e -> {
                hauptFenster.levelGeschafft(level);
                hauptFenster.starteLevel(level + 1);
            });

            JButton auswahlButton = Theme.createStyledButton(
                    "🎮 LEVEL AUSWAHL",
                    Theme.FONT_BUTTON_MEDIUM, // 18px
                    Theme.COLOR_BUTTON_GREY,
                    Theme.COLOR_BUTTON_GREY_HOVER,
                    Theme.PADDING_BUTTON_LARGE // Großes Padding
            );
            auswahlButton.setPreferredSize(new Dimension(280, 65));
            auswahlButton.addActionListener(e -> {
                hauptFenster.levelGeschafft(level);
                hauptFenster.zeigeLevelAuswahl();
            });

            buttonPanel.add(naechstesButton);
            buttonPanel.add(auswahlButton);

        } else if (gewonnen && level >= 3) {
            // GEWONNEN + letztes Level → NUR Level Auswahl (+ Gratulation!)
            JLabel gratulationLabel = new JLabel("🎊 ALLE LEVEL GESCHAFFT! DU BIST EIN MEISTER! 🎊", JLabel.CENTER);
            gratulationLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            gratulationLabel.setForeground(new Color(255, 215, 0));
            gratulationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel gratPanel = new JPanel();
            gratPanel.setOpaque(false);
            gratPanel.add(gratulationLabel);

            JButton auswahlButton = Theme.createStyledButton(
                    "🏆 ZURÜCK ZUR LEVEL AUSWAHL",
                    Theme.FONT_BUTTON_LARGE, // 22px
                    Theme.COLOR_BUTTON_GREEN,
                    Theme.COLOR_BUTTON_GREEN_HOVER,
                    Theme.PADDING_BUTTON_LARGE
            );
            auswahlButton.setPreferredSize(new Dimension(400, 65));
            auswahlButton.addActionListener(e -> {
                hauptFenster.levelGeschafft(level);
                hauptFenster.zeigeLevelAuswahl();
            });

            JPanel finalPanel = new JPanel();
            finalPanel.setLayout(new BoxLayout(finalPanel, BoxLayout.Y_AXIS));
            finalPanel.setOpaque(false);
            gratulationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            auswahlButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            finalPanel.add(gratPanel);
            finalPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            finalPanel.add(auswahlButton);

            buttonPanel.add(finalPanel);

        } else {
            // VERLOREN → Wiederholen + Level Auswahl
            JButton wiederholenButton = Theme.createStyledButton(
                    "🔄 WIEDERHOLEN",
                    Theme.FONT_BUTTON_LARGE,
                    Theme.COLOR_ACCENT_ORANGE,
                    Theme.COLOR_ACCENT_ORANGE_HOVER,
                    Theme.PADDING_BUTTON_LARGE
            );
            wiederholenButton.setPreferredSize(new Dimension(280, 65));
            wiederholenButton.addActionListener(e -> hauptFenster.starteLevel(level));

            JButton auswahlButton = Theme.createStyledButton(
                    "🎮 LEVEL AUSWAHL",
                    Theme.FONT_BUTTON_MEDIUM,
                    Theme.COLOR_BUTTON_GREY,
                    Theme.COLOR_BUTTON_GREY_HOVER,
                    Theme.PADDING_BUTTON_LARGE
            );
            auswahlButton.setPreferredSize(new Dimension(280, 65));
            auswahlButton.addActionListener(e -> hauptFenster.zeigeLevelAuswahl());

            buttonPanel.add(wiederholenButton);
            buttonPanel.add(auswahlButton);
        }

        statsWrapper.add(statsPanel);

        // Center Panel mit Stats + Sterne
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel);
        if (gewonnen) {
            centerPanel.add(sterneWrapper);
        }

        // === NEUER TOP-WRAPPER ===
        achievementCard = new AchievementCard(); // Karte erstellen

        JPanel topAreaWrapper = new JPanel();
        topAreaWrapper.setOpaque(false);
        topAreaWrapper.setLayout(new BoxLayout(topAreaWrapper, BoxLayout.Y_AXIS));

        topAreaWrapper.add(achievementCard); // Zuerst die (unsichtbare) Karte
        topAreaWrapper.add(topPanel); // Dann der Titel

        backgroundPanel.add(topAreaWrapper, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(backgroundPanel);
    }

    // Erstellt eine Stat Card
    private JPanel createStatCard(String icon, String label, String value, Color accentColor) {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Schatten
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(5, 5, w - 5, h - 5, 20, 20);

                g2.setPaint(Theme.COLOR_PANEL_DARK);
                g2.fillRoundRect(0, 0, w - 5, h - 5, 20, 20);

                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(),
                        accentColor.getBlue(), 150));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, w - 5, h - 5, 20, 20);

                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 15, 18, 15));

        // Icon
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label
        JLabel textLabel = new JLabel(label, JLabel.CENTER);
        textLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        textLabel.setForeground(new Color(140, 140, 140));
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Value
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(textLabel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(valueLabel);

        return card;
    }

    // Berechnet Rang-Platzierung
    private int getRangPlatzierung(int score) {
        HighscoreManager manager = new HighscoreManager();
        java.util.List<HighscoreEntry> top10 = manager.getTop10();

        int rang = 1;
        for (HighscoreEntry entry : top10) {
            if (score < entry.getPunkte()) {
                rang++;
            }
        }

        return Math.min(rang, LevelConfig.GESAMT_EINZIGARTIGE_EMAILS_DB);
    }

    // Erstellt ein Stern-Label (erreicht oder nicht)
    private JLabel createSternLabel(boolean erreicht, int sternNummer) {
        String emoji = erreicht ? "⭐" : "☆";

        JLabel stern = new JLabel(emoji, JLabel.CENTER);
        stern.setFont(new Font("Arial", Font.PLAIN, 60));
        stern.setForeground(erreicht ? new Color(255, 215, 0) : new Color(80, 80, 80));

        // Animation für erreichte Sterne
        if (erreicht) {
            Timer animTimer = new Timer(300 * sternNummer, e -> {
                // Sound-Effekt
                stern.setFont(new Font("Arial", Font.PLAIN, 70));
                Timer scaleBack = new Timer(150, ev -> {
                    stern.setFont(new Font("Arial", Font.PLAIN, 60));
                });
                scaleBack.setRepeats(false);
                scaleBack.start();
            });
            animTimer.setRepeats(false);
            animTimer.start();
        }

        return stern;
    }

    // Gibt passenden Text für Sterne-Anzahl
    private String getSterneText(int sterne) {
        switch (sterne) {
            case 3: return "⭐⭐⭐ PERFEKT! MEISTERHAFT! 🏆";
            case 2: return "⭐⭐ SEHR GUT! WEITER SO! 💪";
            case 1: return "⭐ GESCHAFFT! VERSUCH'S NOCHMAL FÜR MEHR! 🎯";
            default: return "";
        }
    }
    /** Holt den Anzeigenamen für das Level-Achievement */
    private String getLevelAchievementName(int level, boolean perfect) {
        if (perfect) {
            if (level == 1) return "Perfekter Anfänger";
            if (level == 2) return "Perfekt Fortgeschritten";
            if (level == 3) return "Meister-Detektiv";
        } else {
            if (level == 1) return "Anfänger-Detektiv";
            if (level == 2) return "Fortgeschritten";
            if (level == 3) return "Experte";
        }
        return "";
    }
}