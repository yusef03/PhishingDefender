package games.phishingdefender.managers;

import games.phishingdefender.ui.components.LevelConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * Verwaltet die erreichten Sterne (0-3) für jedes Level, getrennt für jeden Spieler.
 * Lädt und speichert die Sterne in einer spieler-spezifischen Textdatei
 * im Benutzerverzeichnis. Enthält die Logik zur Berechnung der Sterne
 * basierend auf Genauigkeit und verbleibenden Leben.
 *
 * @author yusef03
 * @version 1.0
 */

public class StarsManager {

    private String spielerName;
    private Map<Integer, Integer> levelStars;
    private boolean hatTutorialGelesen = false;
    private static final String SAVE_DIR_PATH = LevelConfig.SAVE_DIR_PATH;

    public StarsManager(String spielerName) {
        this.spielerName = spielerName;
        this.levelStars = new HashMap<>();

        // Initialisiere mit 0 Sternen
        levelStars.put(1, 0);
        levelStars.put(2, 0);
        levelStars.put(3, 0);

        loadStars();
    }

    /**
     * Gibt Dateinamen für diesen Spieler zurück
     */
    private String getStarsFile() {
        String cleanName = spielerName.toLowerCase().replaceAll("[^a-z0-9]", "");

        File dataDir = new File(SAVE_DIR_PATH);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Gib den NEUEN Pfad zurück
        return SAVE_DIR_PATH + File.separator + "stars_" + cleanName + ".txt";
    }

    /**
     * Lädt Sterne aus Datei
     */
    private void loadStars() {
        File file = new File(getStarsFile());

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {

                    //prüfen, WAS wir da lesen

                    if (parts[0].startsWith("level")) {
                        // Es ist eine Level-Zeile
                        int level = Integer.parseInt(parts[0].replace("level", ""));
                        int stars = Integer.parseInt(parts[1]);
                        levelStars.put(level, stars);
                    } else if (parts[0].equals("tutorial_gelesen")) {
                        // Es ist die Tutorial-Zeile
                        hatTutorialGelesen = Boolean.parseBoolean(parts[1]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Sterne für " + spielerName + ": " + e.getMessage());
        }
    }

    /**
     * Speichert Sterne in Datei
     */
    private void saveStars() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getStarsFile()))) {
            // Speichere die Sterne (wie bisher)
            for (int level = 1; level <= 3; level++) {
                int stars = levelStars.getOrDefault(level, 0);
                writer.write("level" + level + "=" + stars);
                writer.newLine();
            }
            writer.write("tutorial_gelesen=" + hatTutorialGelesen);
            writer.newLine();

        } catch (Exception e) {
            System.out.println("Fehler beim Speichern der Sterne für " + spielerName + ": " + e.getMessage());
        }
    }

    /**
     * Gibt Sterne für ein Level zurück
     */
    public int getStarsForLevel(int level) {
        return levelStars.getOrDefault(level, 0);
    }

    /**
     * Aktualisiert Sterne (nur wenn besser!)
     */
    public void updateStars(int level, int newStars) {
        int currentStars = levelStars.getOrDefault(level, 0);

        if (newStars > currentStars) {
            levelStars.put(level, newStars);
            saveStars();
        }
    }

    /**
     * Berechnet Sterne basierend auf Performance
     */
    public static int berechneSterne(int richtigeAntworten, int gesamtEmails, int leben, int maxLeben) {
        // Genauigkeit berechnen
        double genauigkeit = (double) richtigeAntworten / gesamtEmails * 100;

        // 3 Sterne: ≥90% Genauigkeit UND mindestens 2 Leben übrig
        if (genauigkeit >= 90 && leben >= Math.max(2, maxLeben - 1)) {
            return 3;
        }

        // 2 Sterne: ≥70% Genauigkeit UND mindestens 1 Leben übrig
        if (genauigkeit >= 70 && leben >= 1) {
            return 2;
        }

        // 1 Stern: Level geschafft (egal wie)
        if (leben > 0) {
            return 1;
        }

        // 0 Sterne: Verloren
        return 0;
    }

    /**
     * Gibt Gesamt-Sterne zurück (für Statistik)
     */
    public int getGesamtSterne() {
        int total = 0;
        for (int stars : levelStars.values()) {
            total += stars;
        }
        return total;
    }
    /**
     * Prüft, ob der Spieler das Tutorial gelesen hat.
     */
    public boolean hatTutorialGelesen() {
        return hatTutorialGelesen;
    }

    /**
     * Markiert das Tutorial als gelesen und speichert sofort.
     */
    public void setTutorialGelesen() {
        this.hatTutorialGelesen = true;
        saveStars(); // Sofort speichern!
    }

    /**
     * ADMIN-FUNKTION: Löscht ALLE "stars_*.txt" Dateien im Speicherverzeichnis.
     * @return true, wenn alle erfolgreich gelöscht wurden, false bei Fehler.
     */
    public static boolean adminResetAllStars() {
        try {
            File saveDir = new File(SAVE_DIR_PATH);
            if (!saveDir.exists() || !saveDir.isDirectory()) {
                return true; // Ordner existiert nicht, also "erfolgreich"
            }

            boolean allDeleted = true;

            // Gehe durch alle Dateien im Ordner
            for (File file : saveDir.listFiles()) {
                // Wenn sie mit "stars_" anfängt UND mit ".txt" aufhört
                if (file.getName().startsWith("stars_") && file.getName().endsWith(".txt")) {
                    if (!file.delete()) {
                        allDeleted = false; // Eine konnte nicht gelöscht werden
                        System.err.println("ADMIN RESET FEHLER: Konnte " + file.getName() + " nicht löschen.");
                    }
                }
            }
            return allDeleted;

        } catch (Exception e) {
            System.err.println("ADMIN RESET FEHLER (Stars): " + e.getMessage());
            return false;
        }
    }

}