package games.phishingdefender.ui;

import games.phishingdefender.ui.components.HeartDisplayPanel;
import games.phishingdefender.ui.components.Theme;
import java.awt.*;
import javax.swing.*;

/**
 * Overlay-Menü im Spiel (GlassPane).
 * Zeigt aktuellen Status und bietet Optionen (Weiter, Neustart, Exit).
 *
 * @author yusef03
 * @version 2.0
 */
public class PauseMenu extends JPanel {

    private final GameScreen gameScreen;
    private final int score;
    private final int leben;
    private final int verbleibendeZeit;
    private final int maxLeben;

    public PauseMenu(GameScreen gameScreen, int score, int leben, int maxLeben, int verbleibendeZeit) {
        this.gameScreen = gameScreen;
        this.score = score;
        this.leben = leben;
        this.maxLeben = maxLeben;
        this.verbleibendeZeit = verbleibendeZeit;

        setLayout(new GridBagLayout());
        setOpaque(true);
        setupUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Hintergrund komplett überdecken
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Theme.COLOR_BACKGROUND_DARK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    private void setupUI() {
        setPreferredSize(new Dimension(9999, 9999)); // Maximale Größe erzwingen

        // Hauptpanel (Zentrierte Box)
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                // Schatten
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(6, 6, w - 6, h - 6, 20, 20);

                // Verlauf
                g2.setPaint(new GradientPaint(0, 0, Theme.COLOR_PANEL_DARK, 0, h, new Color(25, 25, 25)));
                g2.fillRoundRect(0, 0, w - 6, h - 6, 20, 20);

                // Rand
                g2.setColor(Theme.COLOR_ACCENT_GREEN);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, w - 6, h - 6, 20, 20);

                g2.dispose();
            }
        };

        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setPreferredSize(new Dimension(550, 600));
        menuPanel.setMaximumSize(new Dimension(550, 600));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        // 1. Header
        JLabel title = new JLabel("PAUSE", JLabel.CENTER);
        title.setIcon(Theme.loadIcon("icon_pause.png", 42));
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(new Color(100, 180, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2. Stats
        JPanel stats = new JPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
        stats.setOpaque(false);
        stats.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        JLabel scoreLabel = new JLabel("Score: " + score, JLabel.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        scoreLabel.setForeground(new Color(255, 215, 100));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        HeartDisplayPanel hearts = new HeartDisplayPanel(leben, maxLeben);
        hearts.setAlignmentX(Component.CENTER_ALIGNMENT);
        hearts.setMaximumSize(new Dimension(200, 40));

        JLabel timeLabel = new JLabel("Zeit: " + verbleibendeZeit + "s", JLabel.CENTER);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        timeLabel.setForeground(new Color(255, 140, 80));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        stats.add(scoreLabel);
        stats.add(Box.createVerticalStrut(8));
        stats.add(hearts);
        stats.add(Box.createVerticalStrut(8));
        stats.add(timeLabel);

        // 3. Buttons
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setOpaque(false);

        JButton btnResume = createMenuButton("FORTSETZEN", "icon_play.png", Theme.COLOR_BUTTON_GREEN);
        btnResume.addActionListener(e -> gameScreen.fortsetzen());

        JButton btnRestart = createMenuButton("NEU STARTEN", "icon_retry.png", Theme.COLOR_BUTTON_BLUE);
        btnRestart.addActionListener(e -> gameScreen.levelNeuStarten());

        JButton btnMenu = createMenuButton("HAUPTMENÜ", "icon_home.png", Theme.COLOR_BUTTON_GREY);
        btnMenu.addActionListener(e -> gameScreen.zumHauptmenue());

        buttons.add(btnResume);
        buttons.add(Box.createVerticalStrut(12));
        buttons.add(btnRestart);
        buttons.add(Box.createVerticalStrut(12));
        buttons.add(btnMenu);

        // 4. Hinweis
        JLabel hint = new JLabel("Drücke LEERTASTE zum Fortsetzen", JLabel.CENTER);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 13));
        hint.setForeground(new Color(140, 140, 140));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuPanel.add(title);
        menuPanel.add(stats);
        menuPanel.add(buttons);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(hint);

        add(menuPanel);
    }

    private JButton createMenuButton(String text, String icon, Color color) {
        JButton btn = Theme.createStyledButton(text, Theme.FONT_BUTTON_MEDIUM, color, color, Theme.PADDING_BUTTON_LARGE);
        btn.setIcon(Theme.loadIcon(icon, 22));
        btn.setPreferredSize(new Dimension(320, 60));
        btn.setMaximumSize(new Dimension(320, 60));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
}