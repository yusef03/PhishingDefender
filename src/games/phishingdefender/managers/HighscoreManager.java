package games.phishingdefender.managers;

import games.phishingdefender.ui.components.LevelConfig;
import games.phishingdefender.data.HighscoreEntry;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Verwaltet das Lesen, Schreiben und Sortieren der Highscores.
 * Speichert die Einträge in einer Textdatei im Benutzerverzeichnis.
 * Stellt Methoden zum Hinzufügen neuer Scores und Abrufen der Top-Liste bereit.
 *
 * @author yusef03
 * @version 1.0
 */

public class HighscoreManager {

    // Speicherpfade werden aus LevelConfig geholt
    private static final String SAVE_DIR_PATH = LevelConfig.SAVE_DIR_PATH;
    private static final String DATEI_NAME = SAVE_DIR_PATH + java.io.File.separator + "highscores.txt";

    private List<HighscoreEntry> highscores;
    public HighscoreManager() {
        highscores = new ArrayList<>();
        laden();
    }

    // Lädt Highscores aus Datei
    private void laden() {
        File dataDir = new File(SAVE_DIR_PATH);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File datei = new File(DATEI_NAME);
        if (!datei.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(datei))) {
            String zeile;
            while ((zeile = reader.readLine()) != null) {
                String[] teile = zeile.split(",");

                if (teile.length == 5) {
                    String name = teile[0];
                    int punkte = Integer.parseInt(teile[1]);
                    int genauigkeit = Integer.parseInt(teile[2]);
                    int level = Integer.parseInt(teile[3]);
                    String datum = teile[4];

                    highscores.add(new HighscoreEntry(name, punkte, genauigkeit, level, datum));
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Highscores: " + e.getMessage());
        }

        sortieren();
    }

    // Speichert Highscores in Datei
    private void speichern() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATEI_NAME))) {
            for (HighscoreEntry entry : highscores) {
                writer.write(entry.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern der Highscores: " + e.getMessage());
        }
    }

    // Sortiert nach Punkte (höchste zuerst)
    private void sortieren() {
        Collections.sort(highscores, new Comparator<HighscoreEntry>() {
            public int compare(HighscoreEntry a, HighscoreEntry b) {
                return b.getPunkte() - a.getPunkte();  // b - a = absteigend
            }
        });
    }

    // Fügt neuen Highscore hinzu
    public void hinzufuegen(String name, int punkte, int genauigkeit, int level) {
        // Aktuelles Datum
        LocalDateTime jetzt = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String datum = jetzt.format(formatter);

        HighscoreEntry neuerEntry = new HighscoreEntry(name, punkte, genauigkeit, level, datum);
        highscores.add(neuerEntry);

        sortieren();
        speichern();
    }

    // Gibt Top 10 zurück
    public List<HighscoreEntry> getTop10() {
        List<HighscoreEntry> top10 = new ArrayList<>();

        for (int i = 0; i < Math.min(10, highscores.size()); i++) {
            top10.add(highscores.get(i));
        }

        return top10;
    }

    // Gibt Platzierung für einen Score zurück
    public int getPlatzierung(int punkte) {
        int platzierung = 1;

        for (HighscoreEntry entry : highscores) {
            if (entry.getPunkte() > punkte) {
                platzierung++;
            }
        }

        return platzierung;
    }

    public int getAnzahl() {
        return highscores.size();
    }

    /**
     * ADMIN-FUNKTION: Löscht die Highscore-Datei.
     * @return true, wenn erfolgreich (oder wenn Datei nicht existierte), false bei Fehler.
     */
    public static boolean adminResetHighscores() {
        try {
            File datei = new File(DATEI_NAME);
            if (datei.exists()) {
                return datei.delete(); // Versucht zu löschen
            }
            return true;
        } catch (Exception e) {
            System.err.println("ADMIN RESET FEHLER (Highscore): " + e.getMessage());
            return false;
        }
    }
}