package games.phishingdefender.ui;

import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.PhishingDefender;
import games.phishingdefender.ui.components.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Zeigt einen mehrseitigen Tutorial-Bildschirm, der die Grundlagen
 * von Phishing für Kinder erklärt.
 *
 * @author yusef03
 * @version 1.0
 */
public class TutorialScreen extends JPanel {

    private PhishingDefender hauptFenster;
    private int currentPage = 0;

    // UI-Komponenten,
    private JLabel titleLabel;
    private JLabel iconLabel;
    private JLabel contentArea;
    private JButton prevButton;
    private JButton nextButton;

    //  Inhalte für Tutorial-Seiten
    private static final String[] TITEL = {
            "Was ist Phishing? 🎣",
            "Tipp 1: Prüfe den Absender! 🕵️",
            "Tipp 2: Lass dich nicht stressen! ⏰",
            "Tipp 3: Vertraue keinen Links! 🔗"
    };

    private static final String[] ICONS = { "💡", "🕵️", "⏰", "🚫" };

    private static final String[] INHALTE = {
            // Seite 1: Was ist Phishing?
            "<html><body style='width: 350px; font-size: 15px;'>" +
                    "Stell dir vor, ein Betrüger <b>'angelt'</b> (englisch: fishing) nach deinen geheimen Daten, wie Passwörtern.<br><br>" +
                    "Er tarnt sich als jemand, dem du vertraust (z.B. deine Bank, ein Spiel oder ein Paketdienst). " +
                    "In diesem Spiel lernst du, diese fiesen Tricks zu erkennen!" +
                    "</body></html>",

            // Seite 2: Absender prüfen
            "<html><body style='width: 350px; font-size: 15px;'>" +
                    "Betrüger benutzen oft Adressen, die *fast* echt aussehen. Sie tauschen Buchstaben aus oder fügen Zahlen hinzu.<br><br>" +

                    "<div style='background-color: #1a3a1a; border: 1px solid #00DD78; padding: 10px; margin-bottom: 10px; text-align: center;'>" +
                    "<b>✅ ECHT:</b><br>" +
                    "<span style='font-family: Monospace; font-weight: bold; font-size: 18px; padding-top: 5px; color: white;'>" +
                    "service@bank.de</span></div>" +

                    "<div style='background-color: #3a1a1a; border: 1px solid #E03030; padding: 12px; text-align: center;'>" +
                    "<b>❌ FALSCH:</b><br>" +
                    "<span style='font-family: Monospace; font-weight: bold; font-size: 18px; padding-top: 5px; color: white;'>" +
                    "service@bank-onl<span style='color: #E03030; font-size: 22px;'>1</span>ne.de</span><br>" +
                    "<span style='font-size: 13px; color: #ccc;'>(mit einer '1'!)</span></div>" +
                    "</body></html>",

            // Seite 3: Stress
            "<html><body style='width: 350px; font-size: 15px;'>" +
                    "Phishing-Mails wollen, dass du in Panik gerätst. Sie sagen:<br><br>" +

                    "<div style='background-color: #3a1a1a; border: 1px solid #E03030; padding: 15px; text-align: center; font-family: Monospace; font-weight: bold; line-height: 1.5;'>" +
                    "'<span style='color: #E03030; font-size: 20px;'>SOFORT HANDELN!</span>'<br>" +
                    "'<span style='color: #E03030; font-size: 20px;'>KONTO GESPERRT!</span>'</div><br>" +

                    "Echte Firmen tun das (fast) nie. Atme tief durch und schau genau hin, bevor du klickst." +
                    "</body></html>",

            // Seite 4:
            "<html><body style='width: 350px; font-size: 15px;'>" +
                    "Fahre mit der Maus über einen Link (ohne zu klicken!). Sieht die Adresse komisch aus? Klicke nicht darauf!<br><br>" +
                    "<b>Beispiel:</b><br>" +

                    "<div style='background-color: #111; border: 1px solid #555; padding: 15px; text-align: center;'>" +
                    "Text sagt: <span style='color: #66aaff; text-decoration: underline; font-size: 16px;'>Klick hier zum Einloggen</span><br><br>" +
                    "Link zeigt: <div style='font-family: Monospace; font-size: 17px; color: #E03030; font-weight: bold; margin-top: 8px;'>" +
                    "www.komische-seite-xyz.ru</div>" +
                    "</div><br>" +

                    "Echte Banken bitten dich nie, auf einen Link in einer E-Mail zu klicken." +
                    "</body></html>"
    };

    public TutorialScreen(PhishingDefender hauptFenster) {
        this.hauptFenster = hauptFenster;
        setLayout(new BorderLayout());
        setupUI();
        updatePageContent(); // Erste Seite laden
    }

    private void setupUI() {
        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // === 1. TITEL ===
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel mainTitle = new JLabel(">>> LERN-MODUS <<<", JLabel.CENTER);
        mainTitle.setFont(new Font("Monospaced", Font.BOLD, 18));
        mainTitle.setForeground(Theme.COLOR_ACCENT_GREEN);
        mainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleLabel = new JLabel("Tutorial Titel", JLabel.CENTER); // Wird von updatePageContent gesetzt
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(mainTitle);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(titleLabel);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        // === 2. INHALTS-PANEL ===
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 25, 25, 200)); // Semi-transparent
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(0, 220, 120, 100)); // Grüner Rand
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Emoji-Icon
        iconLabel = new JLabel("💡");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 120));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(iconLabel, BorderLayout.NORTH);

        // Text-Inhalt (jetzt als JLabel für HTML)
        contentArea = new JLabel("Inhaltstext..."); // Wird von updatePageContent gesetzt
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentArea.setForeground(Theme.COLOR_TEXT_PRIMARY);
        contentArea.setOpaque(false);
        contentArea.setVerticalAlignment(JLabel.TOP); // WICHTIG: Text oben ausrichten
        contentArea.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(contentArea, BorderLayout.CENTER);

        backgroundPanel.add(contentPanel, BorderLayout.CENTER);

        // === 3. FOOTER (Navigation) ===
        JPanel footerPanel = new JPanel(new BorderLayout(20, 10));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        // Zurück zum Menü Button
        JButton backBtn = Theme.createStyledButton(
                "← Zurück zum Menü",
                Theme.FONT_BUTTON_SMALL,
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        backBtn.addActionListener(e -> hauptFenster.zeigeWelcomeScreen());

        // Seiten-Navigation
        prevButton = Theme.createStyledButton(
                "« Vorherige",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_BUTTON_NEUTRAL,
                Theme.COLOR_BUTTON_NEUTRAL_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        prevButton.addActionListener(e -> changePage(-1));

        nextButton = Theme.createStyledButton(
                "Nächste »",
                Theme.FONT_BUTTON_MEDIUM,
                Theme.COLOR_ACCENT_GREEN,
                Theme.COLOR_ACCENT_GREEN_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        nextButton.addActionListener(e -> changePage(1));

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        navigationPanel.setOpaque(false);
        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);

        footerPanel.add(backBtn, BorderLayout.WEST);
        footerPanel.add(navigationPanel, BorderLayout.CENTER);

        backgroundPanel.add(footerPanel, BorderLayout.SOUTH);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Wechselt die Tutorial-Seite.
     * @param direction +1 für nächste, -1 für vorherige
     */
    private void changePage(int direction) {
        currentPage += direction;

        // Grenzen prüfen
        if (currentPage < 0) {
            currentPage = 0;
        }
        if (currentPage >= TITEL.length) {
            // Letzte Seite erreicht

            hauptFenster.tutorialAbgeschlossen();

            return;
        }

        updatePageContent();
    }

    /**
     * Aktualisiert die UI-Komponenten mit dem Inhalt der aktuellen Seite.
     */
    private void updatePageContent() {
        titleLabel.setText(TITEL[currentPage]);
        iconLabel.setText(ICONS[currentPage]);
        contentArea.setText(INHALTE[currentPage]);



        // Buttons anpassen
        prevButton.setVisible(currentPage > 0); // "Zurück" nur sichtbar nach Seite 1

        if (currentPage == TITEL.length - 1) {
            // Letzte Seite
            nextButton.setText("Verstanden! (Beenden)");
        } else {
            nextButton.setText("Nächste »");
        }
    }
}