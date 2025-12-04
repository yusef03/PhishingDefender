package games.phishingdefender.managers;

import games.phishingdefender.ui.components.LevelConfig;
import java.io.*;
import java.util.Properties;

/**
 * Verwaltet globale Einstellungen (Lautstärke, Stummschaltung).
 * Speichert Konfiguration dauerhaft in 'settings.properties'.
 *
 * @author yusef03
 * @version 2.0
 */
public class SettingsManager {

    private static final String FILE_NAME = "settings.properties";

    private boolean musicMuted = false;
    private int musicVolume = 30;

    public SettingsManager() {
        loadSettings();
    }

    // --- Getter & Setter mit Auto-Save ---

    public boolean isMusicMuted() {
        return musicMuted;
    }

    public void setMusicMuted(boolean musicMuted) {
        this.musicMuted = musicMuted;
        saveSettings();
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
        saveSettings();
    }

    // --- Datei-Operationen ---

    private void loadSettings() {
        File file = new File(LevelConfig.SAVE_DIR_PATH, FILE_NAME);
        if (!file.exists()) return;

        try (FileInputStream in = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(in);

            this.musicMuted = Boolean.parseBoolean(props.getProperty("musicMuted", "false"));
            String volStr = props.getProperty("musicVolume", "30");

            // Wert begrenzen (0-100)
            this.musicVolume = Math.max(0, Math.min(100, Integer.parseInt(volStr)));

        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Settings: " + e.getMessage());
        }
    }

    private void saveSettings() {
        // Verzeichnis erstellen falls nötig
        File dir = new File(LevelConfig.SAVE_DIR_PATH);
        if (!dir.exists() && !dir.mkdirs()) return;

        File file = new File(dir, FILE_NAME);

        try (FileOutputStream out = new FileOutputStream(file)) {
            Properties props = new Properties();
            props.setProperty("musicMuted", String.valueOf(musicMuted));
            props.setProperty("musicVolume", String.valueOf(musicVolume));

            props.store(out, "Phishing Defender Config");
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Settings: " + e.getMessage());
        }
    }
}
