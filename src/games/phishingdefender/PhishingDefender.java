package games.phishingdefender;

import games.phishingdefender.managers.AchievementManager;
import games.phishingdefender.managers.MusicManager;
import games.phishingdefender.managers.SettingsManager;
import games.phishingdefender.managers.StarsManager;
import games.phishingdefender.ui.*;
import games.phishingdefender.ui.SplashScreen;
import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.ui.components.ScrollablePanel;
import games.phishingdefender.ui.components.SettingsButton;
import games.phishingdefender.ui.components.Theme;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Hauptklasse (Controller) der Anwendung.
 * Verwaltet das Hauptfenster (JFrame) und die Navigation zwischen den Screens.
 * Hält den globalen Spielzustand (Spielerdaten, Manager).
 *
 * @author yusef03
 * @version 2.0
 */
public class PhishingDefender extends JFrame {

    // UI-Container
    private JPanel mainPanel;

    // Spielzustand & Daten
    private int hoechstesFreigeschaltetes;
    private String spielerName;

    // Manager-Instanzen
    private StarsManager starsManager;
    private AchievementManager achievementManager;
    private final SettingsManager settingsManager;

    // Buttons (für dynamische Updates)
    private JLabel spielerAnzeigeLabel;
    private JButton startButton; // Der "Hero"-Button

    public PhishingDefender() {
        // Initialisierung
        this.hoechstesFreigeschaltetes = 1;
        this.spielerName = "";
        this.settingsManager = new SettingsManager();

        setTitle("Phishing Defender by 03yusef v.2.2l");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setMinimumSize(new Dimension(1280, 720));
        setLocationRelativeTo(null);

        // Haupt-Panel
        mainPanel = new AnimatedBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());

        setupWelcomeScreenUI();
        zeigeWelcomeScreen(); // Initialanzeige
    }

    /**
     * Baut das UI für den Startbildschirm auf.
     * Wird bei Statusänderungen (Login/Logout) neu aufgerufen.
     */
    private void setupWelcomeScreenUI() {
        mainPanel.removeAll(); // Reset

        // === 1. LOGO & HEADER ===
        JLabel logoLabel = new JLabel();
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        try {
            URL logoURL = getClass().getResource("/games/phishingdefender/assets/images/logo.png");
            if (logoURL != null) {
                ImageIcon icon = new ImageIcon(logoURL);
                Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            } else {
                throw new Exception("Logo nicht gefunden");
            }
        } catch (Exception e) {
            logoLabel.setText("🛡️"); // Fallback
            logoLabel.setFont(new Font("Arial", Font.PLAIN, 100));
        }

        JLabel titleLabel = new JLabel("PHISHING DEFENDER", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 52));
        titleLabel.setForeground(Theme.COLOR_BUTTON_RED);

        // === 2. STORY BOX (E-Mail Style) ===
        JPanel storyBox = new JPanel(new BorderLayout());
        storyBox.setBackground(Theme.COLOR_PANEL_DARK);
        storyBox.setBorder(BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GREEN, 2));

        // E-Mail Header
        JPanel mailHeader = new JPanel(new BorderLayout());
        mailHeader.setBackground(new Color(40, 40, 40));
        mailHeader.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel from = new JLabel(" Von: Cyber Security HQ");
        from.setIcon(Theme.loadIcon("icon_mail.png", 16));
        from.setFont(new Font("SansSerif", Font.BOLD, 13));
        from.setForeground(Theme.COLOR_TEXT_SECONDARY);

        JLabel subject = new JLabel(" Betreff: DRINGEND - Angriff erkannt!");
        subject.setIcon(Theme.loadIcon("icon_warning.png", 16));
        subject.setFont(new Font("SansSerif", Font.BOLD, 14));
        subject.setForeground(Theme.COLOR_ACCENT_GREEN);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(from);
        headerText.add(Box.createVerticalStrut(5));
        headerText.add(subject);

        mailHeader.add(headerText, BorderLayout.WEST);

        // E-Mail Body
        JPanel mailBody = new JPanel(new BorderLayout());
        mailBody.setBackground(Theme.COLOR_PANEL_DARK);
        mailBody.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel bodyText = new JLabel("<html><div style='line-height: 1.5; color:#CCCCCC;'>" +
                "Hacker haben gefährliche Phishing-E-Mails verschickt!<br>" +
                "Nur du kannst sie aufhalten!<br><br>" +
                "<span style='color:#FF6B35;'>•</span> Analysiere E-Mails<br>" +
                "<span style='color:#FF6B35;'>•</span> Erkenne Betrug<br>" +
                "<span style='color:#FF6B35;'>•</span> Schütze das Netzwerk<br><br>" +
                "<b style='color:#64B5FF;'>Werde zum Cyber-Detektiv!</b></div></html>");
        mailBody.add(bodyText, BorderLayout.CENTER);

        storyBox.add(mailHeader, BorderLayout.NORTH);
        storyBox.add(mailBody, BorderLayout.CENTER);

        // === 3. CONTENT WRAPPER (Scrollbar) ===
        JPanel content = new ScrollablePanel();
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        storyBox.setAlignmentX(CENTER_ALIGNMENT);

        content.add(logoLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(25));
        content.add(storyBox);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // === 4. BUTTONS & FOOTER (Das neue Layout) ===

        // Spieler Info
        spielerAnzeigeLabel = new JLabel("", JLabel.CENTER);
        spielerAnzeigeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        spielerAnzeigeLabel.setForeground(new Color(100, 200, 255));
        aktualisiereSpielerAnzeige();

        // --- Buttons definieren ---

        // 1. Der "Hero-Button" (Dynamisch)
        String startText = " START GAME";
        java.awt.event.ActionListener startAction = e -> {
            if (spielerName == null || spielerName.isEmpty()) {
                zeigeNameInput();
            } else if (starsManager != null && !starsManager.hatTutorialGelesen()) {
                zeigeTutorialScreen();
            } else {
                zeigeLevelAuswahl();
            }
        };

        if (spielerName != null && !spielerName.isEmpty()) {
            if (starsManager != null && !starsManager.hatTutorialGelesen()) {
                startText = " TUTORIAL STARTEN";
            } else {
                startText = " MISSION WÄHLEN";
            }
        }

        startButton = Theme.createStyledButton(startText, Theme.FONT_BUTTON_LARGE, Theme.COLOR_ACCENT_ORANGE, Theme.COLOR_ACCENT_ORANGE_HOVER, Theme.PADDING_BUTTON_LARGE);
        startButton.setIcon(Theme.loadIcon("icon_play.png", 22));
        startButton.addActionListener(startAction);

        // 2. Highscores
        JButton scoresBtn = Theme.createStyledButton(" HIGHSCORES", Theme.FONT_BUTTON_MEDIUM, Theme.COLOR_BUTTON_GREY, Theme.COLOR_BUTTON_GREY_HOVER, Theme.PADDING_BUTTON_MEDIUM);
        scoresBtn.setIcon(Theme.loadIcon("icon_trophy.png", 18));
        scoresBtn.addActionListener(e -> zeigeHighscores());

        // 3. Erfolge (Deaktiviert für Gäste)
        JButton achBtn = Theme.createStyledButton(" ERFOLGE", Theme.FONT_BUTTON_MEDIUM, Theme.COLOR_BUTTON_GREY, Theme.COLOR_BUTTON_GREY_HOVER, Theme.PADDING_BUTTON_MEDIUM);
        achBtn.setIcon(Theme.loadIcon("icon_trophy.png", 18));
        if (spielerName == null || spielerName.isEmpty()) {
            achBtn.setEnabled(false);
            achBtn.setToolTipText("Melde dich an, um Erfolge zu sehen!");
        } else {
            achBtn.addActionListener(e -> zeigeAchievementScreen());
        }

        // 4. Hilfe / Tutorial
        JButton helpBtn = Theme.createStyledButton(" WAS IST PHISHING?", Theme.FONT_BUTTON_MEDIUM, Theme.COLOR_BUTTON_BLUE, Theme.COLOR_BUTTON_BLUE_HOVER, Theme.PADDING_BUTTON_MEDIUM);
        helpBtn.setIcon(Theme.loadIcon("icon_lightbulb.png", 18));
        helpBtn.addActionListener(e -> zeigeTutorialScreen());

        // 5. Spieler Wechseln / Anmelden
        JButton switchBtn;
        if (spielerName == null || spielerName.isEmpty()) {
            switchBtn = Theme.createStyledButton(" ANMELDEN", Theme.FONT_BUTTON_MEDIUM, Theme.COLOR_BUTTON_PURPLE, Theme.COLOR_BUTTON_PURPLE_HOVER, Theme.PADDING_BUTTON_MEDIUM);
            switchBtn.setIcon(Theme.loadIcon("icon_user.png", 18));
            switchBtn.addActionListener(e -> zeigeNameInput());
        } else {
            switchBtn = Theme.createStyledButton(" SPIELER WECHSELN", Theme.FONT_BUTTON_MEDIUM, Theme.COLOR_BUTTON_PURPLE, Theme.COLOR_BUTTON_PURPLE_HOVER, Theme.PADDING_BUTTON_MEDIUM);
            switchBtn.setIcon(Theme.loadIcon("icon_change_player.png", 18));
            switchBtn.addActionListener(e -> spielerAbmelden());
        }

        // 6. Beenden
        JButton exitBtn = Theme.createStyledButton("BEENDEN", Theme.FONT_BUTTON_MEDIUM, Theme.COLOR_BUTTON_RED, Theme.COLOR_BUTTON_RED_HOVER, Theme.PADDING_BUTTON_MEDIUM);
        exitBtn.addActionListener(e -> System.exit(0));

        // --- Layout zusammenbauen (GridBag) ---
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15); // Abstand zwischen Buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Zeile 0: Info
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        btnPanel.add(spielerAnzeigeLabel, gbc);

        // Zeile 1: Start (Groß)
        gbc.gridy = 1;
        gbc.ipady = 10;
        gbc.insets = new Insets(8, 15, 8, 15);
        btnPanel.add(startButton, gbc);
        gbc.ipady = 0;

        // Zeile 2: Scores | Erfolge
        gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.gridx = 0; btnPanel.add(scoresBtn, gbc);
        gbc.gridx = 1; btnPanel.add(achBtn, gbc);

        // Zeile 3: Hilfe | Login
        gbc.gridy = 3;
        gbc.gridx = 0; btnPanel.add(helpBtn, gbc);
        gbc.gridx = 1; btnPanel.add(switchBtn, gbc);

        // Zeile 4: Beenden (Zentriert, kleiner)
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 50, 0, 50);
        gbc.fill = GridBagConstraints.NONE;
        btnPanel.add(exitBtn, gbc);

// === Settings Button (Perfekt unten rechts positioniert) ===
        SettingsButton settingsBtn = new SettingsButton();
        settingsBtn.addActionListener(e -> {
            SettingsDialog dialog = new SettingsDialog(this, this.settingsManager);
            dialog.setVisible(true);
        });

        // Wir nutzen GridBagLayout für den Wrapper, um den Button in die Ecke zu "zwingen"
        JPanel settingsWrap = new JPanel(new GridBagLayout());
        settingsWrap.setOpaque(false);

        GridBagConstraints gbcSettings = new GridBagConstraints();
        gbcSettings.gridx = 0;
        gbcSettings.gridy = 0;
        gbcSettings.weightx = 1.0; // Nimmt horizontalen Platz
        gbcSettings.weighty = 1.0; // Nimmt vertikalen Platz (drückt nach unten)
        gbcSettings.anchor = GridBagConstraints.SOUTHEAST; // Verankerung: UNTEN RECHTS
        // Abstand zum Rand: Oben, Links, Unten, Rechts
        gbcSettings.insets = new Insets(0, 0, 30, 40);

        settingsWrap.add(settingsBtn, gbcSettings);

        // Footer zusammenbauen
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        // WICHTIG: Damit der Wrapper Platz hat, geben wir ihm eine Mindestbreite
        settingsWrap.setPreferredSize(new Dimension(150, 0));

        bottomPanel.add(btnPanel, BorderLayout.CENTER);
        bottomPanel.add(settingsWrap, BorderLayout.EAST);

        mainPanel.add(scroll, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // === NAVIGATION & LOGIK ===

    public void zeigeWelcomeScreen() {
        setupWelcomeScreenUI(); // UI neu aufbauen für aktuellen Status
        switchScreen(mainPanel);
        if (!MusicManager.isPlaying()) MusicManager.startMenuMusic(settingsManager);
    }

    public void zeigeNameInput() {
        switchScreen(new NameInputScreen(this));
    }

    public void zeigeLevelAuswahl() {
        switchScreen(new LevelSelectionScreen(this, starsManager));
        if (!MusicManager.isPlaying()) MusicManager.startMenuMusic(settingsManager);
    }

    public void zeigeTutorialScreen() {
        switchScreen(new TutorialScreen(this));
    }

    public void zeigeHighscores() {
        switchScreen(new HighscoreScreen(this));
    }

    public void zeigeAchievementScreen() {
        if (spielerName == null || spielerName.isEmpty()) return;
        switchScreen(new AchievementScreen(this));
    }

    public void starteLevel(int level) {
        MusicManager.stopMenuMusic();

        // Lade-Screen
        JPanel loader = new JPanel(new BorderLayout());
        loader.setBackground(new Color(20, 20, 30));
        JLabel lbl = new JLabel("LADEN... 🎵", JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 40));
        lbl.setForeground(Color.ORANGE);
        loader.add(lbl, BorderLayout.CENTER);

        switchScreen(loader);

        // Async Start
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(100); // Kurzer Delay für Rendering
                switchScreen(new GameScreen(this, level, achievementManager));
            } catch (Exception e) {
                System.err.println("Fehler beim Levelstart: " + e.getMessage());
            }
        });
    }

    public void zeigeResultScreen(int lvl, int pts, int lives, int maxLives, int totalMails, boolean win) {
        MusicManager.stopMenuMusic();
        switchScreen(new ResultScreen(this, lvl, pts, lives, maxLives, totalMails, win, starsManager, achievementManager));
    }

    public void levelGeschafft(int level) {
        aktualisiereLevelStatus();
    }

    public void tutorialAbgeschlossen() {
        if (starsManager != null) starsManager.setTutorialGelesen();
        zeigeWelcomeScreen();
    }

    // Helper zum Wechseln
    private void switchScreen(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    // === DATEN-MANAGEMENT ===

    public void setSpielerName(String name) {
        this.spielerName = name;
        if (name != null && !name.isEmpty()) {
            this.starsManager = new StarsManager(name);
            this.achievementManager = new AchievementManager(name);
        } else {
            this.starsManager = null;
            this.achievementManager = null;
        }
        aktualisiereLevelStatus();
        aktualisiereSpielerAnzeige();
    }

    public String getSpielerName() {
        return spielerName;
    }

    public StarsManager getStarsManager() {
        return starsManager;
    }

    private void aktualisiereLevelStatus() {
        if (starsManager == null) {
            hoechstesFreigeschaltetes = 1;
            return;
        }
        hoechstesFreigeschaltetes = 1;
        if (starsManager.getStarsForLevel(1) > 0) hoechstesFreigeschaltetes = 2;
        if (starsManager.getStarsForLevel(2) > 0) hoechstesFreigeschaltetes = 3;
    }

    private void spielerAbmelden() {
        int wahl = JOptionPane.showConfirmDialog(this,
                "Spieler wechseln? Dein Fortschritt ist sicher.",
                "Abmelden", JOptionPane.YES_NO_OPTION);

        if (wahl == JOptionPane.YES_OPTION) {
            setSpielerName("");
            zeigeNameInput();
        }
    }

    private void aktualisiereSpielerAnzeige() {
        if (spielerAnzeigeLabel != null) {
            spielerAnzeigeLabel.setIcon(Theme.loadIcon("icon_user.png", 16));
            String text = (spielerName == null || spielerName.isEmpty()) ? "Gast" : spielerName;
            spielerAnzeigeLabel.setText("Angemeldet als: " + text);
        }
    }

    // === MAIN ===
    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        SwingWorker<PhishingDefender, Void> loader = new SwingWorker<>() {
            @Override
            protected PhishingDefender doInBackground() throws Exception {
                PhishingDefender game = new PhishingDefender();
                MusicManager.startMenuMusic(game.settingsManager);
                Thread.sleep(1500); // Künstliche Ladezeit für Effekt
                return game;
            }

            @Override
            protected void done() {
                try {
                    PhishingDefender game = get();
                    game.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Startfehler: " + e.getMessage());
                } finally {
                    splash.dispose();
                }
            }
        };
        loader.execute();
    }
}