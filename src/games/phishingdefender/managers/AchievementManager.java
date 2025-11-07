package games.phishingdefender.managers;

import games.phishingdefender.ui.components.LevelConfig;
import games.phishingdefender.data.Achievement;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verwaltet das Freischalten, Speichern und Laden von Achievements
 * für einen bestimmten Spieler.
 *
 * Nutzt eine Map, um alle Achievements zu speichern und lädt/speichert
 * den Freischalt-Status in einer spieler-spezifischen Datei
 * (z.B. achievements_yusef.txt).
 *
 * @author yusef03
 * @version 1.0
 */
public class AchievementManager {

    private String spielerName;
    private Map<String, Achievement> achievements;

    // Pfad aus LevelConfig (wie bei StarsManager)
    private static final String SAVE_DIR_PATH = LevelConfig.SAVE_DIR_PATH;

    public AchievementManager(String spielerName) {
        this.spielerName = spielerName;
        this.achievements = new LinkedHashMap<>();

        // 1. Definiere alle Achievements, die es im Spiel gibt
        initializeAchievements();

        // 2. Lade den Speicherstand (welche davon sind schon frei?)
        loadAchievements();
    }

    /**
     * Erstellt die "Master-Liste" aller Achievements im Spiel.
     * Alle fangen als "gesperrt" (locked) an.
     */
    private void initializeAchievements() {
        // ID, Name, Beschreibung, Icon

        // Level 1
        addAchievement("L1_COMPLETE", "Anfänger-Detektiv", "Schließe Level 1 ab.", "🏆");
        addAchievement("L1_PERFECT", "Perfekter Anfänger", "Erreiche 3 Sterne in Level 1.", "⭐⭐⭐");

        // Level 2
        addAchievement("L2_COMPLETE", "Fortgeschritten", "Schließe Level 2 ab.", "🏆");
        addAchievement("L2_PERFECT", "Perfekt Fortgeschritten", "Erreiche 3 Sterne in Level 2.", "⭐⭐⭐");

        // Level 3
        addAchievement("L3_COMPLETE", "Experte", "Schließe Level 3 ab.", "🏆");
        addAchievement("L3_PERFECT", "Meister-Detektiv", "Erreiche 3 Sterne in Level 3.", "⭐⭐⭐");

        // Gameplay
        addAchievement("FIRST_CATCH", "Erster Fang!", "Erkenne deine erste Phishing-Mail.", "🎣");
        addAchievement("FIREWALL", "Brandheiß!", "Aktiviere den Firewall-Bonus.", "🔥");
        addAchievement("STREAK_10", "Adlerauge", "Erkenne 10 E-Mails in Folge korrekt.", "🧠");
        addAchievement("NO_MISTAKES_L1", "Makellos (Level 1)", "Schließe Level 1 ab, ohne ein Leben zu verlieren.", "🛡️");
        addAchievement("NO_MISTAKES_L2", "Makellos (Level 2)", "Schließe Level 2 ab, ohne ein Leben zu verlieren.", "🛡️🛡️");
        addAchievement("NO_MISTAKES_L3", "Makellos (Level 3)", "Schließe Level 3 ab, ohne ein Leben zu verlieren.", "🛡️🛡️🛡️");
        addAchievement("CYBER_LEGEND", "Cyber-Legende", "Erreiche 3 Sterne in ALLEN Levels.", "👑");
    }

    /**
     * Eine private Helfermethode, um das Erstellen sauber zu halten.
     */
    private void addAchievement(String id, String name, String desc, String icon) {
        achievements.put(id, new Achievement(id, name, desc, icon));
    }

    /**
     * Gibt den Dateipfad für den Speicherstand dieses Spielers zurück.
     * z.B. /home/user/.phishingDefenderData/achievements_yusef.txt
     */
    private String getAchievementsFile() {
        String cleanName = spielerName.toLowerCase().replaceAll("[^a-z0-9]", "");

        File dataDir = new File(SAVE_DIR_PATH);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        return SAVE_DIR_PATH + File.separator + "achievements_" + cleanName + ".txt";
    }

    /**
     * Lädt die freigeschalteten Achievement-IDs aus der Speicherdatei.
     */
    private void loadAchievements() {
        File file = new File(getAchievementsFile());
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String id = line.trim();
                if (achievements.containsKey(id)) {
                    achievements.get(id).setUnlocked(true);
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Achievements für " + spielerName + ": " + e.getMessage());
        }
    }

    /**
     * Speichert ALLE freigeschalteten Achievement-IDs in die Datei.
     */
    private void saveAchievements() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getAchievementsFile()))) {

            //durch alle Achievements in Map gehen
            for (Achievement ach : achievements.values()) {
                if (ach.isUnlocked()) {
                    // Wenn es freigeschaltet ist, seine ID in die Datei schreiben !
                    writer.write(ach.getId());
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Speichern der Achievements für " + spielerName + ": " + e.getMessage());
        }
    }

    /**
     * Die Hauptmethode, die von außen aufgerufen wird (z.B. vom GameScreen).
     * Versucht, ein Achievement freizuschalten.
     *
     * @param id Die ID des Achievements (z.B. "FIRST_CATCH")
     * @return true, wenn das Achievement NEU freigeschaltet wurde (für Pop-up!),
     * false, wenn es schon freigeschaltet war.
     */
    public boolean unlockAchievement(String id) {
        if (!achievements.containsKey(id)) {
            System.err.println("Unbekannte Achievement-ID: " + id);
            return false;
        }

        Achievement ach = achievements.get(id);

        if (ach.isUnlocked()) {
            return false;
        }


        ach.setUnlocked(true);
        saveAchievements();

        System.out.println("ACHIEVEMENT FREIGESCHALTET: " + ach.getName());
        return true;
    }

    /**
     * Gibt eine Liste aller Achievements zurück (für den Achievement-Screen).
     */
    public List<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements.values());
    }

    /**
     * Prüft, ob ein Achievement bereits freigeschaltet ist.
     */
    public boolean isUnlocked(String id) {
        if (!achievements.containsKey(id)) {
            return false;
        }
        return achievements.get(id).isUnlocked();
    }
}