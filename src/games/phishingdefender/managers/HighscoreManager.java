package games.phishingdefender.managers;

import games.phishingdefender.data.HighscoreEntry;
import games.phishingdefender.ui.components.LevelConfig;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet die Highscore-Liste.
 * - Lädt/Speichert Daten in Textdatei
 * - Asynchrone Schreibvorgänge (Performance)
 * - Thread-Sichere Listenverwaltung
 *
 * @author yusef03
 * @version 2.0
 */
public class HighscoreManager {

    private static final String DATEI_NAME = "highscores.txt";

    private final List<HighscoreEntry> highscores;
    private final Object lock = new Object(); // Für Thread-Synchronisation

    public HighscoreManager() {
        this.highscores = new ArrayList<>();
        laden();
    }

    // Lädt Einträge beim Start (blockierend, da notwendig)
    private void laden() {
        File file = new File(LevelConfig.SAVE_DIR_PATH, DATEI_NAME);
        if (!file.exists()) return;

        synchronized (lock) {
            highscores.clear();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

                String zeile;
                while ((zeile = reader.readLine()) != null) {
                    String[] teile = zeile.split(",");
                    if (teile.length == 5) {
                        highscores.add(new HighscoreEntry(
                                teile[0],
                                Integer.parseInt(teile[1]),
                                Integer.parseInt(teile[2]),
                                Integer.parseInt(teile[3]),
                                teile[4]
                        ));
                    }
                }
                sortieren();

            } catch (Exception e) {
                System.err.println("Fehler beim Laden der Highscores: " + e.getMessage());
            }
        }
    }

    // Speichert Daten im Hintergrund-Thread
    private void speichernAsync() {
        List<HighscoreEntry> copy;

        // Kopie erstellen, um Konflikte zu vermeiden
        synchronized (lock) {
            copy = new ArrayList<>(highscores);
        }

        new Thread(() -> {
            File dir = new File(LevelConfig.SAVE_DIR_PATH);
            if (!dir.exists() && !dir.mkdirs()) return;

            File file = new File(dir, DATEI_NAME);
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

                for (HighscoreEntry entry : copy) {
                    writer.write(entry.toFileString());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Fehler beim Speichern der Highscores: " + e.getMessage());
            }
        }).start();
    }

    private void sortieren() {
        // Absteigend nach Punkten sortieren
        highscores.sort((a, b) -> Integer.compare(b.getPunkte(), a.getPunkte()));
    }

    // Öffentliche API
    public void hinzufuegen(String name, int punkte, int genauigkeit, int level) {
        String datum = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        HighscoreEntry neuerEntry = new HighscoreEntry(name, punkte, genauigkeit, level, datum);

        synchronized (lock) {
            highscores.add(neuerEntry);
            sortieren();
        }
        speichernAsync();
    }

    public List<HighscoreEntry> getTop10() {
        synchronized (lock) {
            int limit = Math.min(10, highscores.size());
            return new ArrayList<>(highscores.subList(0, limit));
        }
    }

    // Berechnet aktuellen Rang für Ergebnis-Screen
    public int getPlatzierung(int punkte) {
        synchronized (lock) {
            int platzierung = 1;
            for (HighscoreEntry entry : highscores) {
                if (entry.getPunkte() > punkte) {
                    platzierung++;
                }
            }
            return platzierung;
        }
    }

    // Admin-Funktion
    public static boolean adminResetHighscores() {
        try {
            File file = new File(LevelConfig.SAVE_DIR_PATH, DATEI_NAME);
            return !file.exists() || file.delete();
        } catch (Exception e) {
            System.err.println("Reset Fehler: " + e.getMessage());
            return false;
        }
    }
}