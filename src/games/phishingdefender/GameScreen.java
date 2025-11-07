package games.phishingdefender;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;


/**
 * Das Haupt-Panel für das eigentliche Gameplay ("Level").
 * Zeigt E-Mails an, verwaltet den Timer, Score, Leben und Spieler-Antworten.
 * Enthält auch die Logik für Pause, Firewall-Bonus und Spielende.
 *
 * @author yusef03
 * @version 1.0
 */

public class GameScreen extends JPanel {


// Timer Config LevelConfig.java holen


    // Firewall-Bonus Config
    private static final int BONUS_RICHTIGE_NOETIG = 5;     // Wie viele richtige für Bonus
    private static final int BONUS_DAUER_EMAILS = 3;        // Bonus hält für X E-Mails
    private static final double BONUS_ZEIT_MULTIPLIKATOR = 0.5;  // 0.5 = 50% mehr Zeit
    private static final int BONUS_PUNKTE = 20;             // Punkte mit Bonus
    private static final int NORMALE_PUNKTE = 10;           // Normale Punkte

    // Steuerung Config
    private static final int TASTE_SICHER = KeyEvent.VK_A; // A = Sicher
    private static final int TASTE_PHISHING = KeyEvent.VK_L; // L = Phishing
    private static final int TASTE_PAUSE = KeyEvent.VK_SPACE; // Space = Pause
    private static final int TASTE_ZURUECK = KeyEvent.VK_ESCAPE;  // ESC = Zurück


    private PhishingDefender hauptFenster;
    private int level;
    private List<Email> emails;
    private int aktuelleEmailIndex;
    private int score;
    private int leben;
    private int maxLeben;
    private int zeitProEmail;
    private int verbleibendeSekunden;
    private int richtigeInFolge;
    private boolean firewallAktiv;
    private int firewallCounter;
    private boolean isPausiert;

    // UI Komponenten
    private JPanel pauseOverlay;
    private JLabel scoreLabel;
    private JLabel lebenLabel;
    private JTextArea emailAnzeigeArea;
    private JLabel absenderLabel;
    private JLabel betreffLabel;
    private Timer timer;
    private JLabel timerLabel;
    private JLabel feedbackLabel;
    private JLabel tippLabel;
    private JPanel pauseGlassPane;
    private JButton sicherButton;
    private JButton phishingButton;

    private Clip clipRichtig;
    private Clip clipFalsch;
    private Clip clipBonus;
    private Clip clipLevelGeschafft;
    private Clip clipGameOver;
    private Clip clipTimerTick;
    private FeedbackCard feedbackCard;
    private TippCard tippCard;
    private PauseMenu pauseMenu;
    private AchievementManager achievementManager;
    private AchievementCard achievementCard;


    public GameScreen(PhishingDefender hauptFenster, int level, AchievementManager manager) {
        this.hauptFenster = hauptFenster;
        this.achievementManager = manager;
        this.level = level;
        this.aktuelleEmailIndex = 0;
        this.score = 0;
        this.richtigeInFolge = 0;
        this.firewallAktiv = false;
        this.firewallCounter = 0;
        this.isPausiert = false;

        // Leben basierend auf Level (wird aus LevelConfig geholt)
        if (level == LevelConfig.L1_LEVEL_NUM) {
            this.maxLeben = LevelConfig.L1_MAX_LEBEN;
        } else if (level == LevelConfig.L2_LEVEL_NUM) {
            this.maxLeben = LevelConfig.L2_MAX_LEBEN;
        } else {
            this.maxLeben = LevelConfig.L3_MAX_LEBEN;
        }
        this.leben = maxLeben;

        // Zeit pro E-Mail basierend auf Level
        if (level == LevelConfig.L1_LEVEL_NUM) {
            this.zeitProEmail = LevelConfig.L1_ZEIT;
        } else if (level == LevelConfig.L2_LEVEL_NUM) {
            this.zeitProEmail = LevelConfig.L2_ZEIT;
        } else {
            this.zeitProEmail = LevelConfig.L3_ZEIT;
        }

        // E-Mails für dieses Level laden
        EmailDatabase database = new EmailDatabase();
        this.emails = database.getEmailsFuerLevel(level);

        setupUI();
        zeigeNaechsteEmail();

        // Tastatur-Input
        setFocusable(true);
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
        });
        ladeSoundsUndStarteSpiel();
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == TASTE_PAUSE) {
                    togglePause();
                } else if (isPausiert) {
                    return;  // Keine anderen Tasten wenn pausiert
                } else if (e.getKeyCode() == TASTE_SICHER) {
                    antwortGeben(false);
                } else if (e.getKeyCode() == TASTE_PHISHING) {
                    antwortGeben(true);
                } else if (e.getKeyCode() == TASTE_ZURUECK) {
                    if (timer != null) {
                        timer.stop();
                    }
                    if (clipTimerTick != null) {
                        clipTimerTick.stop();
                    }
                    hauptFenster.zeigeLevelAuswahl();
                }
            }
        });
    }

    /**
     * Startet einen SwingWorker, um die Sounds im Hintergrund zu laden,
     * ohne die UI einzufrieren. Nach dem Laden wird das Spiel gestartet.
     */
    private void ladeSoundsUndStarteSpiel() {
        // SwingWorker, um Lade-Operationen vom UI-Thread (EDT) fernzuhalten
        SwingWorker<Void, Void> soundLoader = new SwingWorker<>() {

            /**
             * Dieser Code läuft in einem Hintergrund-Thread.
             * Perfekt für I/O (Dateien laden).
             */
            @Override
            protected Void doInBackground() throws Exception {
                // Hier rufen wir die blockierende Lade-Methode auf
                soundsVorladen();
                return null;
            }

            /**
             * Dieser Code läuft wieder auf dem UI-Thread (EDT),
             * nachdem doInBackground() fertig ist.
             * Perfekt, um die UI zu aktualisieren.
             */
            @Override
            protected void done() {
                try {
                    get();
                    sicherButton.setEnabled(true);
                    phishingButton.setEnabled(true);
                    zeigeNaechsteEmail();

                    // NEUER, BESSERER WEG, UM FOKUS ZU SETZEN:
                    // Wir warten einen winzigen Moment (10ms), bis die UI
                    // sich "gesetzt" hat, und holen uns DANN den Fokus.
                    Timer focusTimer = new Timer(10, e -> requestFocusInWindow());
                    focusTimer.setRepeats(false);
                    focusTimer.start();

                } catch (Exception e) {
                    e.printStackTrace();
                    emailAnzeigeArea.setText("Fehler beim Laden der Sounds!\n" + e.getMessage());
                }
            }
        };

        // Starte den Worker
        soundLoader.execute();
    }

    //UI
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_BACKGROUND_DARK); // Unser dunkler Hintergrund

        // === TOP BAR - Gaming HUD ===
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());
        topBar.setBackground(Theme.COLOR_PANEL_DARK);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(100, 100, 100, 60)),
                BorderFactory.createEmptyBorder(20, 35, 20, 35)
        ));
        topBar.setPreferredSize(new Dimension(0, 95));

        // Links: Score
        scoreLabel = new JLabel("⭐ 0");
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        scoreLabel.setForeground(new Color(255, 215, 100));

        // Mitte: Timer
        timerLabel = new JLabel("⏱️ 15s");
        timerLabel.setFont(new Font("Monospace", Font.BOLD, 34));
        timerLabel.setForeground(Theme.COLOR_ACCENT_GREEN); // <-- NEU: Grün
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Rechts: Leben
        lebenLabel = new JLabel(erstelleLebenString());
        lebenLabel.setFont(new Font("Arial", Font.PLAIN, 34));
        lebenLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topBar.add(scoreLabel, BorderLayout.WEST);
        topBar.add(timerLabel, BorderLayout.CENTER);
        topBar.add(lebenLabel, BorderLayout.EAST);

        // === CENTER WRAPPER (WICHTIG FÜR LAYOUT) ===
        JPanel centerWrapper = new JPanel(new GridBagLayout()); // <-- BENUTZT GridBagLayout
        centerWrapper.setBackground(Theme.COLOR_BACKGROUND_DARK);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        // === EMAIL PANEL ===
        JPanel emailPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int cornerRadius = 25;

                g2.setColor(new Color(25, 25, 25, 200));
                g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

                // 2. Der leuchtende "Frost"-Rand
                g2.setColor(new Color(0, 220, 120, 100)); // Akzent-Grün
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);

                // 3. Ein subtiler innerer Rand
                g2.setColor(new Color(150, 150, 150, 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(2, 2, w - 5, h - 5, cornerRadius - 2, cornerRadius - 2);
                g2.dispose();
            }
        };
        emailPanel.setLayout(new BorderLayout(0, 0));
        emailPanel.setOpaque(false); // WICHTIG: Muss durchsichtig sein

        // === EMAIL HEADER ===
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false); // <-- WICHTIG: Kind ist durchsichtig
        headerPanel.setBackground(new Color(0, 0, 0, 0)); // <-- WICHTIG: Kind ist unsichtbar
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 220, 120, 100)), // Grüne Linie
                BorderFactory.createEmptyBorder(22, 28, 22, 28)
        ));

        absenderLabel = new JLabel("📨 Von: ");
        absenderLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        absenderLabel.setForeground(Theme.COLOR_TEXT_SECONDARY);
        absenderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        betreffLabel = new JLabel("📌 Betreff: ");
        betreffLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        betreffLabel.setForeground(new Color(255, 255, 255));
        betreffLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel datumLabel = new JLabel("📅 18. Okt 2025, 14:30");
        datumLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        datumLabel.setForeground(Theme.COLOR_TEXT_SECONDARY);
        datumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(absenderLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 9)));
        headerPanel.add(betreffLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 9)));
        headerPanel.add(datumLabel);

        // === EMAIL BODY  ===
        emailAnzeigeArea = new JTextArea();
        emailAnzeigeArea.setText("Lade Sounds, bitte warten...");
        emailAnzeigeArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailAnzeigeArea.setEditable(false);
        emailAnzeigeArea.setLineWrap(true);
        emailAnzeigeArea.setWrapStyleWord(true);
        emailAnzeigeArea.setForeground(new Color(220, 220, 220));
        emailAnzeigeArea.setCaretColor(Theme.COLOR_ACCENT_GREEN); // <-- NEU: Grüner Caret
        emailAnzeigeArea.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        emailAnzeigeArea.setOpaque(false); // <-- WICHTIG: Textfeld ist DURCHSICHTIG
        emailAnzeigeArea.setBackground(new Color(0, 0, 0, 0)); // <-- WICHTIG: Unsichtbar

        JScrollPane scrollPane = new JScrollPane(emailAnzeigeArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false); // <-- WICHTIG: ScrollPane ist DURCHSICHTIG
        scrollPane.getViewport().setOpaque(false); // <-- WICHTIG: Viewport ist DURCHSICHTIG
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));

        // === FEEDBACK PANEL  ===
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        feedbackPanel.setOpaque(false); // Ist selbst durchsichtig
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20)); // Padding unten entfernt
        feedbackPanel.setPreferredSize(new Dimension(850, 150));

        // Custom Cards
        feedbackCard = new FeedbackCard("✓", "RICHTIG! +10 Punkte", Theme.COLOR_ACCENT_GREEN); // <-- Nutzt Theme
        feedbackCard.setVisible(false);
        feedbackCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        tippCard = new TippCard();
        tippCard.setVisible(false);
        tippCard.setAlignmentX(Component.CENTER_ALIGNMENT);


        feedbackPanel.add(feedbackCard);
        feedbackPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        feedbackPanel.add(tippCard);
        feedbackPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Abstand

        // === ZUSAMMENBAUEN  ===

        // 1. Email-Fenster (Header + Text) in das emailPanel
        emailPanel.add(headerPanel, BorderLayout.NORTH);
        emailPanel.add(scrollPane, BorderLayout.CENTER);



        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH; // Fülle horizontal und vertikal
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerWrapper.add(emailPanel, gbc);
        gbc.gridy = 1;
        gbc.weighty = 0;
        centerWrapper.add(feedbackPanel, gbc);

        // === BOTTOM: Buttons ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 28));
        buttonPanel.setBackground(Theme.COLOR_BACKGROUND_DARK);

        sicherButton = Theme.createStyledButton(
                "✅ SICHER (A)",
                Theme.FONT_BUTTON_LARGE,
                Theme.COLOR_BUTTON_GREEN,
                Theme.COLOR_BUTTON_GREEN_HOVER,
                Theme.PADDING_BUTTON_LARGE
        );
        sicherButton.setEnabled(false);
        sicherButton.setPreferredSize(new Dimension(310, 75));
        sicherButton.addActionListener(e -> antwortGeben(false));

        phishingButton = Theme.createStyledButton(
                "⚠️ PHISHING (L)",
                Theme.FONT_BUTTON_LARGE,
                Theme.COLOR_BUTTON_RED,
                Theme.COLOR_BUTTON_RED_HOVER,
                Theme.PADDING_BUTTON_LARGE
        );
        phishingButton.setEnabled(false);
        phishingButton.setPreferredSize(new Dimension(310, 75));
        phishingButton.addActionListener(e -> antwortGeben(true));

        buttonPanel.add(sicherButton);
        buttonPanel.add(phishingButton);

        // 1. Erstelle das "Pause"-Label
        JLabel pauseHintLabel = new JLabel("Drücke [LEERTASTE] zum Pausieren");
        pauseHintLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        pauseHintLabel.setForeground(new Color(160, 160, 160)); // Helles Grau
        pauseHintLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Wichtig für Zentrierung

        // 2. Erstelle einen neuen Wrapper-Panel, der Buttons UND Label hält
        JPanel southWrapper = new JPanel();
        southWrapper.setLayout(new BoxLayout(southWrapper, BoxLayout.Y_AXIS));
        southWrapper.setBackground(Theme.COLOR_BACKGROUND_DARK); // Wichtig: Gleiche Hintergrundfarbe

        southWrapper.add(buttonPanel); // Fügt den Panel mit den Knöpfen (oben) hinzu
        southWrapper.add(pauseHintLabel); // Fügt dein neues Label (darunter) hinzu
        southWrapper.add(Box.createRigidArea(new Dimension(0, 20))); // Fügt 20px Luft am unteren Rand hinzu


        achievementCard = new AchievementCard();

        JPanel topAreaWrapper = new JPanel();
        topAreaWrapper.setOpaque(false);
        topAreaWrapper.setLayout(new BoxLayout(topAreaWrapper, BoxLayout.Y_AXIS));


        topAreaWrapper.add(achievementCard);

        topAreaWrapper.add(topBar);

        // === FINALES LAYOUT ===
        add(topAreaWrapper, BorderLayout.NORTH); // <-- Hier den Wrapper einfügen
        add(centerWrapper, BorderLayout.CENTER);
        add(southWrapper, BorderLayout.SOUTH);
    }

    private void zeigeNaechsteEmail() {
        if (aktuelleEmailIndex >= emails.size()) {
            levelAbgeschlossen();
            return;
        }

        Email email = emails.get(aktuelleEmailIndex);
        absenderLabel.setText("📨 Von: " + email.getAbsender());
        betreffLabel.setText("📌 Betreff: " + email.getBetreff());
        emailAnzeigeArea.setText(email.getNachricht());

        starteTimer();
        requestFocusInWindow();
    }

    private void antwortGeben(boolean spielerSagtPhishing) {
        if (timer != null) {
            timer.stop();
        }

        if (clipTimerTick != null) {
            clipTimerTick.stop();
        }

        Email email = emails.get(aktuelleEmailIndex);
        boolean istRichtig = (email.istPhishing() == spielerSagtPhishing);

        if (istRichtig) {
            richtigeInFolge++;

            // Bonus-Punkte wenn Firewall aktiv
            int punkte = firewallAktiv ? BONUS_PUNKTE : NORMALE_PUNKTE;
            score += punkte;
            scoreLabel.setText("⭐ Score: " + score);

            // ===  ACHIEVEMENTS PRÜFEN ===
            if (email.istPhishing()) {
                if (achievementManager.unlockAchievement("FIRST_CATCH")) {
                    // NUR DANN das Pop-up zeigen
                    achievementCard.showAchievement("Erster Fang!");
                }
            }
            if (richtigeInFolge == 10) {
                if (achievementManager.unlockAchievement("STREAK_10")) {
                    // NUR DANN das Pop-up zeigen
                    achievementCard.showAchievement("Adlerauge");
                }
            }
            // === ENDE ===
            zeigeFeedbackRichtig(punkte);

            // Firewall aktivieren bei X richtigen
            if (richtigeInFolge >= BONUS_RICHTIGE_NOETIG && !firewallAktiv) {
                aktiviereFirewall();
            }

            Timer naechsteEmailTimer = new Timer(1000, e -> {
                aktuelleEmailIndex++;
                zeigeNaechsteEmail();
                requestFocusInWindow();
            });
            naechsteEmailTimer.setRepeats(false);
            naechsteEmailTimer.start();

        } else {
            richtigeInFolge = 0;
            firewallAktiv = false;
            firewallCounter = 0;

            leben--;
            lebenLabel.setText(erstelleLebenString());

            zeigeFeedbackFalsch("FALSCH! -1 ❤️");

            if (leben <= 0) {
                gameOver();
                return;
            }

            // Bei falsch: Längere Verzögerung (2.6 Sekunden) wegen Tipp
            Timer naechsteEmailTimer = new Timer(3200, e -> {
                aktuelleEmailIndex++;
                zeigeNaechsteEmail();
                requestFocusInWindow();
            });
            naechsteEmailTimer.setRepeats(false);
            naechsteEmailTimer.start();
        }

        // Firewall-Counter runterzählen
        if (firewallAktiv) {
            firewallCounter--;
            if (firewallCounter <= 0) {
                deaktiviereFirewall();
            }
        }


    }

    private void levelAbgeschlossen() {
        playSound(clipLevelGeschafft);
        hauptFenster.levelGeschafft(level);
        hauptFenster.zeigeResultScreen(level, score, leben, maxLeben, emails.size(), true);
    }

    // Spiel vorbei - keine Leben mehr
    private void gameOver() {
        playSound(clipGameOver);
        hauptFenster.zeigeResultScreen(level, score, 0, maxLeben, emails.size(), false);
    }

    // Startet den Timer für die aktuelle E-Mail
    private void starteTimer() {
        // Alten Timer stoppen falls vorhanden
        if (timer != null) {
            timer.stop();
        }

        // Zeit anpassen wenn Firewall aktiv
        int bonusZeit = firewallAktiv ? (int)(zeitProEmail * BONUS_ZEIT_MULTIPLIKATOR) : 0;
        verbleibendeSekunden = zeitProEmail + bonusZeit;

        // Timer-Farbe ändern wenn Firewall aktiv
        if (firewallAktiv) {
            timerLabel.setForeground(new Color(100, 200, 255));
            timerLabel.setText("⏱️ Zeit: " + verbleibendeSekunden + "s 🛡️");
        } else {
            timerLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
            timerLabel.setText("⏱️ Zeit: " + verbleibendeSekunden + "s");
        }

        if (verbleibendeSekunden <= 7 && verbleibendeSekunden > 0) {
            // Starte den Tick-Sound (im Loop), falls er nicht schon läuft
            if (clipTimerTick != null && !clipTimerTick.isRunning()) {
                clipTimerTick.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }

        // Neuer Timer - jede Sekunde
        timer = new Timer(1000, e -> {
            verbleibendeSekunden--;

            if (firewallAktiv) {
                timerLabel.setText("⏱️ Zeit: " + verbleibendeSekunden + "s 🛡️");
            } else {
                timerLabel.setText("⏱️ Zeit: " + verbleibendeSekunden + "s");
            }

            if (verbleibendeSekunden <= 7 && verbleibendeSekunden > 0) {
                if (clipTimerTick != null && !clipTimerTick.isRunning()) {
                    clipTimerTick.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }

            if (verbleibendeSekunden <= 0) {
                timer.stop();
                if (clipTimerTick != null) clipTimerTick.stop(); // <-- WICHTIG: Stoppen!
                zeitAbgelaufen();
            }
        });
        timer.start();
    }

    private void zeitAbgelaufen() {
        leben--;
        lebenLabel.setText(erstelleLebenString());

        zeigeFeedbackFalsch("⏱️ ZEIT ABGELAUFEN! -1 ❤️");

        if (leben <= 0) {
            gameOver();
            return;
        }

        aktuelleEmailIndex++;
        zeigeNaechsteEmail();
        requestFocusInWindow();
    }

    // Aktiviert FireWall Bounus
    private void aktiviereFirewall() {
        if (achievementManager.unlockAchievement("FIREWALL")) {
            achievementCard.showAchievement("Brandheiß!");
        }
        playSound(clipBonus);
        firewallAktiv = true;
        firewallCounter = BONUS_DAUER_EMAILS;
        richtigeInFolge = 0;

        setBackground(new Color(40, 60, 80));
        int prozent = (int)(BONUS_ZEIT_MULTIPLIKATOR * 100);

        //DIALOG
        JDialog firewallDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "🛡️ Firewall Aktiviert!", true);
        firewallDialog.setLayout(new BorderLayout());
        firewallDialog.setUndecorated(true);

        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BorderLayout(0, 20));
        dialogPanel.setBackground(Theme.COLOR_PANEL_DARK);

        dialogPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GREEN, 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));


        JLabel titleLabel = new JLabel("🛡️ FIREWALL AKTIVIERT! 🛡️", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Theme.COLOR_ACCENT_GREEN);

        // Info
        JLabel infoLabel = new JLabel(
                "<html><center style='line-height: 1.6;'>" +
                        "<span style='font-size: 16px; color: #FFFFFF;'><b>Bonus für die nächsten " + BONUS_DAUER_EMAILS + " E-Mails:</b></span><br><br>" +
                        "<span style='font-size: 15px; color: #AAAAAA;'>⏱️ <b>" + prozent + "% mehr Zeit</b> pro Email</span><br>" +
                        "<span style='font-size: 15px; color: #AAAAAA;'>⭐ <b>" + BONUS_PUNKTE + " Punkte</b> statt " + NORMALE_PUNKTE + "</span>" +
                        "</center></html>",
                JLabel.CENTER
        );

        // OK Button
        JButton okButton = Theme.createStyledButton(
                "✓ WEITER",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_BLUE,
                Theme.COLOR_BUTTON_BLUE_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        okButton.setPreferredSize(new Dimension(180, 50));
        okButton.addActionListener(e -> firewallDialog.dispose());

        // ENTER-Taste
        okButton.registerKeyboardAction(
                e -> firewallDialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Dialog auf Enter reagieren lassen
        firewallDialog.getRootPane().setDefaultButton(okButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        dialogPanel.add(titleLabel, BorderLayout.NORTH);
        dialogPanel.add(infoLabel, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        firewallDialog.add(dialogPanel);
        firewallDialog.pack();
        firewallDialog.setLocationRelativeTo(this);
        firewallDialog.setVisible(true);
    }

    // Deaktiviert den Firewall- Bonus
    private void deaktiviereFirewall() {
        firewallAktiv = false;
        setBackground(new Color(26, 26, 46));
        timerLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
    }

    // Pause oder fortsetzen
    private void togglePause() {
        if (isPausiert) {
            fortsetzen();
        } else {
            pausieren();
        }
    }

    // Pausiert das Spiel
    private void pausieren() {
        isPausiert = true;

        // Timer stoppen
        if (timer != null) {
            timer.stop();
        }

        if (clipTimerTick != null) {
            clipTimerTick.stop();
        }

        // Pause-Menü
        pauseMenu = new PauseMenu(this, score, leben, verbleibendeSekunden);

        // GlassPane vorbereiten
        pauseGlassPane = new JPanel(new BorderLayout());
        pauseGlassPane.setOpaque(false);
        pauseGlassPane.add(pauseMenu, BorderLayout.CENTER);

        // GlassPane aktivieren
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setGlassPane(pauseGlassPane);
            frame.getGlassPane().setVisible(true);
        }
    }

    /**
     * Setzt das pausierte Spiel fort.
     */

    public void fortsetzen() {
        isPausiert = false;

        // GlassPane verstecken
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null && frame.getGlassPane() != null) {
            frame.getGlassPane().setVisible(false);
        }

        pauseMenu = null;
        pauseGlassPane = null;

        // Timer fortsetzen
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }

        if (verbleibendeSekunden <= 7 && verbleibendeSekunden > 0) {
            if (clipTimerTick != null && !clipTimerTick.isRunning()) {
                clipTimerTick.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }

        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }


    /**
     * Startet das aktuelle Level neu.
     */

    public void levelNeuStarten() {
        if (timer != null) {
            timer.stop();
        }
        if (clipTimerTick != null) {
            clipTimerTick.stop();
        }
        isPausiert = false;

        // GlassPane verstecken
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null && frame.getGlassPane() != null) {
            frame.getGlassPane().setVisible(false);
        }

        pauseMenu = null;
        pauseGlassPane = null;
        hauptFenster.starteLevel(level);
    }

    /**
     * Kehrt vom Spiel zum Hauptmenü (Levelauswahl) zurück.
     */

    public void zumHauptmenue() {
        if (timer != null) {
            timer.stop();
        }
        if (clipTimerTick != null) {
            clipTimerTick.stop();
        }
        isPausiert = false;

        // GlassPane verstecken
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null && frame.getGlassPane() != null) {
            frame.getGlassPane().setVisible(false);
        }

        pauseMenu = null;
        pauseGlassPane = null;

        // Zum Hauptmenü
        hauptFenster.zeigeLevelAuswahl();
    }


    /**
     * Helfer-Methode, um Code-Duplikate in zeigeFeedbackRichtig/Falsch zu vermeiden.
     * Nimmt eine (rote oder grüne) FeedbackCard, löscht die alte und zeigt die neue an.
     */
    private void zeigeFeedbackCard(FeedbackCard neueCard) {
        Container parent = feedbackCard.getParent();

        if (parent instanceof JPanel) {
            JPanel feedbackPanel = (JPanel) parent;
            feedbackPanel.removeAll(); // Lösche alte Cards

            neueCard.setAlignmentX(Component.CENTER_ALIGNMENT);
            tippCard.setAlignmentX(Component.CENTER_ALIGNMENT);

            feedbackPanel.add(neueCard);
            feedbackPanel.add(tippCard);

            // UI aktualisieren
            feedbackPanel.revalidate();
            feedbackPanel.repaint();

            feedbackCard = neueCard;
        }

        feedbackCard.showWithAnimation();
    }

    private void zeigeFeedbackRichtig(int punkte) {
        playSound(clipRichtig);

        FeedbackCard neueCard = new FeedbackCard("✅", "RICHTIG!  +" + punkte + " Punkte", new Color(50, 180, 100));

        zeigeFeedbackCard(neueCard);

        Timer hideTimer = new Timer(1200, e -> feedbackCard.setVisible(false));
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void zeigeFeedbackFalsch(String grund) {
        playSound(clipFalsch);

        FeedbackCard neueCard = new FeedbackCard("❌", grund, new Color(220, 50, 50));

        zeigeFeedbackCard(neueCard);

        zeigeTipp();

        Timer hideTimer = new Timer(2800, e -> feedbackCard.setVisible(false));
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void zeigeTipp() {
        Email aktuelleEmail = emails.get(aktuelleEmailIndex);
        String tipp = aktuelleEmail.getTipp();

        tippCard.showTipp(tipp);

        Timer hideTimer = new Timer(2800, e -> tippCard.setVisible(false));
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    // Lädt alle Sounds beim Start
    private void soundsVorladen() {
        clipRichtig = soundLaden("richtig.wav");
        clipFalsch = soundLaden("falsch.wav");
        clipBonus = soundLaden("bonus.wav");
        clipLevelGeschafft = soundLaden("level_geschafft.wav");
        clipGameOver = soundLaden("game_over.wav");
        clipTimerTick = soundLaden("timer_tick.wav");
    }

    private Clip soundLaden(String dateiname) {
        try {
            java.net.URL soundURL = getClass().getResource("/games/phishingdefender/assets/sounds/" + dateiname);

            if (soundURL == null) {
                System.out.println("Sound nicht gefunden (als Ressource): " + dateiname);
                return null;
            }

            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                return clip;
            }

        } catch (Exception e) {
            System.out.println("Fehler beim Laden von " + dateiname + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);  // Von vorne starten
            clip.start();
        }
    }

    private String erstelleLebenString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>"); // Starte HTML-Modus für das Label

        // Definiere unsere Herzen (Rot und ein dunkles Grau)
        String rotesHerz = "<font color='#E03030'>❤️</font>";
        String grauesHerz = "<font color='#444444'>🖤</font>";

        for (int i = 0; i < leben; i++) {
            sb.append(rotesHerz);
        }
        for (int i = leben; i < maxLeben; i++) {
            sb.append(grauesHerz);
        }

        sb.append("</html>"); // Beende HTML-Modus
        return sb.toString();
    }
}

