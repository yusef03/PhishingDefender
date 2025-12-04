package games.phishingdefender.managers;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Verwaltet die Hintergrundmusik via Java Sound API.
 * - Startet/Stoppt Musik basierend auf Einstellungen
 * - Dynamische Lautstärkeregelung (dB-Umrechnung)
 *
 * @author yusef03
 * @version 2.0
 */
public class MusicManager {

    private static Clip musicClip;
    private static boolean isPlaying = false;

    public static void startMenuMusic(SettingsManager settings) {
        // 1. Einstellungen prüfen (Mute)
        if (settings.isMusicMuted()) {
            stopMenuMusic();
            return;
        }

        // 2. Falls läuft: Nur Lautstärke aktualisieren
        if (isPlaying && musicClip != null && musicClip.isRunning()) {
            updateVolume(settings);
            return;
        }

        // 3. Neu starten
        try {
            URL musicURL = MusicManager.class.getResource("/games/phishingdefender/assets/sounds/menu_music.wav");
            if (musicURL == null) return;

            // Alten Clip schließen, falls vorhanden
            if (musicClip != null) {
                musicClip.close();
            }

            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicURL)) {
                musicClip = AudioSystem.getClip();
                musicClip.open(audioIn);
            }

            // Lautstärke setzen VOR dem Start
            updateVolume(settings);

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
            isPlaying = true;

        } catch (Exception e) {
            System.err.println("Audio Fehler: " + e.getMessage());
        }
    }

    public static void stopMenuMusic() {
        if (musicClip != null) {
            if (musicClip.isRunning()) {
                musicClip.stop();
            }
            musicClip.close(); // Ressourcen freigeben
            musicClip = null;
        }
        isPlaying = false;
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    public static void updateVolume(SettingsManager settings) {
        if (musicClip == null) return;

        try {
            FloatControl gainControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            float db = percentToDecibel(settings.getMusicVolume());
            gainControl.setValue(db);
        } catch (Exception e) {
        }
    }

    // Rechnet Prozent (0-100) in Dezibel um (-80 bis 0)
    private static float percentToDecibel(int percent) {
        if (percent <= 0) return -80.0f;
        if (percent >= 100) return 0.0f;
        return (float) (20.0 * Math.log10(percent / 100.0));
    }
}