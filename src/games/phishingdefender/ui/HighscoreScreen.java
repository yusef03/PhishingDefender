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
 * Zeigt die Highscore-Liste an.
 * Lädt die Top-Scores über den HighscoreManager und stellt sie
 * in einer scrollbaren Liste dar. Hebt den aktuellen Spieler hervor.
  *
  * @author yusef03
  * @version 1.0
 */


public class HighscoreScreen extends JPanel {

    private PhishingDefender hauptFenster;
    private HighscoreManager manager;
    private String currentPlayer;

    private static final Color COLOR_BACKGROUND = new Color(25, 25, 30, 200); // Semi-transparentes Dunkelgrau
    private static final Color COLOR_TEXT_PRIMARY = new Color(230, 230, 230); // Fast Weiß
    private static final Color COLOR_TEXT_SECONDARY = new Color(160, 160, 160); // Helles Grau
    private static final Color COLOR_HIGHLIGHT = new Color(100, 220, 255); // Sanftes Blau-Highlight
    private static final Color COLOR_SEPARATOR = new Color(70, 70, 80); // Trennlinie
    private static final Color COLOR_PLAYER_HIGHLIGHT_BG = new Color(45, 65, 80, 200); // Hintergrund für Spieler

    private static final Color CYBER_CYAN = new Color(0, 255, 255);
    private static final Color CYBER_GREEN = new Color(0, 255, 100);

    public HighscoreScreen(PhishingDefender hauptFenster) {
        this.hauptFenster = hauptFenster;
        this.currentPlayer = hauptFenster.getSpielerName();
        this.manager = new HighscoreManager();

        setLayout(new BorderLayout());
        setupUI();
    }

    private void setupUI() {
        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60)); // Mehr Rand

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(35, 50, 20, 50));

        JLabel titleLabel = new JLabel("🏆 HIGHSCORES", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(CYBER_CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(">>> ELITE CYBER DETECTIVES <<<", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        subtitleLabel.setForeground(CYBER_GREEN);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(titleLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        topPanel.add(subtitleLabel);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        // === 3. LISTE DER SCORES ===
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false); // Transparent, damit man den Hintergrund sieht
        List<HighscoreEntry> uniqueScores = getBestScorePerPlayer();

        if (uniqueScores.isEmpty()) {
            JLabel emptyLabel = new JLabel("Noch keine Einträge vorhanden.", JLabel.CENTER);
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
            emptyLabel.setForeground(COLOR_TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLabel);
        } else {
            //für jeden Eintrag ein Panel
            for (int i = 0; i < Math.min(10, uniqueScores.size()); i++) {
                HighscoreEntry entry = uniqueScores.get(i);
                boolean isDu = currentPlayer != null &&
                        entry.getName().equalsIgnoreCase(currentPlayer);

                JPanel entryPanel = createElegantEntryPanel(i + 1, entry, isDu);
                listPanel.add(entryPanel);
            }
        }

        // ScrollPane, falls Liste lang wird
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);


        // === 4. FOOTER (mit Zurück-Button) ===
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        // Spieler-Statistik
        if (!uniqueScores.isEmpty()) {
            int playerCount = uniqueScores.size();
            int playerRank = findPlayerRank(uniqueScores);

            String statsText = playerCount + " Spieler";
            if (playerRank > 0) {
                statsText += "  •  Dein Rang: #" + playerRank;
            }

            JLabel statsLabel = new JLabel(statsText, JLabel.CENTER);
            statsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            statsLabel.setForeground(COLOR_TEXT_SECONDARY);
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            footerPanel.add(statsLabel);
            footerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JButton backBtn = Theme.createStyledButton(
                "Zurück zum Menü",
                Theme.FONT_BUTTON_SMALL,
                Theme.COLOR_BUTTON_GREY,
                Theme.COLOR_BUTTON_GREY_HOVER,
                Theme.PADDING_BUTTON_MEDIUM
        );
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        backBtn.addActionListener(e -> hauptFenster.zeigeWelcomeScreen());
        footerPanel.add(backBtn);

        backgroundPanel.add(footerPanel, BorderLayout.SOUTH);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Erstellt ein einzelnes, sauberes Panel für einen Highscore-Eintrag.
     * Ersetzt die alte "createEntryCard"-Methode.
     */

    private JPanel createElegantEntryPanel(int rank, HighscoreEntry entry, boolean isDu) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_SEPARATOR), //Trennlinie
                BorderFactory.createEmptyBorder(15, 20, 15, 20)  // Innenabstand
        ));
        // Verhindert, dass die Einträge in der Höhe wachsen
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));


        // LINKS: Rang
        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        rankLabel.setForeground(rank <= 3 ? COLOR_HIGHLIGHT : COLOR_TEXT_SECONDARY); // Top 3 hervorheben
        panel.add(rankLabel, BorderLayout.WEST);

        // MITTE: Name und Details
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        String displayName = entry.getName();
        if (isDu) displayName += " (Du)";

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(isDu ? COLOR_HIGHLIGHT : COLOR_TEXT_PRIMARY);

        JLabel detailsLabel = new JLabel(
                "Level " + entry.getLevel() + " • " + entry.getGenauigkeit() + "% Genauigkeit"
        );
        detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detailsLabel.setForeground(COLOR_TEXT_SECONDARY);

        centerPanel.add(nameLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        centerPanel.add(detailsLabel);
        panel.add(centerPanel, BorderLayout.CENTER);


        // RECHTS: Punkte
        JLabel scoreLabel = new JLabel(String.format("%,d Pkt.", entry.getPunkte())); // Formatierter Score
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        scoreLabel.setForeground(COLOR_HIGHLIGHT);
        panel.add(scoreLabel, BorderLayout.EAST);

        return panel;
    }


    /**
     * Filtert: Nur BESTER Score pro Spieler!
     */

    private List<HighscoreEntry> getBestScorePerPlayer() {
        Map<String, HighscoreEntry> best = new HashMap<>();

        for (HighscoreEntry entry : manager.getTop10()) {
            String key = entry.getName().toLowerCase();
            if (!best.containsKey(key) || entry.getPunkte() > best.get(key).getPunkte()) {
                best.put(key, entry);
            }
        }

        return best.values().stream()
                .sorted((a, b) -> b.getPunkte() - a.getPunkte())
                .collect(Collectors.toList());
    }

    /**
     * Findet den Rang des aktuellen Spielers.
     * (LOGIK UNVERÄNDERT)
     */

    private int findPlayerRank(List<HighscoreEntry> scores) {
        if (currentPlayer == null || currentPlayer.isEmpty()) return 0;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).getName().equalsIgnoreCase(currentPlayer)) {
                return i + 1;
            }
        }
        return 0;
    }
}