package games.phishingdefender.data;

/**
 * Repräsentiert einen einzelnen Erfolg (Achievement) im Spiel.
 * Speichert ID, Anzeige-Daten und den Freischalt-Status.
 *
 * @author yusef03
 * @version 1.0
 */
public class Achievement {

    private String id;
    private String name;
    private String description;
    private String icon;
    private boolean isUnlocked;

    /**
     * Konstruktor: Initialisiert das Achievement als gesperrt.
     */
    public Achievement(String id, String name, String description, String icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.isUnlocked = false;
    }

    // --- Getter & Setter ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.isUnlocked = unlocked;
    }
}