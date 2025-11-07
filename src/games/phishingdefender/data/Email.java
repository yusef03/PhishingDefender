package games.phishingdefender.data;

/**
 * Stellt eine einzelne E-Mail im Spiel dar.
 * Enthält Absender, Betreff, Nachricht, ob es Phishing ist,
 * und einen Tipp für den Spieler.
 *
 * @author yusef03
 * @version 1.0
 */

public class Email {

    private String absender;
    private String betreff;
    private String nachricht;
    private boolean istPhishing;
    private String tipp;
    private int level;


    public Email(String absender, String betreff, String nachricht, boolean istPhishing, String tipp) {
        this.absender = absender;
        this.betreff = betreff;
        this.nachricht = nachricht;
        this.istPhishing = istPhishing;
        this.tipp = tipp;
    }

    public String getAbsender() {
        return absender;
    }

    public String getBetreff() {
        return betreff;
    }

    public String getNachricht() {
        return nachricht;
    }

    public boolean istPhishing() {
        return istPhishing;
    }

    public String getTipp() { return tipp; }

    public String toString() {
        return "Von: " + absender + "\nBetreff: " + betreff;
    }

    public int getLevel() {
        return level;
    }
}