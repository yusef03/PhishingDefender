package games.phishingdefender.ui;

import games.phishingdefender.ui.components.Theme;
import games.phishingdefender.managers.MusicManager;
import games.phishingdefender.managers.SettingsManager;
import games.phishingdefender.managers.HighscoreManager;
import games.phishingdefender.managers.StarsManager;

import javax.swing.*;
import java.awt.*;

/**
 * Modaler Einstellungs-Dialog.
 * Erlaubt Anpassung von Audio und Admin-Reset.
 *
 * @author yusef03
 * @version 2.0
 */
public class SettingsDialog extends JDialog {

    private final SettingsManager settingsManager;

    public SettingsDialog(JFrame parent, SettingsManager manager) {
        super(parent, "⚙️ Einstellungen", true);
        this.settingsManager = manager;
        setLocationRelativeTo(parent);
        setResizable(false);
        setupUI();
        pack();
    }

    private void setupUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.COLOR_BACKGROUND_DARK);
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("🎮 EINSTELLUNGEN", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Theme.COLOR_ACCENT_GREEN);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.COLOR_PANEL_DARK);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GREEN, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Musik Checkbox
        JPanel music = new JPanel(new BorderLayout());
        music.setOpaque(false);
        JCheckBox cbMute = new JCheckBox("An", !settingsManager.isMusicMuted());
        cbMute.setForeground(Color.WHITE);
        cbMute.setOpaque(false);
        cbMute.addActionListener(e -> {
            settingsManager.setMusicMuted(!cbMute.isSelected());
            if (settingsManager.isMusicMuted()) MusicManager.stopMenuMusic();
            else MusicManager.startMenuMusic(settingsManager);
        });
        JLabel lblMusic = new JLabel("🎵 Musik:");
        lblMusic.setForeground(Color.WHITE);
        music.add(lblMusic, BorderLayout.WEST);
        music.add(cbMute, BorderLayout.EAST);

        // Volume Slider
        JLabel lblVol = new JLabel("🔊 Lautstärke: " + settingsManager.getMusicVolume() + "%");
        lblVol.setForeground(Color.WHITE);

        JSlider slider = new JSlider(0, 100, settingsManager.getMusicVolume());
        slider.setOpaque(false);
        slider.addChangeListener(e -> {
            settingsManager.setMusicVolume(slider.getValue());
            lblVol.setText("🔊 Lautstärke: " + slider.getValue() + "%");
            MusicManager.updateVolume(settingsManager);
        });

        content.add(music);
        content.add(Box.createVerticalStrut(15));
        content.add(lblVol);
        content.add(slider);
        content.add(Box.createVerticalStrut(20));

        // Admin Button (versteckt)
        JPanel admin = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        admin.setOpaque(false);
        admin.setAlignmentX(LEFT_ALIGNMENT);
        JButton btnReset = new JButton("●");
        btnReset.setBackground(new Color(80, 30, 30));
        btnReset.setForeground(new Color(180, 50, 50));
        btnReset.setPreferredSize(new Dimension(45, 30));
        btnReset.addActionListener(e -> doAdminReset());
        admin.add(btnReset);
        content.add(admin);

        // Close
        JButton btnClose = Theme.createStyledButton("SCHLIESSEN", Theme.FONT_BUTTON_SMALL, Theme.COLOR_BUTTON_GREY, Theme.COLOR_BUTTON_GREY_HOVER, Theme.PADDING_BUTTON_MEDIUM);
        btnClose.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(btnClose);

        main.add(title, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);
        add(main);
    }

    private void doAdminReset() {
        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this, pf, "Admin Passwort:", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION && new String(pf.getPassword()).equals("123321")) {
            if (HighscoreManager.adminResetHighscores() && StarsManager.adminResetAllStars()) {
                JOptionPane.showMessageDialog(this, "Daten gelöscht.");
            }
        }
    }
}