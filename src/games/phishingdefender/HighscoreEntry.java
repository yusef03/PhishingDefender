package games.phishingdefender;

/**
 * Stellt einen einzelnen Eintrag in der Highscore-Liste dar.
 * Speichert Name, Punkte, Genauigkeit, Level und Datum des Eintrags.
 *
 * @author yusef03
 * @version 1.0
 */

public class HighscoreEntry {

    private String name;
    private int punkte;
    private int genauigkeit;
    private int level;
    private String datum;

    public HighscoreEntry(String name, int punkte, int genauigkeit, int level, String datum) {
        this.name = name;
        this.punkte = punkte;
        this.genauigkeit = genauigkeit;
        this.level = level;
        this.datum = datum;
    }

    public String getName() {
        return name;
    }

    public int getPunkte() {
        return punkte;
    }

    public int getGenauigkeit() {
        return genauigkeit;
    }

    public int getLevel() {
        return level;
    }

    public String getDatum() {
        return datum;
    }

    public String toFileString() {
        return name + "," + punkte + "," + genauigkeit + "," + level + "," + datum;
    }

    public String toString() {
        return name + ": " + punkte + " Punkte (Level " + level + ")";
    }
}