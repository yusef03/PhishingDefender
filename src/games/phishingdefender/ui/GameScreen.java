package games.phishingdefender.ui;

import games.phishingdefender.*;
import games.phishingdefender.data.Email;
import games.phishingdefender.managers.AchievementManager;
import games.phishingdefender.managers.EmailDatabase;
import games.phishingdefender.ui.components.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Das Haupt-Panel für das eigentliche Gameplay ("Level").
 * Zeigt E-Mails an, verwaltet den Timer, Score, Leben und Spieler-Antworten.
 *
 * @author yusef03
 * @version 1.7
 */
public class GameScreen extends JPanel {

    // Steuerung Config
    private static final int TASTE_SICHER = KeyEvent.VK_A;
    private static final int TASTE_PHISHING = KeyEvent.VK_L;
    private static final int TASTE_PAUSE = KeyEvent.VK_SPACE;
    private static final int TASTE_ZURUECK = KeyEvent.VK_ESCAPE;
    private static final int TIMER_INTERVALL_MS = 50;

    private PhishingDefender hauptFenster;
    private int level;
    private List<Email> emails;
    private int aktuelleEmailIndex;
    private int score;
    private int leben;
    private int maxLeben;

    private long zeitProEmailMillis;
    private long verbleibendeMillis;
    private long maxMillisFuerEmail;
    private int sekundenCounterFuerSound;

    private int richtigeInFolge;
    private boolean firewallAktiv;
    private int firewallCounter;
    private boolean isPausiert;
    private int verbleibendeTipps;
    private boolean tippWurdeVerwendet;
    private int tippKostenSekunden;

    // UI Komponenten
    private JLabel scoreLabel;
    private JEditorPane emailAnzeigeArea;
    private JLabel absenderLabel;
    private JLabel betreffLabel;
    private Timer timer;
    private TimerBarPanel timerBarPanel;
    private JLabel timerLabel;
    private JButton sicherButton;
    private JButton phishingButton;
    private JButton tippButton;

    // UI-Komponenten für Widgets
    private IntegrityShieldPanel shieldPanel;
    private StreakBonusBar streakBonusBar;
    private JTextArea scoreLogArea;
    private JTextArea livesLogArea;

    // UI-Komponenten für Scan-Animation
    private JPanel emailContentWrapper;
    private boolean isScanning = false;
    private int scanLineY = 0;
    private Timer scanAnimationTimer;

    // Sound-Clips
    private Clip clipRichtig;
    private Clip clipFalsch;
    private Clip clipBonus;
    private Clip clipLevelGeschafft;
    private Clip clipGameOver;
    private Clip clipTimerTick;

    // Overlay-Karten
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
        this.tippWurdeVerwendet = false;
        this.isPausiert = false;
        this.isScanning = false;

        if (level == LevelConfig.L1_LEVEL_NUM) {
            this.maxLeben = LevelConfig.L1_MAX_LEBEN;
        } else if (level == LevelConfig.L2_LEVEL_NUM) {
            this.maxLeben = LevelConfig.L2_MAX_LEBEN;
        } else {
            this.maxLeben = LevelConfig.L3_MAX_LEBEN;
        }
        this.leben = maxLeben;

        if (level == LevelConfig.L1_LEVEL_NUM) {
            this.verbleibendeTipps = LevelConfig.L1_ANZAHL_TIPPS;
            this.tippKostenSekunden = LevelConfig.L1_TIPP_KOSTEN;
        } else if (level == LevelConfig.L2_LEVEL_NUM) {
            this.verbleibendeTipps = LevelConfig.L2_ANZAHL_TIPPS;
            this.tippKostenSekunden = LevelConfig.L2_TIPP_KOSTEN;
        } else {
            this.verbleibendeTipps = LevelConfig.L3_ANZAHL_TIPPS;
            this.tippKostenSekunden = LevelConfig.L3_TIPP_KOSTEN;
        }

        if (level == LevelConfig.L1_LEVEL_NUM) {
            this.zeitProEmailMillis = LevelConfig.L1_ZEIT * 1000L;
        } else if (level == LevelConfig.L2_LEVEL_NUM) {
            this.zeitProEmailMillis = LevelConfig.L2_ZEIT * 1000L;
        } else {
            this.zeitProEmailMillis = LevelConfig.L3_ZEIT * 1000L;
        }

        EmailDatabase database = new EmailDatabase();
        this.emails = database.getEmailsFuerLevel(level);

        setupUI();
        zeigeNaechsteEmail();

        setFocusable(true);
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
        ladeSoundsUndStarteSpiel();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == TASTE_PAUSE) {
                    togglePause();
                } else if (isPausiert || isScanning) {
                    return;
                } else if (e.getKeyCode() == TASTE_SICHER) {
                    antwortGeben(false);
                } else if (e.getKeyCode() == TASTE_PHISHING) {
                    antwortGeben(true);
                } else if (e.getKeyCode() == TASTE_ZURUECK) {
                    stopAllTimers();
                    hauptFenster.zeigeLevelAuswahl();
                }
            }
        });
    }

    private void ladeSoundsUndStarteSpiel() {
        SwingWorker<Void, Void> soundLoader = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                soundsVorladen();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    sicherButton.setEnabled(true);
                    phishingButton.setEnabled(true);
                    zeigeNaechsteEmail();
                    Timer focusTimer = new Timer(10, e -> requestFocusInWindow());
                    focusTimer.setRepeats(false);
                    focusTimer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    emailAnzeigeArea.setText("Fehler beim Laden der Sounds!\n" + e.getMessage());
                }
            }
        };
        soundLoader.execute();
    }

    //UI
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Theme.COLOR_BACKGROUND_DARK);

        // === ZONE 1: TOP (Timer + Achievement Card) ===
        timerLabel = new JLabel("⏱️ --s");
        timerLabel.setFont(Theme.FONT_HUD);
        timerLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        timerBarPanel = new TimerBarPanel();

        JPanel timerWrapperPanel = new JPanel();
        timerWrapperPanel.setOpaque(false);
        timerWrapperPanel.setLayout(new BoxLayout(timerWrapperPanel, BoxLayout.Y_AXIS));
        timerWrapperPanel.add(timerLabel);
        timerWrapperPanel.add(timerBarPanel);
        timerWrapperPanel.setBorder(BorderFactory.createEmptyBorder(15, 100, 0, 100));

        achievementCard = new AchievementCard();

        JPanel topWrapper = new JPanel();
        topWrapper.setOpaque(false);
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.add(achievementCard);
        topWrapper.add(timerWrapperPanel);

        add(topWrapper, BorderLayout.NORTH);

        // === ZONE 2: WEST (Score-Widget) ===
        JPanel scoreWidget = createScoreWidget();
        add(scoreWidget, BorderLayout.WEST);

        // === ZONE 3: EAST (Leben-Widget) ===
        JPanel livesWidget = createLivesWidget();
        add(livesWidget, BorderLayout.EAST);

        // === ZONE 4: SOUTH (Aktionsleiste) ===
        JPanel southWrapper = createSouthActionbar();
        add(southWrapper, BorderLayout.SOUTH);

        // === ZONE 5: CENTER (E-Mail) ===
        JPanel centerEmailWrapper = createCenterEmailPanel();
        add(centerEmailWrapper, BorderLayout.CENTER);
    }

    /**
     * Erstellt das linke Score-Widget
     */
    private JPanel createScoreWidget() {
        JPanel widget = new JPanel();
        widget.setLayout(new BorderLayout(0, 10));
        widget.setBackground(Theme.COLOR_PANEL_DARK);
        widget.setPreferredSize(new Dimension(180, 0));
        widget.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, Theme.COLOR_ACCENT_BLUE),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("SCORE", JLabel.CENTER);
        title.setFont(Theme.FONT_BUTTON_MEDIUM);
        title.setForeground(Theme.COLOR_TEXT_SECONDARY);
        widget.add(title, BorderLayout.NORTH);

        scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Monospace", Font.BOLD, 48));
        scoreLabel.setForeground(new Color(255, 215, 100));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        widget.add(scoreLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JLabel streakTitle = new JLabel("<html><center>FIREWALL<br>CHARGE</center></html>", JLabel.CENTER);
        streakTitle.setFont(Theme.FONT_BUTTON_SMALL);
        streakTitle.setForeground(Theme.COLOR_TEXT_SECONDARY);
        streakTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        streakTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        streakBonusBar = new StreakBonusBar();
        streakBonusBar.updateStreak(0, LevelConfig.BONUS_SERIE_NOETIG);

        JSeparator logSeparator = new JSeparator();
        logSeparator.setForeground(Theme.COLOR_BUTTON_NEUTRAL);
        logSeparator.setBackground(Theme.COLOR_PANEL_DARK);

        JLabel logTitle = new JLabel("Score-Log", JLabel.CENTER);
        logTitle.setFont(Theme.FONT_BUTTON_SMALL);
        logTitle.setForeground(Theme.COLOR_TEXT_SECONDARY);
        logTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLogArea = new JTextArea();
        scoreLogArea.setFont(new Font("Monospace", Font.PLAIN, 10));
        scoreLogArea.setForeground(Theme.COLOR_TEXT_SECONDARY);
        scoreLogArea.setBackground(new Color(15, 15, 15));
        scoreLogArea.setEditable(false);

        JScrollPane scoreScroll = new JScrollPane(scoreLogArea);
        scoreScroll.setPreferredSize(new Dimension(140, 150));
        scoreScroll.setBorder(null);
        scoreScroll.setOpaque(false);
        scoreScroll.getViewport().setOpaque(false);

        bottomPanel.add(streakTitle);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        bottomPanel.add(streakBonusBar);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bottomPanel.add(logSeparator);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(logTitle);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        bottomPanel.add(scoreScroll);

        widget.add(bottomPanel, BorderLayout.SOUTH);

        return widget;
    }

    /**
     * Erstellt das rechte Leben-Widget
     */
    private JPanel createLivesWidget() {
        JPanel widget = new JPanel();
        widget.setLayout(new BorderLayout(0, 10));
        widget.setBackground(Theme.COLOR_PANEL_DARK);
        widget.setPreferredSize(new Dimension(180, 0));
        widget.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Theme.COLOR_ACCENT_BLUE),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("<html><center>SYSTEM-<br>INTEGRITÄT</center></html>", JLabel.CENTER);
        title.setFont(Theme.FONT_BUTTON_MEDIUM);
        title.setForeground(Theme.COLOR_TEXT_SECONDARY);
        widget.add(title, BorderLayout.NORTH);

        shieldPanel = new IntegrityShieldPanel();
        shieldPanel.updateShield(leben, maxLeben);
        widget.add(shieldPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JSeparator logSeparator = new JSeparator();
        logSeparator.setForeground(Theme.COLOR_BUTTON_NEUTRAL);
        logSeparator.setBackground(Theme.COLOR_PANEL_DARK);

        JLabel logTitle = new JLabel("Fehler-Log", JLabel.CENTER);
        logTitle.setFont(Theme.FONT_BUTTON_SMALL);
        logTitle.setForeground(Theme.COLOR_TEXT_SECONDARY);
        logTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        livesLogArea = new JTextArea();
        livesLogArea.setFont(new Font("Monospace", Font.PLAIN, 10));
        livesLogArea.setForeground(Theme.COLOR_TIMER_MEDIUM);
        livesLogArea.setBackground(new Color(15, 15, 15));
        livesLogArea.setEditable(false);

        JScrollPane livesScroll = new JScrollPane(livesLogArea);
        livesScroll.setPreferredSize(new Dimension(140, 150 + 12 + 15 + 15 + 5));
        livesScroll.setBorder(null);
        livesScroll.setOpaque(false);
        livesScroll.getViewport().setOpaque(false);

        bottomPanel.add(logSeparator);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(logTitle);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        bottomPanel.add(livesScroll);

        widget.add(bottomPanel, BorderLayout.SOUTH);

        return widget;
    }

    /**
     * Erstellt das zentrale E-Mail-Panel
     */
    private JPanel createCenterEmailPanel() {
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
                g2.setColor(new Color(0, 220, 120, 100));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
                g2.setColor(new Color(150, 150, 150, 50));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(2, 2, w - 5, h - 5, cornerRadius - 2, cornerRadius - 2);
                g2.dispose();
            }
        };
        emailPanel.setLayout(new BorderLayout(0, 0));
        emailPanel.setOpaque(false);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 220, 120, 100)),
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

        emailAnzeigeArea = new JEditorPane();
        emailAnzeigeArea.setContentType("text/html");
        emailAnzeigeArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        emailAnzeigeArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailAnzeigeArea.setText("Lade E-Mail...");
        emailAnzeigeArea.setEditable(false);
        emailAnzeigeArea.setForeground(new Color(220, 220, 220));
        emailAnzeigeArea.setCaretColor(Theme.COLOR_ACCENT_GREEN);
        emailAnzeigeArea.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        emailAnzeigeArea.setOpaque(false);
        emailAnzeigeArea.setBackground(new Color(0, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(emailAnzeigeArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        emailContentWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isScanning) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color scanColor = Theme.COLOR_ACCENT_BLUE;
                    g2.setColor(new Color(scanColor.getRed(), scanColor.getGreen(), scanColor.getBlue(), 150));
                    g2.fillRect(0, scanLineY, getWidth(), 6);
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, scanLineY + 2, getWidth(), 2);
                    g2.dispose();
                }
            }
        };
        emailContentWrapper.setOpaque(false);
        emailContentWrapper.setLayout(new BorderLayout());
        emailContentWrapper.add(headerPanel, BorderLayout.NORTH);
        emailContentWrapper.add(scrollPane, BorderLayout.CENTER);

        emailPanel.add(emailContentWrapper, BorderLayout.CENTER);

        JPanel emailWrapper = new JPanel(new BorderLayout());
        emailWrapper.setOpaque(false);
        emailWrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));
        emailWrapper.add(emailPanel, BorderLayout.CENTER);

        return emailWrapper;
    }

    /**
     * Erstellt die untere Aktionsleiste
     */
    private JPanel createSouthActionbar() {
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        feedbackPanel.setOpaque(false);
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        feedbackPanel.setPreferredSize(new Dimension(850, 150));
        feedbackCard = new FeedbackCard("✓", "RICHTIG! +10 Punkte", Theme.COLOR_ACCENT_GREEN);
        feedbackCard.setVisible(false);
        feedbackCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        tippCard = new TippCard();
        tippCard.setVisible(false);
        tippCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackPanel.add(feedbackCard);
        feedbackPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        feedbackPanel.add(tippCard);

        tippButton = Theme.createStyledButton(
                "🔬 E-MAIL SCANNEN (" + verbleibendeTipps + ")",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_ACCENT_BLUE,
                Theme.COLOR_ACCENT_BLUE_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        tippButton.setPreferredSize(new Dimension(310, 60));
        tippButton.addActionListener(e -> starteHeaderScan());

        JPanel tippButtonPanel = new JPanel();
        tippButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        tippButtonPanel.setOpaque(false);
        tippButtonPanel.add(tippButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 28));
        buttonPanel.setOpaque(false);
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

        JLabel pauseHintLabel = new JLabel("Drücke [LEERTASTE] zum Pausieren");
        pauseHintLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        pauseHintLabel.setForeground(new Color(160, 160, 160));
        pauseHintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel southWrapper = new JPanel();
        southWrapper.setLayout(new BoxLayout(southWrapper, BoxLayout.Y_AXIS));
        southWrapper.setOpaque(false);
        southWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        southWrapper.add(feedbackPanel);
        southWrapper.add(tippButtonPanel);
        southWrapper.add(Box.createRigidArea(new Dimension(0, 15)));
        southWrapper.add(buttonPanel);
        southWrapper.add(pauseHintLabel);
        southWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

        return southWrapper;
    }


    private void zeigeNaechsteEmail() {
        this.tippWurdeVerwendet = false;
        updateTippButtonStatus();

        if (aktuelleEmailIndex >= emails.size()) {
            levelAbgeschlossen();
            return;
        }

        Email email = emails.get(aktuelleEmailIndex);
        absenderLabel.setText("📨 Von: " + email.getAbsender());
        betreffLabel.setText("📌 Betreff: " + email.getBetreff());

        String emailBody = email.getNachricht().replaceAll("\n", "<br>");
        emailAnzeigeArea.setText("<html><body style='font-family: SansSerif; font-size: 16px; color: #DCDCDC;'>" + emailBody + "</body></html>");

        SwingUtilities.invokeLater(() -> emailAnzeigeArea.setCaretPosition(0));

        starteTimer();
        requestFocusInWindow();
    }

    private void antwortGeben(boolean spielerSagtPhishing) {
        if (isScanning) return;

        if (timer != null) timer.stop();
        if (clipTimerTick != null) clipTimerTick.stop();

        Email email = emails.get(aktuelleEmailIndex);
        boolean istRichtig = (email.istPhishing() == spielerSagtPhishing);

        if (istRichtig) {
            richtigeInFolge++;
            int punkte = firewallAktiv ? LevelConfig.PUNKTE_BONUS : LevelConfig.PUNKTE_NORMAL;
            score += punkte;
            scoreLabel.setText(String.valueOf(score));

            scoreLogArea.append("+ " + punkte + " (Korrekt)\n");
            scoreLogArea.setCaretPosition(scoreLogArea.getDocument().getLength());
            streakBonusBar.updateStreak(richtigeInFolge, LevelConfig.BONUS_SERIE_NOETIG);

            if (email.istPhishing()) {
                if (achievementManager.unlockAchievement("FIRST_CATCH")) {
                    achievementCard.showAchievement("Erster Fang!");
                }
            }
            if (richtigeInFolge == 10) {
                if (achievementManager.unlockAchievement("STREAK_10")) {
                    achievementCard.showAchievement("Adlerauge");
                }
            }
            zeigeFeedbackRichtig(punkte);

            if (richtigeInFolge >= LevelConfig.BONUS_SERIE_NOETIG && !firewallAktiv) {
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
            if (firewallAktiv) deaktiviereFirewall();

            leben--;
            shieldPanel.updateShield(leben, maxLeben);

            livesLogArea.append("FEHLER: Falsch klassifiziert.\n");
            livesLogArea.setCaretPosition(livesLogArea.getDocument().getLength());
            streakBonusBar.updateStreak(richtigeInFolge, LevelConfig.BONUS_SERIE_NOETIG);

            zeigeFeedbackFalsch("FALSCH! -1 ❤️");

            if (leben <= 0) {
                gameOver();
                return;
            }

            Timer naechsteEmailTimer = new Timer(3200, e -> {
                aktuelleEmailIndex++;
                zeigeNaechsteEmail();
                requestFocusInWindow();
            });
            naechsteEmailTimer.setRepeats(false);
            naechsteEmailTimer.start();
        }

        if (firewallAktiv) {
            firewallCounter--;
            if (firewallCounter <= 0) {
                deaktiviereFirewall();
            }
        }
    }

    private void levelAbgeschlossen() {
        stopAllTimers();
        playSound(clipLevelGeschafft);
        hauptFenster.levelGeschafft(level);
        hauptFenster.zeigeResultScreen(level, score, leben, maxLeben, emails.size(), true);
    }

    private void gameOver() {
        stopAllTimers();
        playSound(clipGameOver);
        hauptFenster.zeigeResultScreen(level, score, 0, maxLeben, emails.size(), false);
    }

    private void starteTimer() {
        if (timer != null) timer.stop();

        long bonusZeitMillis = firewallAktiv ? (long)(zeitProEmailMillis * LevelConfig.BONUS_ZEIT_MULTIPLIKATOR) : 0L;
        verbleibendeMillis = zeitProEmailMillis + bonusZeitMillis;
        maxMillisFuerEmail = verbleibendeMillis;
        sekundenCounterFuerSound = 0;

        int maxSekunden = (int) Math.ceil(maxMillisFuerEmail / 1000.0);
        timerBarPanel.setMaxTime(maxSekunden);

        updateTimerDisplay();

        timer = new Timer(TIMER_INTERVALL_MS, e -> {
            verbleibendeMillis -= TIMER_INTERVALL_MS;
            sekundenCounterFuerSound += TIMER_INTERVALL_MS;

            updateTimerDisplay();

            if (sekundenCounterFuerSound >= 1000) {
                sekundenCounterFuerSound = 0;

                int verbleibendeSekunden = (int) Math.ceil(verbleibendeMillis / 1000.0);

                if (verbleibendeSekunden <= 7 && verbleibendeSekunden > 0) {
                    if (clipTimerTick != null && !clipTimerTick.isRunning()) {
                        clipTimerTick.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }
            }

            if (verbleibendeMillis <= 0) {
                timer.stop();
                if (clipTimerTick != null) clipTimerTick.stop();
                zeitAbgelaufen();
            }
        });
        timer.start();
    }

    private void zeitAbgelaufen() {
        if (firewallAktiv) deaktiviereFirewall();

        richtigeInFolge = 0;
        streakBonusBar.updateStreak(richtigeInFolge, LevelConfig.BONUS_SERIE_NOETIG);
        livesLogArea.append("FEHLER: Zeit abgelaufen.\n");
        livesLogArea.setCaretPosition(livesLogArea.getDocument().getLength());

        leben--;
        shieldPanel.updateShield(leben, maxLeben);
        zeigeFeedbackFalsch("⏱️ ZEIT ABGELAUFEN! -1 ❤️");

        if (leben <= 0) {
            gameOver();
            return;
        }

        aktuelleEmailIndex++;
        zeigeNaechsteEmail();
        requestFocusInWindow();
    }

    private void aktiviereFirewall() {
        if (achievementManager.unlockAchievement("FIREWALL")) {
            achievementCard.showAchievement("Brandheiß!");
        }
        playSound(clipBonus);
        firewallAktiv = true;
        timerBarPanel.setFirewallActive(true);
        firewallCounter = LevelConfig.BONUS_DAUER_IN_EMAILS;
        richtigeInFolge = 0;

        streakBonusBar.updateStreak(richtigeInFolge, LevelConfig.BONUS_SERIE_NOETIG);
        scoreLogArea.append("--- FIREWALL AKTIV ---\n");
        scoreLogArea.setCaretPosition(scoreLogArea.getDocument().getLength());

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
        int prozent = (int)(LevelConfig.BONUS_ZEIT_MULTIPLIKATOR * 100);
        JLabel infoLabel = new JLabel(
                "<html><center style='line-height: 1.6;'>" +
                        "<span style='font-size: 16px; color: #FFFFFF;'><b>Bonus für die nächsten " + LevelConfig.BONUS_DAUER_IN_EMAILS + " E-Mails:</b></span><br><br>" +
                        "<span style='font-size: 15px; color: #AAAAAA;'>⏱️ <b>" + prozent + "% mehr Zeit</b> pro Email</span><br>" +
                        "<span style='font-size: 15px; color: #AAAAAA;'>⭐ <b>" + LevelConfig.PUNKTE_BONUS + " Punkte</b> statt " + LevelConfig.PUNKTE_NORMAL + "</span>" +
                        "</center></html>",
                JLabel.CENTER
        );
        JButton okButton = Theme.createStyledButton(
                "✓ WEITER",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_BLUE,
                Theme.COLOR_BUTTON_BLUE_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        okButton.setPreferredSize(new Dimension(180, 50));
        okButton.addActionListener(e -> firewallDialog.dispose());
        okButton.registerKeyboardAction(
                e -> firewallDialog.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
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

    private void deaktiviereFirewall() {
        firewallAktiv = false;
        timerBarPanel.setFirewallActive(false);
        updateTimerDisplay();
        scoreLogArea.append("--- Firewall offline ---\n");
        scoreLogArea.setCaretPosition(scoreLogArea.getDocument().getLength());
    }

    private void stopAllTimers() {
        if (timer != null) timer.stop();
        if (clipTimerTick != null) clipTimerTick.stop();
        if (scanAnimationTimer != null) scanAnimationTimer.stop();
    }

    private void togglePause() {
        if (isPausiert) {
            fortsetzen();
        } else {
            pausieren();
        }
    }

    private void pausieren() {
        isPausiert = true;
        stopAllTimers(); // Stoppt jetzt auch den scanAnimationTimer

        int angezeigteSekunden = (int) Math.ceil(verbleibendeMillis / 1000.0);
        pauseMenu = new PauseMenu(this, score, leben, angezeigteSekunden);

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setGlassPane(pauseMenu);
            frame.getGlassPane().setVisible(true);
        }
        updateTippButtonStatus();
    }

    public void fortsetzen() {
        isPausiert = false;
        isScanning = false;

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null && frame.getGlassPane() != null) {
            frame.getGlassPane().setVisible(false);
        }
        pauseMenu = null;

        // Starte den Haupt-Timer nur, wenn das Spiel nicht gerade scannt
        if (timer != null && !timer.isRunning() && !isScanning) {
            timer.start();
        }

        int verbleibendeSekunden = (int) Math.ceil(verbleibendeMillis / 1000.0);
        if (verbleibendeSekunden <= 7 && verbleibendeSekunden > 0) {
            if (clipTimerTick != null && !clipTimerTick.isRunning()) {
                clipTimerTick.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }

        SwingUtilities.invokeLater(() -> requestFocusInWindow());
        updateTippButtonStatus();
    }


    public void levelNeuStarten() {
        stopAllTimers();
        isPausiert = false;
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null && frame.getGlassPane() != null) {
            frame.getGlassPane().setVisible(false);
        }
        hauptFenster.starteLevel(level);
    }

    public void zumHauptmenue() {
        stopAllTimers();
        isPausiert = false;
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null && frame.getGlassPane() != null) {
            frame.getGlassPane().setVisible(false);
        }
        hauptFenster.zeigeLevelAuswahl();
    }


    /**
     * Wird aufgerufen, wenn der Spieler den "Header Scan"-Button klickt.
     */
    private void starteHeaderScan() {
        if (isPausiert || tippWurdeVerwendet || verbleibendeTipps <= 0 || isScanning) {
            return;
        }

        // --- TAKTISCHE PAUSE START ---
        isScanning = true;
        if (timer != null) timer.stop();
        if (clipTimerTick != null) clipTimerTick.stop();

        tippWurdeVerwendet = true;
        verbleibendeTipps--;

        updateTippButtonStatus();
        sicherButton.setEnabled(false);
        phishingButton.setEnabled(false);

        scanLineY = 0;
        scanAnimationTimer = new Timer(15, e -> {
            scanLineY += 10;

            if (scanLineY > emailContentWrapper.getHeight()) {
                // --- SCAN FERTIG ---
                scanAnimationTimer.stop();
                isScanning = false;

                sicherButton.setEnabled(true);
                phishingButton.setEnabled(true);
                updateTippButtonStatus();

                // ZEITSTRAFE JETZT ANWENDEN
                verbleibendeMillis -= (this.tippKostenSekunden * 1000L);
                if (verbleibendeMillis < 0) {
                    verbleibendeMillis = 0;
                }
                updateTimerDisplay();

                livesLogArea.append("HINWEIS: Scan genutzt.\n");
                livesLogArea.setCaretPosition(livesLogArea.getDocument().getLength());

                zeigeTipp();

                emailContentWrapper.repaint();

                // --- TAKTISCHE PAUSE ENDE ---
                // Timer erst neustarten, NACHDEM der Tipp angezeigt wurde
                if (timer != null) timer.start();

                // Sound ggf. neustarten
                int verbleibendeSekunden = (int) Math.ceil(verbleibendeMillis / 1000.0);
                if (verbleibendeSekunden <= 7 && verbleibendeSekunden > 0) {
                    if (clipTimerTick != null && !clipTimerTick.isRunning()) {
                        clipTimerTick.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }

            } else {
                emailContentWrapper.repaint();
            }
        });
        scanAnimationTimer.start();

        requestFocusInWindow();
    }

    /**
     * Aktualisiert den Text und den Status (aktiv/inaktiv) des Tipp-Buttons.
     */
    private void updateTippButtonStatus() {
        if (tippButton == null) return;

        if (isScanning) {
            tippButton.setText("🔬 SCANNEN...");
        } else {
            tippButton.setText("🔬 E-MAIL SCANNEN (" + verbleibendeTipps + ")");
        }

        if (isPausiert || tippWurdeVerwendet || verbleibendeTipps <= 0 || isScanning) {
            tippButton.setEnabled(false);
        } else {
            tippButton.setEnabled(true);
        }
    }

    /**
     * Aktualisiert die Timer-Anzeige (Text UND Leiste)
     */
    private void updateTimerDisplay() {
        int angezeigteSekunden = (int) Math.ceil(verbleibendeMillis / 1000.0);
        if (angezeigteSekunden < 0) angezeigteSekunden = 0;

        String text = "⏱️ " + angezeigteSekunden + "s";
        Color textColor;

        if (firewallAktiv) {
            text += " 🛡️";
            textColor = Theme.COLOR_ACCENT_BLUE;
        } else if (angezeigteSekunden <= 5) {
            textColor = Theme.COLOR_TIMER_LOW;
        } else if (angezeigteSekunden <= 10) {
            textColor = Theme.COLOR_TIMER_MEDIUM;
        } else {
            textColor = Theme.COLOR_TIMER_HIGH;
        }
        timerLabel.setText(text);
        timerLabel.setForeground(textColor);

        double percent = (double) verbleibendeMillis / maxMillisFuerEmail;
        timerBarPanel.updateSmooth(percent);
        timerBarPanel.updateTime(angezeigteSekunden, (int)(maxMillisFuerEmail/1000));
    }


    /**
     * Zeigt die Feedback-Karte an
     */
    private void zeigeFeedbackCard(FeedbackCard neueCard) {
        Container parent = feedbackCard.getParent();
        if (parent instanceof JPanel) {
            JPanel feedbackPanel = (JPanel) parent;
            feedbackPanel.removeAll();
            neueCard.setAlignmentX(Component.CENTER_ALIGNMENT);
            tippCard.setAlignmentX(Component.CENTER_ALIGNMENT);
            feedbackPanel.add(neueCard);
            feedbackPanel.add(tippCard);
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
        zeigeTipp(); // Zeigt Tipp bei Fehler

        // Timer-Dauer von zeigeTipp() (6s) muss länger sein als dieser (2.8s)
        Timer hideTimer = new Timer(2800, e -> feedbackCard.setVisible(false));
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void zeigeTipp() {
        Email aktuelleEmail = emails.get(aktuelleEmailIndex);
        String tipp = aktuelleEmail.getTipp();
        tippCard.showTipp(tipp);

        // ÄNDERUNG: Tipp bleibt 6 Sekunden statt 2.8 Sekunden
        Timer hideTimer = new Timer(6000, e -> tippCard.setVisible(false));
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

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
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private String erstelleLebenString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        String rotesHerz = "<font color='#E03030'>❤️</font>";
        String grauesHerz = "<font color='#444444'>🖤</font>";
        for (int i = 0; i < leben; i++) {
            sb.append(rotesHerz);
        }
        for (int i = leben; i < maxLeben; i++) {
            sb.append(grauesHerz);
        }
        sb.append("</html>");
        return sb.toString();
    }
}