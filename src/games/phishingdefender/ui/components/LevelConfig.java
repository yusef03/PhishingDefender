package games.phishingdefender.ui.components;

import java.awt.*;
import java.io.File;

/**
 * Statische Konfiguration für alle Spiel-Parameter.
 * Definiert Farben, Schwierigkeitsgrade, Pfade und Balancing.
 *
 * @author yusef03
 * @version 2.0
 */
public class LevelConfig {

    // === LEVEL 1 ===
    public static final int L1_LEVEL_NUM = 1;
    public static final String L1_NAME = "ANFÄNGER";
    public static final String L1_ICON = "icon_trophy.png";
    public static final String L1_SCHWIERIGKEIT = "LEICHT";
    public static final int L1_ZEIT = 20;
    public static final int L1_ECHTE = 6;
    public static final int L1_PHISHING = 4;
    public static final int L1_GESAMT_EMAILS = 10;
    public static final int L1_MAX_LEBEN = 3;
    public static final Color L1_FARBE = new Color(0, 200, 110);

    // === LEVEL 2 ===
    public static final int L2_LEVEL_NUM = 2;
    public static final String L2_NAME = "FORTGESCHRITTEN";
    public static final String L2_ICON = "icon_trophy.png";
    public static final String L2_SCHWIERIGKEIT = "MITTEL";
    public static final int L2_ZEIT = 15;
    public static final int L2_ECHTE = 7;
    public static final int L2_PHISHING = 8;
    public static final int L2_GESAMT_EMAILS = 15;
    public static final int L2_MAX_LEBEN = 3;
    public static final Color L2_FARBE = new Color(255, 200, 50);

    // === LEVEL 3 ===
    public static final int L3_LEVEL_NUM = 3;
    public static final String L3_NAME = "EXPERTE";
    public static final String L3_ICON = "icon_trophy.png";
    public static final String L3_SCHWIERIGKEIT = "SCHWER";
    public static final int L3_ZEIT = 12;
    public static final int L3_ECHTE = 10;
    public static final int L3_PHISHING = 10;
    public static final int L3_GESAMT_EMAILS = 20;
    public static final int L3_MAX_LEBEN = 3;
    public static final Color L3_FARBE = new Color(220, 50, 50);

    // === ALLGEMEINES ===
    public static final int MAX_LEVEL = 3;

    // Für Ranglisten-Berechnung (Summe aller Mails in der DB)
    public static final int GESAMT_EINZIGARTIGE_EMAILS_DB = 90;

    // === SPEICHERUNG ===
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String SAVE_DIR_NAME = ".phishingDefenderData";
    public static final String SAVE_DIR_PATH = USER_HOME + File.separator + SAVE_DIR_NAME;

    // === BALANCING & SCORES ===
    public static final int PUNKTE_NORMAL = 10;
    public static final int PUNKTE_BONUS = 20;

    public static final int BONUS_SERIE_NOETIG = 5;
    public static final int BONUS_DAUER_IN_EMAILS = 3;
    public static final double BONUS_ZEIT_MULTIPLIKATOR = 0.5; // +50% Zeit

    // Scanner-Kosten (Zeitstrafe in Sekunden)
    public static final int L1_TIPP_KOSTEN = 6;
    public static final int L2_TIPP_KOSTEN = 4;
    public static final int L3_TIPP_KOSTEN = 2;

    // Verfügbare Scans pro Level
    public static final int L1_ANZAHL_TIPPS = 5;
    public static final int L2_ANZAHL_TIPPS = 3;
    public static final int L3_ANZAHL_TIPPS = 2;
}