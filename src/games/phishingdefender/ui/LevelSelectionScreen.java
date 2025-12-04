package games.phishingdefender.ui;

import games.phishingdefender.PhishingDefender;
import games.phishingdefender.managers.StarsManager;
import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.ui.components.LevelConfig;
import games.phishingdefender.ui.components.Theme;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Level-Auswahlbildschirm.
 * Zeigt Level-Karten mit Fortschrittsanzeige (Sterne) an.
 *
 * @author yusef03
 * @version 2.0
 */
public class LevelSelectionScreen extends JPanel {

    private final PhishingDefender hauptFenster;
    private final StarsManager starsManager;

    public LevelSelectionScreen(PhishingDefender hauptFenster, StarsManager starsManager) {
        this.hauptFenster = hauptFenster;
        this.starsManager = starsManager;
        setLayout(new BorderLayout());
        setupUI();
    }

    private void setupUI() {
        AnimatedBackgroundPanel background = new AnimatedBackgroundPanel();
        background.setLayout(new BorderLayout());

        // 1. Header
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 20, 50));

        JLabel greeting = new JLabel("Hallo " + hauptFenster.getSpielerName() + "!", JLabel.CENTER);
        greeting.setIcon(Theme.loadIcon("icon_detective.png", 28));
        greeting.setHorizontalTextPosition(JLabel.LEFT);
        greeting.setFont(new Font("SansSerif", Font.BOLD, 28));
        greeting.setForeground(Theme.COLOR_ACCENT_GREEN);
        greeting.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("WÄHLE DEINE MISSION", JLabel.CENTER);
        title.setIcon(Theme.loadIcon("icon_gamepad.png", 42));
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fortschrittsberechnung
        int unlocked = 1;
        if (starsManager.getStarsForLevel(1) > 0) unlocked = 2;
        if (starsManager.getStarsForLevel(2) > 0) unlocked = 3;

        topPanel.add(greeting);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(title);
        topPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        topPanel.add(createProgressBar(unlocked, 3));

        background.add(topPanel, BorderLayout.NORTH);

        // 2. Level Karten
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 30));
        cardsPanel.setOpaque(false);

        // Level 1 (Immer offen)
        cardsPanel.add(createCard(1, LevelConfig.L1_NAME, LevelConfig.L1_ICON,
                LevelConfig.L1_GESAMT_EMAILS, LevelConfig.L1_ZEIT, LevelConfig.L1_SCHWIERIGKEIT,
                LevelConfig.L1_FARBE, true));

        // Level 2
        boolean l2Open = starsManager.getStarsForLevel(1) > 0;
        cardsPanel.add(createCard(2, LevelConfig.L2_NAME, LevelConfig.L2_ICON,
                LevelConfig.L2_GESAMT_EMAILS, LevelConfig.L2_ZEIT, LevelConfig.L2_SCHWIERIGKEIT,
                LevelConfig.L2_FARBE, l2Open));

        // Level 3
        boolean l3Open = starsManager.getStarsForLevel(2) > 0;
        cardsPanel.add(createCard(3, LevelConfig.L3_NAME, LevelConfig.L3_ICON,
                LevelConfig.L3_GESAMT_EMAILS, LevelConfig.L3_ZEIT, LevelConfig.L3_SCHWIERIGKEIT,
                LevelConfig.L3_FARBE, l3Open));

        background.add(cardsPanel, BorderLayout.CENTER);

        // 3. Footer
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        JButton backBtn = Theme.createStyledButton("← ZURÜCK", Theme.FONT_BUTTON_MEDIUM, Theme.COLOR_BUTTON_GREY, Theme.COLOR_BUTTON_GREY_HOVER, Theme.PADDING_BUTTON_MEDIUM);
        backBtn.setPreferredSize(new Dimension(200, 55));
        backBtn.addActionListener(e -> hauptFenster.zeigeWelcomeScreen());

        bottom.add(backBtn);
        background.add(bottom, BorderLayout.SOUTH);

        add(background, BorderLayout.CENTER);
    }

    private JPanel createCard(int lvl, String name, String icon, int mails, int time, String diff, Color color, boolean unlocked) {
        JPanel card = new JPanel() {
            private boolean hovered = false;
            private float glowAlpha = 0f;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (unlocked) {
                            hovered = true;
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                            animateGlow(true);
                        }
                    }
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        animateGlow(false);
                    }
                    public void mouseClicked(MouseEvent e) {
                        if (unlocked) hauptFenster.starteLevel(lvl);
                    }
                });
            }

            private void animateGlow(boolean fadeIn) {
                Timer t = new Timer(30, null);
                t.addActionListener(e -> {
                    glowAlpha += fadeIn ? 0.1f : -0.1f;
                    if (glowAlpha >= 1.0f || glowAlpha <= 0f) {
                        glowAlpha = Math.max(0, Math.min(1, glowAlpha));
                        t.stop();
                    }
                    repaint();
                });
                t.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                // Glow
                if (hovered && unlocked) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(100 * glowAlpha)));
                    g2.fill(new RoundRectangle2D.Float(-5, -5, w + 10, h + 10, 25, 25));
                }

                // Card Body
                g2.setColor(new Color(30, 30, 40, unlocked ? 255 : 150));
                if (unlocked) {
                    g2.setPaint(new GradientPaint(0, 0, Theme.COLOR_PANEL_DARK, 0, h, new Color(25, 25, 25)));
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 5, h - 5, 20, 20));

                // Border
                g2.setColor(unlocked ? color : new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(3));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 5, h - 5, 20, 20));

                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 380));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        // Icon
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(Theme.loadIcon(unlocked ? icon : "icon_lock.png", 80));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text
        JLabel lvlLabel = new JLabel("MISSION " + lvl, JLabel.CENTER);
        lvlLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        lvlLabel.setForeground(unlocked ? color : Color.GRAY);
        lvlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(name, JLabel.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(unlocked ? Color.WHITE : Color.GRAY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sterne
        JPanel stars = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        stars.setOpaque(false);
        int earned = starsManager.getStarsForLevel(lvl);
        for (int i = 1; i <= 3; i++) {
            String starIcon = (i <= earned) ? "icon_star_filled.png" : "icon_star_outline.png";
            JLabel s = new JLabel();
            s.setIcon(Theme.loadIcon(starIcon, 22));
            stars.add(s);
        }
        stars.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Info
        JLabel mailInfo = new JLabel(mails + " E-Mails");
        mailInfo.setIcon(Theme.loadIcon("icon_mail.png", 16));
        mailInfo.setForeground(Color.LIGHT_GRAY);
        mailInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timeInfo = new JLabel(time + " Sek. / E-Mail");
        timeInfo.setIcon(Theme.loadIcon("icon_clock.png", 16));
        timeInfo.setForeground(Color.LIGHT_GRAY);
        timeInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(lvlLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(stars);
        card.add(Box.createVerticalStrut(15));
        card.add(mailInfo);
        card.add(Box.createVerticalStrut(5));
        card.add(timeInfo);

        return card;
    }

    private JPanel createProgressBar(int current, int total) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        p.setOpaque(false);
        for (int i = 1; i <= total; i++) {
            JLabel dot = new JLabel(i <= current ? "●" : "○");
            dot.setFont(new Font("Arial", Font.PLAIN, 24));
            dot.setForeground(i <= current ? Theme.COLOR_ACCENT_GREEN : Color.GRAY);
            p.add(dot);
        }
        return p;
    }
}