package games.phishingdefender;

/**
 * Eine Daten-Klasse , die ein einzelnes Achievement (einen Erfolg) darstellt.
 * Sie speichert einen internen ID-Schlüssel, einen Namen, eine Beschreibung,
 * ein Icon und ob der Spieler es freigeschaltet hat.
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
     * Konstruktor für ein neues Achievement (standardmäßig gesperrt).
     */
    public Achievement(String id, String name, String description, String icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.isUnlocked = false; // Fängt immer als gesperrt an
    }


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
        isUnlocked = unlocked;
    }
}