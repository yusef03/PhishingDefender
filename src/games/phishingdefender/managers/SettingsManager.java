package games.phishingdefender.managers;

/**
 * Verwaltet die globalen Spieleinstellungen (z.B. Lautstärke).
 * Diese Klasse speichert die Daten (im Gegensatz zum SettingsDialog, der sie nur anzeigt).
 *
 * @author yusef03
 * @version 1.0
 */
public class SettingsManager {

    // Standardwerte
    private boolean musicMuted = false;
    private int musicVolume = 30;  // 0-100

    /**
     * Konstruktor.
     * (Hier könnte man später das Laden aus einer Datei einfügen)
     */
    public SettingsManager() {
        // Vorerst leer, wir nutzen die Standardwerte.
    }

    // --- GETTER und SETTER ---

    public boolean isMusicMuted() {
        return musicMuted;
    }

    public void setMusicMuted(boolean musicMuted) {
        this.musicMuted = musicMuted;
        // (Hier könnte man das Speichern in eine Datei auslösen)
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
        // (Hier könnte man das Speichern in eine Datei auslösen)
    }
}