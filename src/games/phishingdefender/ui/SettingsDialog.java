package games.phishingdefender.ui;

import games.phishingdefender.ui.components.Theme;
import games.phishingdefender.managers.HighscoreManager;
import games.phishingdefender.managers.MusicManager;
import games.phishingdefender.managers.StarsManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Ein modaler Dialog zum Anpassen der Spieleinstellungen.
 * Erlaubt das Stummschalten der Musik und das Einstellen der Lautstärke
 * über einen Slider. Die Einstellungen werden statisch gespeichert.
 *
 * @author yusef03
 * @version 1.0
 */

public class SettingsDialog extends JDialog {

    private static boolean musicMuted = false;
    private static int musicVolume = 30;  // 0-100

    public SettingsDialog(JFrame parent) {
        super(parent, "⚙️ Einstellungen", true);
        // setSize(450, 300); // <-- 1. Diese Zeile LÖSCHEN oder auskommentieren
        setLocationRelativeTo(parent);
        setResizable(false);

        setupUI(); // Zuerst die UI bauen lassen...

        pack(); // <-- 2. DIESE ZEILE HINZUFÜGEN (passt das Fenster an den Inhalt an)
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Theme.COLOR_BACKGROUND_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Titel
        JLabel titleLabel = new JLabel("🎮 EINSTELLUNGEN");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Settings Panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(Theme.COLOR_PANEL_DARK);
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GREEN, 1), // <-- NEU
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // === MUSIC ON/OFF ===
        JPanel musicPanel = new JPanel(new BorderLayout(10, 0));
        musicPanel.setOpaque(false);
        musicPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel musicLabel = new JLabel("🎵 Hintergrundmusik:");
        musicLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        musicLabel.setForeground(Color.WHITE);

        JCheckBox musicCheckbox = new JCheckBox("An");
        musicCheckbox.setSelected(!musicMuted);
        musicCheckbox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        musicCheckbox.setForeground(Color.WHITE);
        musicCheckbox.setOpaque(false);
        musicCheckbox.setFocusPainted(false);
        musicCheckbox.addActionListener(e -> {
            musicMuted = !musicCheckbox.isSelected();
            if (musicMuted) {
                MusicManager.stopMenuMusic();
            } else {
                MusicManager.startMenuMusic();
            }
        });

        musicPanel.add(musicLabel, BorderLayout.WEST);
        musicPanel.add(musicCheckbox, BorderLayout.EAST);

        // === VOLUME SLIDER ===
        JPanel volumePanel = new JPanel();
        volumePanel.setLayout(new BoxLayout(volumePanel, BoxLayout.Y_AXIS));
        volumePanel.setOpaque(false);

        JLabel volumeLabel = new JLabel("🔊 Lautstärke: " + musicVolume + "%");
        volumeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        volumeLabel.setForeground(Color.WHITE);
        volumeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSlider volumeSlider = new JSlider(0, 100, musicVolume);
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(Theme.COLOR_ACCENT_GREEN);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        volumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                musicVolume = volumeSlider.getValue();
                volumeLabel.setText("🔊 Lautstärke: " + musicVolume + "%");
                // Lautstärke SOFORT anpassen!
                MusicManager.updateVolume();
            }
        });

        volumePanel.add(volumeLabel);
        volumePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        volumePanel.add(volumeSlider);

        settingsPanel.add(musicPanel);
        settingsPanel.add(volumePanel);

// === NEUER "GEHEIMER" RESET BUTTON ===

        // Ein leeres Panel, das als Abstandshalter dient
        settingsPanel.add(Box.createVerticalStrut(20));

        // Ein Panel, das den Button nach rechts schiebt
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        adminPanel.setOpaque(false);
        adminPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Wichtig für BoxLayout

        JButton secretResetButton = new JButton("●"); // Ein einfacher Punkt
        secretResetButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        secretResetButton.setBackground(new Color(80, 30, 30)); // Sehr dunkles Rot
        secretResetButton.setForeground(new Color(180, 50, 50)); // Dunkelroter Punkt
        secretResetButton.setFocusPainted(false);
        secretResetButton.setPreferredSize(new Dimension(45, 30)); // Klein
        secretResetButton.setToolTipText("Admin-Funktion"); // Nur als Maus-Hover

        // Ruft die Passwort-Methode auf!
        secretResetButton.addActionListener(e -> adminResetAusfuehren());

        adminPanel.add(secretResetButton);
        settingsPanel.add(adminPanel);
        // === ENDE GEHEIMER BUTTON ===

        // Close Button
        JButton closeButton = Theme.createStyledButton(
                "✓ SCHLIESSEN",
                Theme.FONT_BUTTON_SMALL, // 16px
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_MEDIUM // 12px padding
        );
        closeButton.setPreferredSize(new Dimension(150, 45)); // Deine alte Größe
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.add(closeButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Zeigt eine Passwortabfrage und führt DANN den Admin-Reset durch.
     */
    private void adminResetAusfuehren() {

        // === DAS PASSWORT ===
        final String ADMIN_PASSWORT = "123321";

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel("Bitte Admin-Passwort eingeben:");
        JPasswordField passField = new JPasswordField(20); // Das ist das Feld mit '****'

        SwingUtilities.invokeLater(() -> passField.requestFocusInWindow());

        panel.add(label, BorderLayout.NORTH);
        panel.add(passField, BorderLayout.CENTER);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "🔒 Admin-Login",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );


        // 3. Prüfe die Eingabe
        if (option != JOptionPane.OK_OPTION) {
            return; // Benutzer hat "Abbrechen" geklickt
        }

        // 4. Passwort auslesen und prüfen
        //holen das Passwort als char[] und wandeln es in einen String um
        String eingabe = new String(passField.getPassword());

        if (!eingabe.equals(ADMIN_PASSWORT)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Passwort FALSCH. Zugriff verweigert.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
            return; // Falsches Passwort
        }

        // Schritt 5: Warnung anzeigen (Passwort war korrekt)
        int antwort = JOptionPane.showConfirmDialog(
                this,
                "Passwort korrekt.\n\n" +
                        "ACHTUNG! Bist du sicher, dass du ALLE Highscores und ALLE Sterne-Speicherstände löschen willst?\n" +
                        "Dies kann nicht rückgängig gemacht werden.",
                "🔥 Admin Reset Bestätigung 🔥",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (antwort != JOptionPane.YES_OPTION) {
            return; // Abbruch
        }

        // Schritt 6: Reset durchführen
        boolean highscoreOK = HighscoreManager.adminResetHighscores();
        boolean starsOK = StarsManager.adminResetAllStars();

        // Schritt 7: Feedback geben
        if (highscoreOK && starsOK) {
            JOptionPane.showMessageDialog(
                    this,
                    "Alle Daten wurden erfolgreich zurückgesetzt.\n" +
                            "Bitte starte das Spiel neu, damit die Änderungen wirksam werden.",
                    "Reset erfolgreich",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Ein Fehler ist aufgetreten.\n\nHighscores gelöscht: " + highscoreOK +
                            "\nSterne gelöscht: " + starsOK +
                            "\n\nBitte prüfe die Konsole.",
                    "Reset fehlgeschlagen",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static boolean isMusicMuted() {
        return musicMuted;
    }

    public static int getMusicVolume() {
        return musicVolume;
    }
}