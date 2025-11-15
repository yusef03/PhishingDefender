package games.phishingdefender.ui.components;

import java.awt.*;

/**
 * Zentrale Konfigurationsklasse für alle Level-spezifischen Daten.
 * Verhindert "Magic Numbers" und Duplizierung in anderen Klassen.
 */
public class LevelConfig {

    // === LEVEL 1 ===
    public static final int L1_LEVEL_NUM = 1;
    public static final String L1_NAME = "ANFÄNGER";
    public static final String L1_ICON = "🎯";
    public static final String L1_SCHWIERIGKEIT = "LEICHT";
    public static final int L1_ZEIT = 20;
    public static final int L1_ECHTE = 6;
    public static final int L1_PHISHING = 4;
    public static final int L1_GESAMT_EMAILS = L1_ECHTE + L1_PHISHING;
    public static final int L1_MAX_LEBEN = 3;
    public static final Color L1_FARBE = new Color(0, 200, 110);


    // === LEVEL 2 ===
    public static final int L2_LEVEL_NUM = 2;
    public static final String L2_NAME = "FORTGESCHRITTEN";
    public static final String L2_ICON = "🎯🎯";
    public static final String L2_SCHWIERIGKEIT = "MITTEL";
    public static final int L2_ZEIT = 15;
    public static final int L2_ECHTE = 7;
    public static final int L2_PHISHING = 8;
    public static final int L2_GESAMT_EMAILS = L2_ECHTE + L2_PHISHING;
    public static final int L2_MAX_LEBEN = 3;
    public static final Color L2_FARBE = new Color(255, 200, 50);

    // === LEVEL 3 ===
    public static final int L3_LEVEL_NUM = 3;
    public static final String L3_NAME = "EXPERTE";
    public static final String L3_ICON = "🎯🎯🎯";
    public static final String L3_SCHWIERIGKEIT = "SCHWER";
    public static final int L3_ZEIT = 12;
    public static final int L3_ECHTE = 10;
    public static final int L3_PHISHING = 10;
    public static final int L3_GESAMT_EMAILS = L3_ECHTE + L3_PHISHING;
    public static final int L3_MAX_LEBEN = 3;
    public static final Color L3_FARBE = new Color(220, 50, 50);

    // === ALLGEMEIN ===
    public static final int MAX_LEVEL = 3;

    // Gesamtanzahl aller einzigartigen E-Mails im Spiel (für Rang-Berechnung)
    // (Level 1: 10+10) + (Level 2: 15+15) + (Level 3: 20+20) = 80
    public static final int GESAMT_EINZIGARTIGE_EMAILS_DB = 20 + 30 + 40;

    // === DATEN-SPEICHERPFADE ===
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String SAVE_DIR_NAME = ".phishingDefenderData";
    public static final String SAVE_DIR_PATH = USER_HOME + java.io.File.separator + SAVE_DIR_NAME;

    // === GAMEPLAY-WERTE ===
    public static final int PUNKTE_NORMAL = 10;
    public static final int PUNKTE_BONUS = 20;
    public static final int BONUS_SERIE_NOETIG = 5;
    public static final int BONUS_DAUER_IN_EMAILS = 3;
    public static final double BONUS_ZEIT_MULTIPLIKATOR = 0.5; // 0.5 = 50% mehr Zeit

    // Tipp-Button-Konfiguration
    public static final int L1_ANZAHL_TIPPS = 5;      // 5 Tipps für Level 1
    public static final int L2_ANZAHL_TIPPS = 3;      // 3 Tipps für Level 2
    public static final int L3_ANZAHL_TIPPS = 2;      // 2 Tipps für Level 3

    public static final int L1_TIPP_KOSTEN = 6; // Leichter: nur 3 Sek
    public static final int L2_TIPP_KOSTEN = 4; // Mittel: 5 Sek
    public static final int L3_TIPP_KOSTEN = 2; // Schwer: 7 Sek
}