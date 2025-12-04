package games.phishingdefender.data;

/**
 * Datenmodell für eine einzelne E-Mail.
 * Speichert Inhalt, Phishing-Status und Hilfetexte.
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
    private int level; // Wird automatisch aus JSON geladen

    /**
     * Konstruktor für manuelle Instanziierung.
     */
    public Email(String absender, String betreff, String nachricht, boolean istPhishing, String tipp) {
        this.absender = absender;
        this.betreff = betreff;
        this.nachricht = nachricht;
        this.istPhishing = istPhishing;
        this.tipp = tipp;
    }

    // --- Getter ---

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

    public String getTipp() {
        return tipp;
    }

    public int getLevel() {
        return level;
    }

    // --- Overrides ---

    @Override
    public String toString() {
        return "Von: " + absender + "\nBetreff: " + betreff;
    }
}