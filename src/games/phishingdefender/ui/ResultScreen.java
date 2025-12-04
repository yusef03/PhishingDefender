package games.phishingdefender.ui;

import games.phishingdefender.*;
import games.phishingdefender.managers.*;
import games.phishingdefender.ui.components.*;

import javax.swing.*;
import java.awt.*;

/**
 * Ergebnis-Bildschirm nach Level-Ende.
 * Berechnet Sterne, schaltet Achievements frei und zeigt Statistiken.
 *
 * @author yusef03
 * @version 2.0
 */
public class ResultScreen extends JPanel {

    private final PhishingDefender hauptFenster;
    private final AchievementManager achievementManager;
    private AchievementCard achievementCard;

    private final int level, score, leben, maxLeben, gesamtEmails;
    private final boolean gewonnen;
    private final int erreichteSterne;

    public ResultScreen(PhishingDefender fenster, int level, int score, int leben, int max, int mails, boolean win, StarsManager stars, AchievementManager achMgr) {
        this.hauptFenster = fenster;
        this.level = level;
        this.score = score;
        this.leben = leben;
        this.maxLeben = max;
        this.gesamtEmails = mails;
        this.gewonnen = win;
        this.achievementManager = achMgr;

        int richtige = score / LevelConfig.PUNKTE_NORMAL;
        this.erreichteSterne = win ? StarsManager.berechneSterne(richtige, gesamtEmails, leben, maxLeben) : 0;

        if (win) {
            stars.updateStars(level, erreichteSterne);
            verarbeiteErfolge(richtige);
        }

        setLayout(new BorderLayout());
        setupUI();
    }

    private void verarbeiteErfolge(int richtige) {
        // Achievements freischalten & anzeigen (verzögert)
        boolean lComplete = achievementManager.unlockAchievement("L" + level + "_COMPLETE");
        boolean lPerfect = (erreichteSterne == 3) && achievementManager.unlockAchievement("L" + level + "_PERFECT");

        new Timer(1500, e -> { if (lComplete) showAch("Level geschafft!"); }).start();
        if (lPerfect) new Timer(4500, e -> showAch("Perfektes Spiel!")).start();

        // Highscore
        int genauigkeit = Math.min(100, (richtige * 100) / gesamtEmails);
        new HighscoreManager().hinzufuegen(hauptFenster.getSpielerName(), score, genauigkeit, level);
    }

    private void showAch(String name) {
        if (achievementCard != null) achievementCard.showAchievement(name);
    }

    private void setupUI() {
        AnimatedBackgroundPanel bg = new AnimatedBackgroundPanel();
        bg.setLayout(new BorderLayout());

        // 1. Header
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(50, 50, 30, 50));

        String title = gewonnen ? "LEVEL GESCHAFFT!" : "LEVEL VERLOREN";
        String icon = gewonnen ? "icon_party.png" : "icon_heartbreak.png";
        Color color = gewonnen ? new Color(80, 200, 120) : new Color(255, 100, 100);

        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setIcon(Theme.loadIcon(icon, 48));
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 48));
        lblTitle.setForeground(color);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Mission " + level, JLabel.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblSub.setForeground(Color.LIGHT_GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        top.add(lblTitle);
        top.add(Box.createVerticalStrut(10));
        top.add(lblSub);

        // 2. Stats Grid
        JPanel stats = new JPanel(new GridLayout(2, 2, 20, 20));
        stats.setOpaque(false);
        stats.setPreferredSize(new Dimension(700, 300));

        int gen = gesamtEmails > 0 ? (int)((double)(score / LevelConfig.PUNKTE_NORMAL) / gesamtEmails * 100) : 0;

        stats.add(createStatCard("icon_trophy.png", "PUNKTE", String.valueOf(score), new Color(255, 200, 80)));
        stats.add(createStatCard("icon_shield.png", "LEBEN", leben + "/" + maxLeben, new Color(255, 100, 100)));
        stats.add(createStatCard("icon_detective.png", "GENAUIGKEIT", gen + "%", Theme.COLOR_ACCENT_GREEN));
        stats.add(createStatCard("icon_trophy.png", "RANG", "#?", Theme.COLOR_TEXT_SECONDARY)); // Rang Logik vereinfacht

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(stats);

        // Sterne (bei Sieg)
        if (gewonnen) {
            JPanel stars = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            stars.setOpaque(false);
            for (int i = 1; i <= 3; i++) {
                String ic = (i <= erreichteSterne) ? "icon_star_filled.png" : "icon_star_outline.png";
                JLabel s = new JLabel(Theme.loadIcon(ic, 60));
                stars.add(s);
            }
            center.add(Box.createVerticalStrut(20));
            center.add(stars);
        }

        // 3. Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 30));
        bottom.setOpaque(false);

        if (gewonnen && level < 3) {
            bottom.add(createBtn("NÄCHSTES LEVEL", "icon_play.png", Theme.COLOR_BUTTON_GREEN, e -> {
                hauptFenster.levelGeschafft(level);
                hauptFenster.starteLevel(level + 1);
            }));
        } else if (!gewonnen) {
            bottom.add(createBtn("WIEDERHOLEN", "icon_retry.png", Theme.COLOR_ACCENT_ORANGE, e -> hauptFenster.starteLevel(level)));
        }

        bottom.add(createBtn("LEVEL AUSWAHL", "icon_home.png", Theme.COLOR_BUTTON_GREY, e -> {
            hauptFenster.levelGeschafft(level);
            hauptFenster.zeigeLevelAuswahl();
        }));

        achievementCard = new AchievementCard();
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(achievementCard);
        wrapper.add(top);

        bg.add(wrapper, BorderLayout.NORTH);
        bg.add(center, BorderLayout.CENTER);
        bg.add(bottom, BorderLayout.SOUTH);
        add(bg);
    }

    private JButton createBtn(String text, String icon, Color col, java.awt.event.ActionListener act) {
        JButton b = Theme.createStyledButton(text, Theme.FONT_BUTTON_LARGE, col, col, Theme.PADDING_BUTTON_LARGE);
        b.setIcon(Theme.loadIcon(icon, 22));
        b.setPreferredSize(new Dimension(280, 65));
        b.addActionListener(act);
        return b;
    }

    private JPanel createStatCard(String icon, String label, String val, Color col) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(18, 15, 18, 15));

        // Hintergrund malen
        JPanel bg = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(5, 5, getWidth()-5, getHeight()-5, 20, 20);
                g2.setColor(Theme.COLOR_PANEL_DARK);
                g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
                g2.setColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 150));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
                g2.dispose();
            }
        };

        JLabel i = new JLabel(Theme.loadIcon(icon, 36));
        i.setAlignmentX(CENTER_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setForeground(Color.GRAY);
        l.setAlignmentX(CENTER_ALIGNMENT);
        JLabel v = new JLabel(val);
        v.setFont(new Font("SansSerif", Font.BOLD, 26));
        v.setForeground(col);
        v.setAlignmentX(CENTER_ALIGNMENT);

        p.add(i);
        p.add(l);
        p.add(v);
        return p;
    }
}