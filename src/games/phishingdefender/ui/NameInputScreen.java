package games.phishingdefender.ui;

import games.phishingdefender.PhishingDefender;
import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.ui.components.Theme;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * Login-Bildschirm für die Spielernamen-Eingabe.
 *
 * @author yusef03
 * @version 2.0
 */
    public class NameInputScreen extends JPanel {

    private final PhishingDefender hauptFenster;
    private JTextField nameField;

    public NameInputScreen(PhishingDefender hauptFenster) {
        this.hauptFenster = hauptFenster;
        setLayout(new BorderLayout());
        setupUI();
    }

    private void setupUI() {
        AnimatedBackgroundPanel background = new AnimatedBackgroundPanel();
        background.setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 50, 20, 50);
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. Titel
        JLabel title = new JLabel("WILLKOMMEN, REKRUT!", JLabel.CENTER);
        title.setIcon(Theme.loadIcon("icon_detective.png", 42));
        title.setFont(new Font("SansSerif", Font.BOLD, 42));
        title.setForeground(Theme.COLOR_ACCENT_GREEN);
        content.add(title, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 50, 30, 50);
        JLabel subtitle = new JLabel("<html><center><span style='font-size: 18px; color: #CCCCCC;'>Wir brauchen deine Hilfe im Kampf gegen Cyber-Kriminelle!</span></center></html>");
        content.add(subtitle, gbc);

        // 2. Input Box
        JPanel inputBox = new JPanel();
        inputBox.setLayout(new BoxLayout(inputBox, BoxLayout.Y_AXIS));
        inputBox.setBackground(Theme.COLOR_PANEL_DARK);
        inputBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.COLOR_ACCENT_GREEN, 2),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        ));
        inputBox.setPreferredSize(new Dimension(600, 200));

        JLabel inputLabel = new JLabel("Gib deinen Detektiv-Code-Namen ein:");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        inputLabel.setForeground(Theme.COLOR_ACCENT_GREEN);
        inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField();
        nameField.setFont(new Font("Monospace", Font.BOLD, 22));
        nameField.setForeground(Color.WHITE);
        nameField.setBackground(new Color(10, 10, 10));
        nameField.setCaretColor(Theme.COLOR_ACCENT_GREEN);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setMaximumSize(new Dimension(500, 60));
        nameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) submitName();
            }
        });

        inputBox.add(inputLabel);
        inputBox.add(Box.createVerticalStrut(15));
        inputBox.add(nameField);
        inputBox.add(Box.createVerticalStrut(15));

        JLabel hint = new JLabel("Drücke ENTER oder klicke LOS!", JLabel.CENTER);
        hint.setForeground(Color.GRAY);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputBox.add(hint);

        gbc.gridy++;
        content.add(inputBox, gbc);

        // 3. Buttons
        JButton btnLos = Theme.createStyledButton(" LOS!", Theme.FONT_BUTTON_LARGE, Theme.COLOR_ACCENT_ORANGE, Theme.COLOR_ACCENT_ORANGE_HOVER, Theme.PADDING_BUTTON_LARGE);
        btnLos.setIcon(Theme.loadIcon("icon_play.png", 22));
        btnLos.setPreferredSize(new Dimension(200, 55));
        btnLos.addActionListener(e -> submitName());

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        content.add(btnLos, gbc);

        JButton btnBack = Theme.createStyledButton(" ZURÜCK", Theme.FONT_BUTTON_MEDIUM, Theme.COLOR_BUTTON_GREY, Theme.COLOR_BUTTON_GREY_HOVER, Theme.PADDING_BUTTON_MEDIUM);
        btnBack.setIcon(Theme.loadIcon("icon_back.png", 22));
        btnBack.setPreferredSize(new Dimension(180, 50));
        btnBack.addActionListener(e -> hauptFenster.zeigeWelcomeScreen());

        gbc.gridy++;
        content.add(btnBack, gbc);

        background.add(content, BorderLayout.CENTER);
        add(background, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> nameField.requestFocusInWindow());
    }

    private void submitName() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte gib einen Namen ein!", "Fehler", JOptionPane.WARNING_MESSAGE);
            return;
        }
        hauptFenster.setSpielerName(name);
        hauptFenster.zeigeWelcomeScreen();
    }
}