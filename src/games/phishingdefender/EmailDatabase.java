package games.phishingdefender;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Verwaltet die Sammlung aller E-Mails (echte und Phishing).
 * Lädt alle E-Mails dynamisch aus der 'emails.json'-Datei beim Start.
 * Stellt Methoden bereit, um eine zufällige, level-spezifische
 * Auswahl an E-Mails für das Spiel zu laden.
 *
 * @author yusef03
 * @version 2.0 (Refactored mit JSON)
 */
public class EmailDatabase {

    // Die Pools sind jetzt 'Maps', die Level-Nummern (Integer)
    // auf Listen von E-Mails (List<Email>) abbilden.
    private Map<Integer, List<Email>> echtPools;
    private Map<Integer, List<Email>> phishingPools;

    public EmailDatabase() {
        // 1. Initialisiere die Maps
        echtPools = new HashMap<>();
        phishingPools = new HashMap<>();

        for (int i = 1; i <= LevelConfig.MAX_LEVEL; i++) {
            echtPools.put(i, new ArrayList<>());
            phishingPools.put(i, new ArrayList<>());
        }

        // 2. Lade alle E-Mails aus der JSON-Datei
        ladeEmailsAusJson();
    }

    /**
     * Lädt alle E-Mails aus der 'emails.json' Datei, die sich in den
     * 'assets' befinden muss, und sortiert sie in die 'echtPools'
     * und 'phishingPools' Maps ein.
     */
    private void ladeEmailsAusJson() {
        // Der Pfad zur Ressource. Wichtig: Beginnt mit '/',
        // da er vom 'root' des Classpath (src-Ordner) ausgeht.
        String jsonFilePath = "/games/phishingdefender/assets/emails.json";

        try {
            // 1. JSON-Datei als Ressource-Stream laden
            InputStream inputStream = getClass().getResourceAsStream(jsonFilePath);
            if (inputStream == null) {
                throw new RuntimeException("Ressource nicht gefunden: " + jsonFilePath);
            }

            // 2. Stream in einen Reader umwandeln (mit UTF-8 für Emojis/Umlaute)
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            // 3. Gson-Bibliothek verwenden, um die JSON-Datei direkt
            //    in eine Liste von Email-Objekten zu parsen.
            Gson gson = new Gson();
            Type emailListType = new TypeToken<ArrayList<Email>>(){}.getType();
            List<Email> alleEmails = gson.fromJson(reader, emailListType);

            // 4. Alle E-Mails in die richtigen Pools sortieren
            for (Email email : alleEmails) {
                if (email.istPhishing()) {
                    if (phishingPools.containsKey(email.getLevel())) {
                        phishingPools.get(email.getLevel()).add(email);
                    }
                } else {
                    if (echtPools.containsKey(email.getLevel())) {
                        echtPools.get(email.getLevel()).add(email);
                    }
                }
            }

            // 5. Reader und Stream schließen
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            // Wenn das Laden fehlschlägt, kann das Spiel nicht starten.
            JOptionPane.showMessageDialog(null,
                    "Fehler beim Laden der E-Mail-Datenbank!\nDatei: " + jsonFilePath + "\nFehler: " + e.getMessage(),
                    "Kritischer Fehler",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Spiel beenden
        }
    }


    /**
     * Wählt eine zufällige Mischung aus echten und Phishing-E-Mails
     * für das angegebene Level aus. (Logik ist von LevelConfig gesteuert).
     */
    public List<Email> getEmailsFuerLevel(int level) {
        List<Email> ausgewaehlteEmails = new ArrayList<>();

        if (level == LevelConfig.L1_LEVEL_NUM) {
            ausgewaehlteEmails = waehleZufaelligeEmails(
                    echtPools.get(LevelConfig.L1_LEVEL_NUM),      // <-- Holt aus Map
                    phishingPools.get(LevelConfig.L1_LEVEL_NUM),  // <-- Holt aus Map
                    LevelConfig.L1_ECHTE,
                    LevelConfig.L1_PHISHING
            );
        } else if (level == LevelConfig.L2_LEVEL_NUM) {
            ausgewaehlteEmails = waehleZufaelligeEmails(
                    echtPools.get(LevelConfig.L2_LEVEL_NUM),
                    phishingPools.get(LevelConfig.L2_LEVEL_NUM),
                    LevelConfig.L2_ECHTE,
                    LevelConfig.L2_PHISHING
            );
        } else if (level == LevelConfig.L3_LEVEL_NUM) {
            ausgewaehlteEmails = waehleZufaelligeEmails(
                    echtPools.get(LevelConfig.L3_LEVEL_NUM),
                    phishingPools.get(LevelConfig.L3_LEVEL_NUM),
                    LevelConfig.L3_ECHTE,
                    LevelConfig.L3_PHISHING
            );
        }

        return ausgewaehlteEmails;
    }

    /**
     * Wählt zufällig X echte und Y phishing E-Mails aus und mischt sie.
     * (Diese Methode bleibt exakt gleich wie vorher).
     */
    private List<Email> waehleZufaelligeEmails(List<Email> poolEcht, List<Email> poolPhishing,
                                               int anzahlEchte, int anzahlPhishing) {
        List<Email> resultat = new ArrayList<>();

        List<Email> echtKopie = new ArrayList<>(poolEcht);
        List<Email> phishingKopie = new ArrayList<>(poolPhishing);

        Collections.shuffle(echtKopie);
        Collections.shuffle(phishingKopie);

        for (int i = 0; i < anzahlEchte && i < echtKopie.size(); i++) {
            resultat.add(echtKopie.get(i));
        }

        for (int i = 0; i < anzahlPhishing && i < phishingKopie.size(); i++) {
            resultat.add(phishingKopie.get(i));
        }

        Collections.shuffle(resultat);
        return resultat;
    }

    /**
     * Gibt alle E-Mails zurück (für Tests oder Statistiken)
     */
    public List<Email> getAlleEmails() {
        List<Email> alle = new ArrayList<>();
        for (int i = 1; i <= LevelConfig.MAX_LEVEL; i++) {
            if (echtPools.containsKey(i)) {
                alle.addAll(echtPools.get(i));
            }
            if (phishingPools.containsKey(i)) {
                alle.addAll(phishingPools.get(i));
            }
        }
        return alle;
    }
}