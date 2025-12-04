package games.phishingdefender.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import games.phishingdefender.ui.components.LevelConfig;
import games.phishingdefender.data.Email;

import javax.swing.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Verwaltet die E-Mail-Datenbank.
 * Lädt Inhalte beim Start aus der JSON-Datei und stellt
 * Methoden zum Abrufen zufälliger Level-Sets bereit.
 *
 * @author yusef03
 * @version 2.0
 */
public class EmailDatabase {

    private final Map<Integer, List<Email>> echtPools;
    private final Map<Integer, List<Email>> phishingPools;

    public EmailDatabase() {
        this.echtPools = new HashMap<>();
        this.phishingPools = new HashMap<>();

        // Listen für alle Level initialisieren
        for (int i = 1; i <= LevelConfig.MAX_LEVEL; i++) {
            echtPools.put(i, new ArrayList<>());
            phishingPools.put(i, new ArrayList<>());
        }

        ladeEmailsAusJson();
    }

    // Lädt JSON-Daten aus den Assets via Gson
    private void ladeEmailsAusJson() {
        String path = "/games/phishingdefender/assets/emails.json";

        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("Datei nicht gefunden: " + path);

            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type listType = new TypeToken<ArrayList<Email>>(){}.getType();
            List<Email> alleEmails = new Gson().fromJson(reader, listType);

            // Sortiere E-Mails in die entsprechenden Maps
            for (Email mail : alleEmails) {
                Map<Integer, List<Email>> targetMap = mail.istPhishing() ? phishingPools : echtPools;

                if (targetMap.containsKey(mail.getLevel())) {
                    targetMap.get(mail.getLevel()).add(mail);
                }
            }

        } catch (Exception e) {
            System.err.println("Kritischer Fehler beim Laden der DB: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Datenbank konnte nicht geladen werden.\n" + e.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // Erstellt eine zufällige Mischung für das Level
    public List<Email> getEmailsFuerLevel(int level) {
        int countEcht = 0;
        int countPhishing = 0;

        switch (level) {
            case 1:
                countEcht = LevelConfig.L1_ECHTE;
                countPhishing = LevelConfig.L1_PHISHING;
                break;
            case 2:
                countEcht = LevelConfig.L2_ECHTE;
                countPhishing = LevelConfig.L2_PHISHING;
                break;
            case 3:
                countEcht = LevelConfig.L3_ECHTE;
                countPhishing = LevelConfig.L3_PHISHING;
                break;
            default:
                return new ArrayList<>();
        }

        return mixEmails(
                echtPools.get(level),
                phishingPools.get(level),
                countEcht,
                countPhishing
        );
    }

    // Hilfsmethode zum Mischen der Listen
    private List<Email> mixEmails(List<Email> echt, List<Email> phishing, int nEcht, int nPhishing) {
        List<Email> result = new ArrayList<>();

        // Kopien erstellen, um Originale nicht zu verändern
        List<Email> copyEcht = new ArrayList<>(echt);
        List<Email> copyPhishing = new ArrayList<>(phishing);

        Collections.shuffle(copyEcht);
        Collections.shuffle(copyPhishing);

        // Elemente sicher hinzufügen (prüft Listenlänge)
        for (int i = 0; i < nEcht && i < copyEcht.size(); i++) {
            result.add(copyEcht.get(i));
        }
        for (int i = 0; i < nPhishing && i < copyPhishing.size(); i++) {
            result.add(copyPhishing.get(i));
        }

        Collections.shuffle(result);
        return result;
    }
}