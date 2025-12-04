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
 * Zeigt alle verfügbaren Erfolge an.
 * Unterschiedliche Darstellung für freigeschaltete/gesperrte Items.
 *
 * @author yusef03
 * @version 2.0
 */
public class AchievementScreen extends JPanel {

    private final PhishingDefender hauptFenster;
    private final AchievementManager manager;

    public AchievementScreen(PhishingDefender hauptFenster) {
        this.hauptFenster = hauptFenster;
        this.manager = new AchievementManager(hauptFenster.getSpielerName());

        setLayout(new BorderLayout());
        setupUI();
    }

    private void setupUI() {
        AnimatedBackgroundPanel background = new AnimatedBackgroundPanel();
        background.setLayout(new BorderLayout());
        background.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // 1. Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("ERFOLGE", JLabel.CENTER);
        title.setIcon(Theme.loadIcon("icon_trophy.png", 50));
        title.setFont(new Font("SansSerif", Font.BOLD, 50));
        title.setForeground(new Color(255, 215, 0)); // Gold
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fortschrittsanzeige
        long unlocked = manager.getAllAchievements().stream().filter(Achievement::isUnlocked).count();
        long total = manager.getAllAchievements().size();

        JLabel subtitle = new JLabel(">>> " + unlocked + " / " + total + " FREIGESCHALTET <<<", JLabel.CENTER);
        subtitle.setFont(new Font("Monospaced", Font.BOLD, 15));
        subtitle.setForeground(Theme.COLOR_ACCENT_GREEN);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 15)));
        header.add(subtitle);

        background.add(header, BorderLayout.NORTH);

        // 2. Liste
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        List<Achievement> all = manager.getAllAchievements();

        if (all.isEmpty()) {
            JLabel empty = new JLabel("Keine Erfolge vorhanden.", JLabel.CENTER);
            empty.setFont(Theme.FONT_BUTTON_MEDIUM);
            empty.setForeground(Theme.COLOR_TEXT_SECONDARY);
            listPanel.add(empty);
        } else {
            for (Achievement ach : all) {
                listPanel.add(createCard(ach));
                listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        background.add(scroll, BorderLayout.CENTER);

        // 3. Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        footer.setOpaque(false);

        JButton backBtn = Theme.createStyledButton(
                "← Zurück zum Menü",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        backBtn.addActionListener(e -> hauptFenster.zeigeWelcomeScreen());

        footer.add(backBtn);
        background.add(footer, BorderLayout.SOUTH);

        add(background, BorderLayout.CENTER);
    }

    private JPanel createCard(Achievement ach) {
        JPanel card = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int r = 15; // Radius

                // Hintergrund
                Color bg = ach.isUnlocked() ? Theme.COLOR_PANEL_DARK : new Color(30, 30, 30, 200);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, w, h, r, r);

                // Rand
                Color border = ach.isUnlocked() ? Theme.COLOR_ACCENT_GREEN : new Color(80, 80, 80);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(ach.isUnlocked() ? 2 : 1));
                g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Icon
        JLabel icon = new JLabel();
        String iconName = ach.isUnlocked() ? ach.getIcon() : "icon_lock.png";
        icon.setIcon(Theme.loadIcon(iconName, 40));
        card.add(icon, BorderLayout.WEST);

        // Text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel nameLabel = new JLabel(ach.isUnlocked() ? ach.getName() : "???????");
        nameLabel.setFont(Theme.FONT_BUTTON_MEDIUM);
        nameLabel.setForeground(ach.isUnlocked() ? Color.WHITE : Theme.COLOR_BUTTON_GREY);

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