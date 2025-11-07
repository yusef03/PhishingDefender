# Changelog

Alle wichtigen Änderungen an diesem Projekt werden in dieser Datei dokumentiert.

## [v1.0.0] - 2025-11-07

Erster offizieller Release des Phishing Defender Spiels.

### Added (Hinzugefügt)

* **Hauptspiel:** Vollständiger Spiel-Loop mit `GameScreen` (Timer, Leben, Score).
* **Level-System:** 3 Level (Anfänger, Fortgeschritten, Experte) mit unterschiedlichen Timern und E-Mail-Anzahlen.
* **Dynamische Datenbank:** E-Mails werden aus einer externen `emails.json`-Datei geladen.
* **Spieler-Management:**
    * `StarsManager` zum Speichern des Fortschritts (Sterne) pro Spieler.
    * `HighscoreManager` für eine globale Highscore-Liste.
    * `AchievementManager` zum Freischalten und Speichern von 13+ Erfolgen.
* **UI-System:**
    * Vollständige UI-Navigation (Splash, Menü, Level-Auswahl, Spiel, Ergebnisse).
    * `Theme.java` für ein konsistentes "darkIT"-Design.
    * Animierter Partikel-Hintergrund (`AnimatedBackgroundPanel`).
    * Moderne Pop-up-Karten (`AchievementCard`, `FeedbackCard`, `TippCard`).
    * Vollständiges Einstellungsmenü (`SettingsDialog`) mit Lautstärkeregler und Admin-Reset.
* **Audio-System:**
    * `MusicManager` für Hintergrundmusik.
    * SFX für Gameplay-Events (richtig, falsch, Bonus, Panik-Timer).

### Changed (Geändert)

* Projektordner in eine professionelle Paketstruktur (`data`, `managers`, `ui`) refaktoriert.

### Fixed (Behoben)

* Sound-Lade-Logik stabilisiert.
* Panik-Timer stoppt jetzt korrekt in allen Spiel-Situationen (Pause, Neustart, Beenden).
