package games.phishingdefender.managers;

import games.phishingdefender.ui.components.LevelConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Verwaltet den Level-Fortschritt (Sterne) und Tutorial-Status.
 * Speichert pro Spieler eine eigene Datei (async/thread-safe).
 *
 * @author yusef03
 * @version 2.0
 */
public class StarsManager {

    private final String spielerName;
    private final Map<Integer, Integer> levelStars;
    private boolean hatTutorialGelesen = false;

    public StarsManager(String spielerName) {
        this.spielerName = spielerName;
        this.levelStars = new HashMap<>();

        // Standardwerte initialisieren
        levelStars.put(1, 0);
        levelStars.put(2, 0);
        levelStars.put(3, 0);

        loadStars();
    }

    // Erzeugt Dateinamen basierend auf Spielernamen (bereinigt)
    private File getStarsFile() {
        String cleanName = spielerName.toLowerCase().replaceAll("[^a-z0-9]", "");
        return new File(LevelConfig.SAVE_DIR_PATH, "stars_" + cleanName + ".txt");
    }

    private void loadStars() {
        File file = getStarsFile();
        if (!file.exists()) return;

        synchronized (this) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        if (parts[0].startsWith("level")) {
                            int level = Integer.parseInt(parts[0].replace("level", ""));
                            int stars = Integer.parseInt(parts[1]);
                            levelStars.put(level, stars);
                        } else if (parts[0].equals("tutorial_gelesen")) {
                            hatTutorialGelesen = Boolean.parseBoolean(parts[1]);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Fehler beim Laden der Sterne für " + spielerName + ": " + e.getMessage());
            }
        }
    }

    // Speichert Daten im Hintergrund-Thread
    private void saveStarsAsync() {
        Map<Integer, Integer> starsCopy;
        boolean tutorialCopy;

        // Thread-Safe Kopie erstellen
        synchronized (this) {
            starsCopy = new HashMap<>(levelStars);
            tutorialCopy = hatTutorialGelesen;
        }

        new Thread(() -> {
            File dir = new File(LevelConfig.SAVE_DIR_PATH);
            if (!dir.exists() && !dir.mkdirs()) return;

            File file = getStarsFile();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

                for (Map.Entry<Integer, Integer> entry : starsCopy.entrySet()) {
                    writer.write("level" + entry.getKey() + "=" + entry.getValue());
                    writer.newLine();
                }
                writer.write("tutorial_gelesen=" + tutorialCopy);
                writer.newLine();

            } catch (Exception e) {
                System.err.println("Fehler beim Speichern (Async): " + e.getMessage());
            }
        }).start();
    }

    public synchronized int getStarsForLevel(int level) {
        return levelStars.getOrDefault(level, 0);
    }

    public void updateStars(int level, int newStars) {
        boolean changed = false;
        synchronized (this) {
            int currentStars = levelStars.getOrDefault(level, 0);
            if (newStars > currentStars) {
                levelStars.put(level, newStars);
                changed = true;
            }
        }
        if (changed) {
            saveStarsAsync();
        }
    }

    public synchronized int getGesamtSterne() {
        int total = 0;
        for (int stars : levelStars.values()) {
            total += stars;
        }
        return total;
    }

    public synchronized boolean hatTutorialGelesen() {
        return hatTutorialGelesen;
    }

    public void setTutorialGelesen() {
        synchronized (this) {
            this.hatTutorialGelesen = true;
        }
        saveStarsAsync();
    }

    // Statische Logik zur Sterne-Berechnung
    public static int berechneSterne(int richtigeAntworten, int gesamtEmails, int leben, int maxLeben) {
        double genauigkeit = (double) richtigeAntworten / gesamtEmails * 100;

        if (genauigkeit >= 90 && leben >= Math.max(2, maxLeben - 1)) return 3;
        if (genauigkeit >= 70 && leben >= 1) return 2;
        if (leben > 0) return 1;
        return 0;
    }

    // Admin-Funktion: Alle Spielstände löschen
    public static boolean adminResetAllStars() {
        try {
            File saveDir = new File(LevelConfig.SAVE_DIR_PATH);
            if (!saveDir.exists() || !saveDir.isDirectory()) return true;

            File[] files = saveDir.listFiles((dir, name) -> name.startsWith("stars_") && name.endsWith(".txt"));
            if (files == null) return true;

            boolean allDeleted = true;
            for (File file : files) {
                if (!file.delete()) allDeleted = false;
            }
            return allDeleted;
        } catch (Exception e) {
            System.err.println("Admin Reset Fehler (Stars): " + e.getMessage());
            return false;
        }
    }
}