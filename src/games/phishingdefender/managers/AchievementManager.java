package games.phishingdefender.managers;

import games.phishingdefender.ui.components.LevelConfig;
import games.phishingdefender.data.Achievement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verwaltet alle Erfolge (Achievements).
 * - Initialisierung der Liste
 * - Asynchrones Speichern & Laden (Thread-Safe)
 * - Prüft Freischalt-Bedingungen
 *
 * @author yusef03
 * @version 2.0
 */
public class AchievementManager {

    private final String spielerName;
    private final Map<String, Achievement> achievements;

    public AchievementManager(String spielerName) {
        this.spielerName = spielerName;
        this.achievements = new LinkedHashMap<>(); // Behält die Reihenfolge bei

        initializeAchievements();
        loadAchievements();
    }

    // Definiert alle verfügbaren Erfolge
    private void initializeAchievements() {
        // Level-Fortschritt
        addAchievement("L1_COMPLETE", "Anfänger-Detektiv", "Schließe Level 1 ab.", "icon_trophy.png");
        addAchievement("L1_PERFECT", "Perfekter Anfänger", "Erreiche 3 Sterne in Level 1.", "icon_star_filled.png");

        addAchievement("L2_COMPLETE", "Fortgeschritten", "Schließe Level 2 ab.", "icon_trophy.png");
        addAchievement("L2_PERFECT", "Perfekt Fortgeschritten", "Erreiche 3 Sterne in Level 2.", "icon_star_filled.png");

        addAchievement("L3_COMPLETE", "Experte", "Schließe Level 3 ab.", "icon_trophy.png");
        addAchievement("L3_PERFECT", "Meister-Detektiv", "Erreiche 3 Sterne in Level 3.", "icon_star_filled.png");

        // Gameplay & Extras
        addAchievement("FIRST_CATCH", "Erster Fang!", "Erkenne deine erste Phishing-Mail.", "icon_fishing.png");
        addAchievement("FIREWALL", "Brandheiß!", "Aktiviere den Firewall-Bonus.", "icon_fire.png");
        addAchievement("STREAK_10", "Adlerauge", "Erkenne 10 E-Mails in Folge korrekt.", "icon_brain.png");

        // Makellos-Challenges
        addAchievement("NO_MISTAKES_L1", "Makellos (Level 1)", "Level 1 ohne Fehler abschließen.", "icon_shield.png");
        addAchievement("NO_MISTAKES_L2", "Makellos (Level 2)", "Level 2 ohne Fehler abschließen.", "icon_shield.png");
        addAchievement("NO_MISTAKES_L3", "Makellos (Level 3)", "Level 3 ohne Fehler abschließen.", "icon_shield.png");

        addAchievement("CYBER_LEGEND", "Cyber-Legende", "3 Sterne in ALLEN Levels erreichen.", "icon_crown.png");
    }

    private void addAchievement(String id, String name, String desc, String icon) {
        achievements.put(id, new Achievement(id, name, desc, icon));
    }

    // Dateipfad basierend auf Spielernamen
    private File getAchievementsFile() {
        String cleanName = spielerName.toLowerCase().replaceAll("[^a-z0-9]", "");
        return new File(LevelConfig.SAVE_DIR_PATH, "achievements_" + cleanName + ".txt");
    }

    // Lädt gespeicherte Erfolge beim Start
    private void loadAchievements() {
        File file = getAchievementsFile();
        if (!file.exists()) return;

        synchronized (this) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String id = line.trim();
                    Achievement ach = achievements.get(id);
                    if (ach != null) {
                        ach.setUnlocked(true);
                    }
                }
            } catch (Exception e) {
                System.err.println("Fehler beim Laden der Achievements: " + e.getMessage());
            }
        }
    }

    // Speichert Fortschritt im Hintergrund (verhindert Ruckler)
    private void saveAchievementsAsync() {
        List<String> unlockedIds = new ArrayList<>();

        synchronized (this) {
            for (Achievement ach : achievements.values()) {
                if (ach.isUnlocked()) {
                    unlockedIds.add(ach.getId());
                }
            }
        }

        new Thread(() -> {
            File dir = new File(LevelConfig.SAVE_DIR_PATH);
            if (!dir.exists() && !dir.mkdirs()) return;

            File file = getAchievementsFile();
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

                for (String id : unlockedIds) {
                    writer.write(id);
                    writer.newLine();
                }
            } catch (Exception e) {
                System.err.println("Fehler beim Speichern der Achievements: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Versucht, einen Erfolg freizuschalten.
     * @return true, wenn NEU freigeschaltet (für Popup-Anzeige).
     */
    public boolean unlockAchievement(String id) {
        synchronized (this) {
            Achievement ach = achievements.get(id);
            if (ach == null || ach.isUnlocked()) {
                return false;
            }
            ach.setUnlocked(true);
        }

        saveAchievementsAsync(); // Sofort speichern
        return true;
    }

    public synchronized List<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements.values());
    }

    public synchronized boolean isUnlocked(String id) {
        Achievement ach = achievements.get(id);
        return ach != null && ach.isUnlocked();
    }
}