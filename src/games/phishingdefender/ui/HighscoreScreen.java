package games.phishingdefender.ui;

import games.phishingdefender.ui.components.AnimatedBackgroundPanel;
import games.phishingdefender.PhishingDefender;
import games.phishingdefender.ui.components.Theme;
import games.phishingdefender.data.HighscoreEntry;
import games.phishingdefender.managers.HighscoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Highscore-Bildschirm.
 * Lädt die Top-10-Liste und hebt den aktuellen Spieler hervor.
 *
 * @author yusef03
 * @version 2.0
 */
public class HighscoreScreen extends JPanel {

    private final PhishingDefender hauptFenster;
    private final HighscoreManager manager;
    private final String currentPlayer;

    private static final Color COLOR_HIGHLIGHT = new Color(100, 220, 255);
    private static final Color COLOR_SEPARATOR = new Color(70, 70, 80);

    public HighscoreScreen(PhishingDefender hauptFenster) {
        this.hauptFenster = hauptFenster;
        this.currentPlayer = hauptFenster.getSpielerName();
        this.manager = new HighscoreManager();

        setLayout(new BorderLayout());
        setupUI();
    }

    private void setupUI() {
        AnimatedBackgroundPanel background = new AnimatedBackgroundPanel();
        background.setLayout(new BorderLayout());
        background.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        // 1. Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(35, 50, 20, 50));

        JLabel title = new JLabel("HIGHSCORES", JLabel.CENTER);
        title.setIcon(Theme.loadIcon("icon_trophy.png", 50));
        title.setFont(new Font("SansSerif", Font.BOLD, 50));
        title.setForeground(new Color(0, 255, 255)); // Cyan
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(">>> ELITE CYBER DETECTIVES <<<", JLabel.CENTER);
        subtitle.setFont(new Font("Monospaced", Font.BOLD, 15));
        subtitle.setForeground(new Color(0, 255, 100)); // Neon Grün
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 8)));
        header.add(subtitle);

        background.add(header, BorderLayout.NORTH);

        // 2. Liste
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        List<HighscoreEntry> uniqueScores = getBestScorePerPlayer();

        if (uniqueScores.isEmpty()) {
            JLabel empty = new JLabel("Noch keine Einträge vorhanden.", JLabel.CENTER);
            empty.setFont(new Font("SansSerif", Font.ITALIC, 18));
            empty.setForeground(Theme.COLOR_TEXT_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(empty);
        } else {
            int limit = Math.min(10, uniqueScores.size());
            for (int i = 0; i < limit; i++) {
                HighscoreEntry entry = uniqueScores.get(i);
                boolean isDu = currentPlayer != null && entry.getName().equalsIgnoreCase(currentPlayer);
                listPanel.add(createEntryPanel(i + 1, entry, isDu));
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        background.add(scroll, BorderLayout.CENTER);

        // 3. Footer
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        // Stats anzeigen
        if (!uniqueScores.isEmpty()) {
            int rank = findPlayerRank(uniqueScores);
            String stats = uniqueScores.size() + " Spieler";
            if (rank > 0) stats += "  •  Dein Rang: #" + rank;

            JLabel statsLabel = new JLabel(stats, JLabel.CENTER);
            statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            statsLabel.setForeground(Theme.COLOR_TEXT_SECONDARY);
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            footer.add(statsLabel);
            footer.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JButton backBtn = Theme.createStyledButton("Zurück zum Menü", Theme.FONT_BUTTON_SMALL, Theme.COLOR_BUTTON_GREY, Theme.COLOR_BUTTON_GREY_HOVER, Theme.PADDING_BUTTON_MEDIUM);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> hauptFenster.zeigeWelcomeScreen());
        footer.add(backBtn);

        background.add(footer, BorderLayout.SOUTH);
        add(background, BorderLayout.CENTER);
    }

    private JPanel createEntryPanel(int rank, HighscoreEntry entry, boolean isDu) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_SEPARATOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Rang
        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        rankLabel.setForeground(rank <= 3 ? COLOR_HIGHLIGHT : Theme.COLOR_TEXT_SECONDARY);
        panel.add(rankLabel, BorderLayout.WEST);

        // Details
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        String name = entry.getName() + (isDu ? " (Du)" : "");
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(isDu ? COLOR_HIGHLIGHT : Theme.COLOR_TEXT_PRIMARY);

        JLabel infoLabel = new JLabel("Level " + entry.getLevel() + " • " + entry.getGenauigkeit() + "% Genauigkeit");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoLabel.setForeground(Theme.COLOR_TEXT_SECONDARY);

        center.add(nameLabel);
        center.add(Box.createRigidArea(new Dimension(0, 4)));
        center.add(infoLabel);
        panel.add(center, BorderLayout.CENTER);

        // Score
        JLabel scoreLabel = new JLabel(String.format("%,d Pkt.", entry.getPunkte()));
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        scoreLabel.setForeground(COLOR_HIGHLIGHT);
        panel.add(scoreLabel, BorderLayout.EAST);

        return panel;
    }

    // Filtert Duplikate (nur bester Score pro Name)
    private List<HighscoreEntry> getBestScorePerPlayer() {
        Map<String, HighscoreEntry> best = new HashMap<>();
        for (HighscoreEntry entry : manager.getTop10()) {
            String key = entry.getName().toLowerCase();
            if (!best.containsKey(key) || entry.getPunkte() > best.get(key).getPunkte()) {
                best.put(key, entry);
            }
        }
        return best.values().stream()
                .sorted((a, b) -> Integer.compare(b.getPunkte(), a.getPunkte()))
                .collect(Collectors.toList());
    }

    private int findPlayerRank(List<HighscoreEntry> scores) {
        if (currentPlayer == null || currentPlayer.isEmpty()) return 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).getName().equalsIgnoreCase(currentPlayer)) return i + 1;
        }
        return 0;
    }
}