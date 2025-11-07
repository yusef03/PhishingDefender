package games.phishingdefender.ui;

import games.phishingdefender.ui.components.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Das Pausenmenü, das als Overlay über dem GameScreen angezeigt wird (via GlassPane).
 * Zeigt den aktuellen Spielstand (Score, Leben, Zeit) und bietet Buttons zum
 * Fortsetzen, Neustarten oder Verlassen des Levels.
 *
 * @author yusef03
 * @version 1.0
 */

public class PauseMenu extends JPanel {

    private GameScreen gameScreen;
    private int score;
    private int leben;
    private int verbleibendeZeit;

    public PauseMenu(GameScreen gameScreen, int score, int leben, int verbleibendeZeit) {
        this.gameScreen = gameScreen;
        this.score = score;
        this.leben = leben;
        this.verbleibendeZeit = verbleibendeZeit;

        setLayout(new GridBagLayout());
        setOpaque(true);
        setupUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dunkler Hintergrund wie im Spiel (kein Overlay!)
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Theme.COLOR_BACKGROUND_DARK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    private void setupUI() {

        setPreferredSize(new Dimension(9999, 9999)); // Immer so groß wie möglich!

        //Menü zentriert
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Schatten
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(6, 6, w - 6, h - 6, 20, 20);

                // Hintergrund (Gradient)
                GradientPaint gradient = new GradientPaint(
                        0, 0, Theme.COLOR_PANEL_DARK,
                        0, h, new Color(25, 25, 25)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, w - 6, h - 6, 20, 20);

                // Border
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

        //TITEL
        JLabel titleLabel = new JLabel("⏸️ PAUSE", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        titleLabel.setForeground(new Color(100, 180, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //STATS
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        JLabel scoreLabel = new JLabel("⭐ Score: " + score, JLabel.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        scoreLabel.setForeground(new Color(255, 215, 100));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lebenLabel = new JLabel("❤️ Leben: " + leben, JLabel.CENTER);
        lebenLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        lebenLabel.setForeground(new Color(255, 100, 100));
        lebenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel zeitLabel = new JLabel("⏱️ Zeit: " + verbleibendeZeit + "s", JLabel.CENTER);
        zeitLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        zeitLabel.setForeground(new Color(255, 140, 80));
        zeitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statsPanel.add(scoreLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statsPanel.add(lebenLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        statsPanel.add(zeitLabel);

        //BUTTONS
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);

        // Fortsetzen Button
        JButton fortsetzenButton = Theme.createStyledButton(
                "▶️ FORTSETZEN",
                Theme.FONT_BUTTON_MEDIUM, // 18px
                Theme.COLOR_BUTTON_GREEN,
                Theme.COLOR_BUTTON_GREEN_HOVER,
                Theme.PADDING_BUTTON_LARGE // 15px padding
        );
        fortsetzenButton.setPreferredSize(new Dimension(320, 60));
        fortsetzenButton.setMaximumSize(new Dimension(320, 60));
        fortsetzenButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fortsetzenButton.addActionListener(e -> gameScreen.fortsetzen());

        // Neustart Button
        JButton neustartButton = Theme.createStyledButton(
                "🔄 NEU STARTEN",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_BLUE,
                Theme.COLOR_BUTTON_BLUE_HOVER,
                Theme.PADDING_BUTTON_LARGE
        );
        neustartButton.setPreferredSize(new Dimension(320, 60));
        neustartButton.setMaximumSize(new Dimension(320, 60));
        neustartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        neustartButton.addActionListener(e -> gameScreen.levelNeuStarten());

        // Hauptmenü Button
        JButton hauptmenuButton = Theme.createStyledButton(
                "🏠 HAUPTMENÜ",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_LARGE
        );
        hauptmenuButton.setPreferredSize(new Dimension(320, 60));
        hauptmenuButton.setMaximumSize(new Dimension(320, 60));
        hauptmenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hauptmenuButton.addActionListener(e -> gameScreen.zumHauptmenue());

        buttonsPanel.add(fortsetzenButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        buttonsPanel.add(neustartButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        buttonsPanel.add(hauptmenuButton);

        // Hinweis
        JLabel hinweisLabel = new JLabel("Drücke LEERTASTE zum Fortsetzen", JLabel.CENTER);
        hinweisLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        hinweisLabel.setForeground(new Color(140, 140, 140));
        hinweisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Alles zusammenbauen
        menuPanel.add(titleLabel);
        menuPanel.add(statsPanel);
        menuPanel.add(buttonsPanel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(hinweisLabel);

        add(menuPanel);
    }
}