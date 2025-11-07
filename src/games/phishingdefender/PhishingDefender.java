package games.phishingdefender;

import games.phishingdefender.managers.AchievementManager;
import games.phishingdefender.managers.MusicManager;
import games.phishingdefender.managers.StarsManager;
import games.phishingdefender.ui.*;
import games.phishingdefender.ui.SplashScreen;
import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.ui.components.ScrollablePanel;
import games.phishingdefender.ui.components.SettingsButton;
import games.phishingdefender.ui.components.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Das Hauptfenster (JFrame) der Phishing Defender Anwendung.
 * Fungiert als Controller, der die verschiedenen Spiel-Bildschirme (Panels)
 * verwaltet und zwischen ihnen wechselt.
 *
 * @author yusef03
 * @version 1.0
 */
public class PhishingDefender extends JFrame {

    // Panel-Manager
    private JPanel mainPanel;

    // Spieler-Daten
    private int hoechstesFreigeschaltetes;
    private String spielerName;
    private StarsManager starsManager;
    private AchievementManager achievementManager;

    // UI-Komponenten, die sich ändern
    private JLabel spielerAnzeigeLabel;
    private JButton wechselnButton;
    private JButton startButton;
    private JButton tutorialButton;
    private JButton achievementsButton;

    public PhishingDefender() {
        hoechstesFreigeschaltetes = 1;
        this.spielerName = "";

        setTitle("Phishing Defender by 03yusef v.2.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setSize(1920, 1080);
        setMinimumSize(new Dimension(1920, 1080));
        // Zentriert das Fenster
        setLocationRelativeTo(null);

        // Haupt-Panel für Gradient-Hintergrund
        mainPanel = new AnimatedBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());

        // === LOGO ===
        JLabel logoLabel = null;
        try {
            java.net.URL logoURL = getClass().getResource("/games/phishingdefender/assets/images/logo.png");
            if (logoURL == null) { throw new Exception("Logo nicht gefunden!"); }
            ImageIcon originalIcon = new ImageIcon(logoURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledImage), JLabel.CENTER);
        } catch (Exception e) {
            logoLabel = new JLabel("🛡️", JLabel.CENTER);
            logoLabel.setFont(new Font("Arial", Font.PLAIN, 100));
        }
        logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        // === TITEL ===
        JLabel welcomeLabel = new JLabel("PHISHING DEFENDER", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(Color.RED);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 52));
        welcomeLabel.setOpaque(false);

        // === EMAIL STORY BOX ===
        JPanel storyBox = new JPanel();
        storyBox.setLayout(new BorderLayout());
        storyBox.setBackground(Theme.COLOR_PANEL_DARK);
        storyBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GREEN, 2),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));



        // (Header)
        JPanel emailHeader = new JPanel(new BorderLayout());
        emailHeader.setBackground(new Color(40, 40, 40));
        emailHeader.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel fromLabel = new JLabel("📧 Von: Cyber Security HQ");
        fromLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        fromLabel.setForeground(Theme.COLOR_TEXT_SECONDARY);
        JLabel subjectLabel = new JLabel("Betreff: ⚠️ DRINGEND - Phishing-Angriff erkannt!");
        subjectLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        subjectLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setOpaque(false);
        headerContent.add(fromLabel);
        headerContent.add(Box.createRigidArea(new Dimension(0, 5)));
        headerContent.add(subjectLabel);
        emailHeader.add(headerContent, BorderLayout.WEST);


        // (Body)
        JPanel emailBody = new JPanel(new BorderLayout());
        emailBody.setBackground(Theme.COLOR_PANEL_DARK);
        emailBody.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel bodyLabel = new JLabel(
                "<html><div style='line-height: 1.5;'>" +
                        "<span style='color: #CCCCCC; font-size: 14px;'>" +
                        "Hacker haben gefährliche Phishing-E-Mails in unserer Stadt verschickt!<br>" +
                        "Nur du kannst sie aufhalten!<br><br>" +
                        "</span>" +
                        "<span style='color: #AAAAAA; font-size: 13px;'>" +
                        "<span style='color: #FF6B35;'>▸</span> <b>Analysiere</b> verdächtige E-Mails<br>" +
                        "<span style='color: #FF6B35;'>▸</span> <b>Erkenne</b> Fälschungen und Betrugsversuche<br>" +
                        "<span style='color: #FF6B35;'>▸</span> <b>Schütze</b> die Bürger vor Cyber-Kriminellen<br><br>" +
                        "</span>" +
                        "<span style='color: #64B5FF; font-size: 15px; font-weight: bold;'>🕵️ Werde zum Cyber-Detektiv! 💻</span>" +
                        "</div></html>"
        );
        emailBody.add(bodyLabel, BorderLayout.CENTER);
        storyBox.add(emailHeader, BorderLayout.NORTH);
        storyBox.add(emailBody, BorderLayout.CENTER);

        // === CONTENT PANEL (Mitte) ===
        JPanel contentPanel = new ScrollablePanel();
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        storyBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(logoLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        contentPanel.add(storyBox);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        // === SPIELER ANZEIGE (OBEN IM BUTTON-BEREICH) ===
        JPanel spielerInfoPanel = new JPanel();
        spielerInfoPanel.setOpaque(false);
        spielerAnzeigeLabel = new JLabel("", JLabel.CENTER);
        spielerAnzeigeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        spielerAnzeigeLabel.setForeground(new Color(100, 200, 255));
        spielerAnzeigeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        aktualisiereSpielerAnzeige();
        spielerInfoPanel.add(spielerAnzeigeLabel);

        // === BUTTONS (NEUES, SAUBERES RASTER) ===

        // --- 1. ALLE BUTTONS ERSTELLEN UND DEFINIEREN ---

        startButton = Theme.createStyledButton(
                "▶ START GAME",
                Theme.FONT_BUTTON_LARGE,
                Theme.COLOR_ACCENT_ORANGE,
                Theme.COLOR_ACCENT_ORANGE_HOVER,
                Theme.PADDING_BUTTON_LARGE
        );
        startButton.addActionListener(e -> {
            if (spielerName == null || spielerName.isEmpty()) {
                zeigeNameInput();
                return;
            }
            if (starsManager != null) {
                if (starsManager.hatTutorialGelesen()) {
                    zeigeLevelAuswahl();
                } else {
                    zeigeTutorialScreen();
                }
            }
        });

        JButton highscoresButton = Theme.createStyledButton(
                "🏆 HIGHSCORES",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        highscoresButton.addActionListener(e -> zeigeHighscores());

        achievementsButton = Theme.createStyledButton(
                "🏆 ERFOLGE",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        achievementsButton.addActionListener(e -> zeigeAchievementScreen());

        tutorialButton = Theme.createStyledButton(
                "💡 WAS IST PHISHING?",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_BLUE,
                Theme.COLOR_BUTTON_BLUE_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        tutorialButton.addActionListener(e -> zeigeTutorialScreen());

        wechselnButton = Theme.createStyledButton(
                "🚪 SPIELER WECHSELN",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_PURPLE,
                Theme.COLOR_BUTTON_PURPLE_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        wechselnButton.addActionListener(e -> spielerAbmelden());
        wechselnButton.setVisible(false);

        JButton beendenButton = Theme.createStyledButton(
                "🔌 BEENDEN",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_RED,
                Theme.COLOR_BUTTON_RED_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        beendenButton.addActionListener(e -> System.exit(0));

        // --- 2. DAS LAYOUT-PANEL (MIT GRIDBAG) ERSTELLEN ---

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        // KEIN fill, KEIN weightx, KEIN ipady. Die Buttons behalten ihre natürliche Größe.
        gbc.insets = new Insets(8, 8, 8, 8);

        // ZEILE 0: Spieler-Info (breit über 2 Spalten)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0); // Nur Abstand nach unten
        buttonPanel.add(spielerInfoPanel, gbc);

        // Zurücksetzen für Buttons
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridwidth = 1;

        // ZEILE 1: Start Game (breit über 2 Spalten)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        buttonPanel.add(startButton, gbc);

        // Zurücksetzen
        gbc.gridwidth = 1;

        // ZEILE 2: Highscores | Erfolge
        gbc.gridx = 0;
        gbc.gridy = 2;
        buttonPanel.add(highscoresButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        buttonPanel.add(achievementsButton, gbc);

        // ZEILE 3: Tutorial | Spieler wechseln
        gbc.gridx = 0;
        gbc.gridy = 3;
        buttonPanel.add(tutorialButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        buttonPanel.add(wechselnButton, gbc);

        // ZEILE 4: Beenden Button (breit über 2 Spalten)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        buttonPanel.add(beendenButton, gbc);

        // === Settings Button (unten rechts) ===
        SettingsButton settingsButton = new SettingsButton();
        settingsButton.addActionListener(e -> {
            SettingsDialog dialog = new SettingsDialog(this);
            dialog.setVisible(true);
        });
        JPanel settingsWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 25));
        settingsWrapper.setOpaque(false);
        settingsWrapper.add(settingsButton);


        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        // Packt die Buttons in die Mitte (GridBagLayout)
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        // Packt den Settings-Button nach rechts
        bottomPanel.add(settingsWrapper, BorderLayout.EAST);

        // === ALLES ZUM HAUPTFENSTER HINZUFÜGEN ===
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // === METHODEN ZUM WECHSELN DER BILDSCHIRME ===

    public void zeigeWelcomeScreen() {
        getContentPane().removeAll();
        getContentPane().add(mainPanel);

        // 1. Logik ausführen, die ändert, was sichtbar ist
        aktualisiereSpielerAnzeige();
        aktualisiereStartButtonStatus();

        // 2. DANN das Fenster neu zeichnen
        revalidate();
        repaint();

        if (!MusicManager.isPlaying()) {
            MusicManager.startMenuMusic();
        }
        if (spielerName != null && !spielerName.isEmpty()) {
            aktualisiereLevelFreischaltung();
        }
    }

    public void zeigeHighscores() {
        getContentPane().removeAll();
        getContentPane().add(new HighscoreScreen(this));
        revalidate();
        repaint();
    }

    public void zeigeAchievementScreen() {
        if (spielerName == null || spielerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte melde dich erst an (über 'Start Game').");
            return;
        }
        getContentPane().removeAll();
        getContentPane().add(new AchievementScreen(this));
        revalidate();
        repaint();
    }

    public void zeigeTutorialScreen() {
        getContentPane().removeAll();
        getContentPane().add(new TutorialScreen(this));
        revalidate();
        repaint();
    }

    public void zeigeLevelAuswahl() {
        getContentPane().removeAll();
        getContentPane().add(new LevelSelectionScreen(this));
        revalidate();
        repaint();
        if (!MusicManager.isPlaying()) {
            MusicManager.startMenuMusic();
        }
    }

    public void starteLevel(int level) {
        MusicManager.stopMenuMusic();
        getContentPane().removeAll();
        JPanel ladePanel = new JPanel(new BorderLayout());
        ladePanel.setBackground(new Color(26, 26, 46));
        JLabel ladeLabel = new JLabel("LADEN... 🎵", JLabel.CENTER);
        ladeLabel.setFont(new Font("Arial", Font.BOLD, 48));
        ladeLabel.setForeground(new Color(255, 107, 53));
        ladePanel.add(ladeLabel, BorderLayout.CENTER);
        getContentPane().add(ladePanel);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            getContentPane().removeAll();
            // KORREKTER AUFRUF MIT 3 ARGUMENTEN
            getContentPane().add(new GameScreen(this, level, this.achievementManager));
            revalidate();
            repaint();
        });
    }

    public void zeigeResultScreen(int level, int score, int leben, int maxLeben, int gesamtEmails, boolean erfolg) {
        MusicManager.stopMenuMusic();
        getContentPane().removeAll();
        getContentPane().add(new ResultScreen(this, level, score, leben, maxLeben, gesamtEmails, erfolg, this.achievementManager));
        revalidate();
        repaint();
    }

    public void levelGeschafft(int level) {
        aktualisiereLevelFreischaltung();
        zeigeLevelAuswahl();
    }

    // === SPIELER-LOGIK METHODEN ===

    public int getHoechstesFreigeschaltetes() {
        return hoechstesFreigeschaltetes;
    }

    public void setSpielerName(String name) {
        this.spielerName = name;
        if (name != null && !name.isEmpty()) {
            this.starsManager = new StarsManager(name);
            this.achievementManager = new AchievementManager(name);
        } else {
            this.starsManager = null;
            this.achievementManager = null;
        }
        aktualisiereLevelFreischaltung();
        aktualisiereSpielerAnzeige();
    }

    public String getSpielerName() {
        return spielerName;
    }

    private void aktualisiereLevelFreischaltung() {
        if (spielerName == null || spielerName.isEmpty() || this.starsManager == null) {
            hoechstesFreigeschaltetes = 1;
            return;
        }
        hoechstesFreigeschaltetes = 1;
        if (this.starsManager.getStarsForLevel(1) > 0) {
            hoechstesFreigeschaltetes = 2;
        }
        if (this.starsManager.getStarsForLevel(2) > 0) {
            hoechstesFreigeschaltetes = 3;
        }
    }

    public void zeigeNameInput() {
        getContentPane().removeAll();
        getContentPane().add(new NameInputScreen(this));
        revalidate();
        repaint();
    }

    private void spielerAbmelden() {
        int antwort = JOptionPane.showConfirmDialog(
                this,
                "Möchtest du den Spieler wechseln?\n\nDein Fortschritt ist gespeichert!",
                "Spieler wechseln",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (antwort == JOptionPane.YES_OPTION) {
            setSpielerName("");
            hoechstesFreigeschaltetes = 1;
            zeigeNameInput();
        }
    }

    private void aktualisiereSpielerAnzeige() {
        if (spielerAnzeigeLabel != null) {
            if (spielerName == null || spielerName.isEmpty()) {
                spielerAnzeigeLabel.setText("👤 Angemeldet als: Gast");
            } else {
                spielerAnzeigeLabel.setText("👤 Angemeldet als: " + spielerName);
            }
        }
        if (wechselnButton != null) {
            wechselnButton.setVisible(spielerName != null && !spielerName.isEmpty());
        }
    }

    private void aktualisiereStartButtonStatus() {
        if (startButton == null || tutorialButton == null || achievementsButton == null) return;

        // Fall 1: "Gast"
        if (spielerName == null || spielerName.isEmpty()) {
            startButton.setEnabled(true);
            startButton.setText("▶ START GAME");
            tutorialButton.setVisible(true);
            achievementsButton.setVisible(false); // ERFOLGE AUSBLENDEN FÜR GAST
        }
        // Fall 2: Angemeldeter Spieler
        else if (starsManager != null) {
            achievementsButton.setVisible(true); // ERFOLGE EINBLENDEN
            if (starsManager.hatTutorialGelesen()) {
                // Hat Tutorial gelesen
                startButton.setEnabled(true);
                startButton.setText("▶ START GAME");
                tutorialButton.setVisible(true);
            } else {
                // Hat Tutorial NICHT gelesen
                startButton.setEnabled(true);
                startButton.setText("▶ (Tutorial starten)");
                tutorialButton.setVisible(false);
            }
        }
    }

    public void tutorialAbgeschlossen() {
        if (this.starsManager != null) {
            this.starsManager.setTutorialGelesen();
        }
        zeigeWelcomeScreen();
    }


    // === MAIN METHODE ===
    public static void main(String[] args) {
        games.phishingdefender.ui.SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        SwingWorker<PhishingDefender, Void> worker = new SwingWorker<>() {
            @Override
            protected PhishingDefender doInBackground() throws Exception {
                MusicManager.startMenuMusic();
                PhishingDefender game = new PhishingDefender();
                Thread.sleep(1500);
                return game;
            }

            @Override
            protected void done() {
                try {
                    PhishingDefender game = get();
                    game.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Spiel konnte nicht geladen werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                } finally {
                    splash.setVisible(false);
                    splash.dispose();
                }
            }
        };
        worker.execute();
    }
}