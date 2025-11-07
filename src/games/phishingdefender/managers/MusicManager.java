package games.phishingdefender.managers;

import games.phishingdefender.ui.SettingsDialog;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Kümmert sich um das Abspielen und Stoppen der Hintergrundmusik.
 * Spielt einen Track im Loop ab und erlaubt das Anpassen der Lautstärke
 * basierend auf den Einstellungen im SettingsDialog. Verwendet statische Methoden.
 *
 * @author yusef03
 * @version 1.0
 */

public class MusicManager {

    private static Clip musicClip;
    private static boolean isPlaying = false;

    // Startet Background Music (Loop)
    public static void startMenuMusic() {
        if (isPlaying) return;

        try {
            java.net.URL musicURL = MusicManager.class.getResource("/games/phishingdefender/assets/sounds/menu_music.wav");

            if (musicURL == null) {
                System.out.println("Menu Music nicht gefunden!");
                return;
            }

            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicURL)) {
                musicClip = AudioSystem.getClip();
                musicClip.open(audioIn);
            }

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);

            // Lautstärke setzen
            updateVolume();

            musicClip.start();
            isPlaying = true;

            System.out.println("Menu Music gestartet!");

        } catch (Exception e) {
            System.out.println("Fehler beim Abspielen der Menu Music: " + e.getMessage());
        }
    }

    public static void stopMenuMusic() {
        if (musicClip != null && isPlaying) {
            musicClip.stop();
            musicClip.close();
            isPlaying = false;
            System.out.println("Menu Music gestoppt!");
        }
    }

    public static boolean isPlaying() {
        return isPlaying;
    }

    // Konvertiert Prozent (0-100) zu Dezibel
    public static float percentToDecibel(int percent) {
        if (percent <= 0) {
            return -80.0f;  // Fast stumm
        }
        if (percent >= 100) {
            return 0.0f;  // Maximum
        }
        // Logarithmische Skalierung
        return (float) (20.0 * Math.log10(percent / 100.0));
    }

    public static void updateVolume() {
        if (musicClip != null) {
            try {
                FloatControl volume = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
                int currentVolume = SettingsDialog.getMusicVolume();
                float db = percentToDecibel(currentVolume);
                volume.setValue(db);
                System.out.println("Lautstärke gesetzt: " + currentVolume + "% (" + db + " dB)");
            } catch (Exception e) {
                System.out.println("Fehler beim Setzen der Lautstärke: " + e.getMessage());
            }
        }
    }
}