# Changelog - Phishing Defender

## [0.8.0] - 2025-10-29

### Hinzugefügt ✨

* **Zentrale `Theme.java` Klasse:** Neue Klasse eingeführt, um alle UI-Farben, Schriftarten und Abstände an einem Ort zu verwalten. Enthält jetzt auch eine Methode `createStyledButton`, um einheitliche "Flat Design"-Buttons mit Hover-Effekt zu erstellen.
* **Javadoc-Header:** Alle 21 Klassen haben jetzt einen standardisierten Javadoc-Header bekommen, der kurz erklärt, was die Klasse macht.
* **Konstanten für Steuerung:** In `GameScreen.java` wurden die Tasten-Codes (z.B. `KeyEvent.VK_A`) für "Sicher", "Phishing", "Pause" und "Zurück" durch klare Konstanten (`TASTE_SICHER`, etc.) ersetzt.
* **Konstanten für Animation:** In `AnimatedBackgroundPanel.java` wurden feste Zahlenwerte für Partikelanzahl (`PARTICLE_COUNT`), Animationsgeschwindigkeit (`ANIMATION_DELAY_MS`) und Grid-Abstand (`GRID_SPACING`) durch Konstanten ersetzt.
* **Verbesserte Fehlerlogs:** In `catch`-Blöcken wird jetzt zusätzlich `e.printStackTrace()` aufgerufen, um die Fehlersuche zu erleichtern (z.B. in `HighscoreManager`, `StarsManager`).

### Geändert 🎨

* **Komplettes Button-Redesign:** Alle alten `RoundedButton`s (mit 3D-Effekt) wurden durch neue, flache `JButton`s ersetzt, die über `Theme.createStyledButton` erstellt werden. Betrifft fast alle Screens (Hauptmenü, Spiel, Levelauswahl, Pause, Ergebnisse, Einstellungen, Namenseingabe, Highscore). Sorgt für einen modernen Look.
* **Highscore Screen UI:** Das Aussehen der Highscore-Liste wurde überarbeitet (transparente Einträge, saubere Trennlinien), um die Lesbarkeit zu verbessern. Der "Cyber"-Header wurde beibehalten.
* **Laden von Assets (JAR-Kompatibilität):** Das Laden von Bildern (`ImageIcon`) und Sounds (`AudioSystem`) wurde von `new File(...)` auf `getClass().getResource(...)` umgestellt. Wichtig, damit das Spiel auch als exportierte `.jar`-Datei funktioniert. Betrifft `GameScreen`, `MusicManager`, `PhishingDefender`, `SplashScreen`.
* **Speicherort für Spieldaten:** Die Dateien `highscores.txt` und `stars_*.txt` werden jetzt in einem versteckten Ordner (`.phishingDefenderData`) im Home-Verzeichnis des Benutzers gespeichert (`System.getProperty("user.home")`). Verhindert Probleme mit Schreibrechten und funktioniert außerhalb der Entwicklungsumgebung.
* **Spiel-Balancing angepasst:** Die Schwierigkeit der Level wurde überarbeitet für eine bessere Lernkurve (besonders für die Zielgruppe 6-14 Jahre):
    * **Level 1:** Zeit pro E-Mail auf **20s** erhöht (war 15s), Leben auf **5** gesetzt (war 3).
    * **Level 2:** Zeit pro E-Mail auf **15s** gesetzt (war 20s), Leben auf **4** gesetzt.
    * **Level 3:** Zeit pro E-Mail auf **12s** gesetzt (war 20s), Leben auf **3** gesetzt (war 5).
* **Levelauswahl-Texte:** Die Beschreibungen auf den Level-Karten (Zeit pro E-Mail) wurden an das neue Balancing angepasst.
* **GameScreen Logik:** Die `maxLeben`-Zuweisung im Konstruktor wurde korrigiert, um das neue Balancing (5/4/3 Leben) korrekt umzusetzen. Formatierung des Timer-Labels vereinheitlicht. Score-Label zeigt jetzt "Score: X".
* **Splash Screen Logik:** Der Startvorgang (`MusicManager.start`, `new PhishingDefender`) wurde in einen `SwingWorker` in `PhishingDefender.main()` ausgelagert. Sorgt für einen flüssigeren Start ohne UI-Blockaden.

### Behoben 🐛

* **Animierter Hintergrund:** Die Partikel im Hintergrund erscheinen jetzt über die gesamte Breite und Höhe des Fensters, auch wenn es vergrößert wird (passten sich vorher nicht an).
* **Highscore Screen Text:** Buchstaben mit Unterlängen (wie 'g', 'p', 'y') wurden in der Highscore-Liste nicht mehr abgeschnitten. Die maximale Höhe der Listeneinträge wurde leicht erhöht.
* **StackOverflowError:** Ein Fehler wurde behoben, bei dem sich die Methode `packAndCenterWindow` in `PhishingDefender` unendlich oft selbst aufrief (Fehler beim Refactoring).

### Refaktorisiert 🧹

* **Code-Wiederholung (DRY):**
    * In `PhishingDefender`: Die Logik zum Anpassen der Fenstergröße und Zentrieren wurde in die Methode `packAndCenterWindow()` ausgelagert, um Duplikate im Konstruktor und in `zeigeWelcomeScreen()` zu vermeiden.
    * In `GameScreen`: Die Logik zum Anzeigen der Feedback-Karten (Panel leeren, Karten hinzufügen, UI aktualisieren) wurde in die Methode `zeigeFeedbackCard()` ausgelagert, um Duplikate in `zeigeFeedbackRichtig()` und `zeigeFeedbackFalsch()` zu vermeiden.
* **Kommentare bereinigt:** Viele unnötige oder offensichtliche Kommentare (z.B. `// Konstruktor`) wurden entfernt. Die verbleibenden Kommentare wurden auf Klarheit geprüft. Javadoc-Header wurden vereinheitlicht.
* **Debug-Ausgaben entfernt:** Die meisten `System.out.println`-Ausgaben (z.B. "Menu Music gestartet!") wurden entfernt oder auskommentiert, um die Konsole im fertigen Spiel sauber zu halten.

### Entfernt 🗑️

* Die Klasse `RoundedButton.java` wurde gelöscht, da sie nicht mehr verwendet wird.
* Die Methode `showSplash(int milliseconds)` in `SplashScreen.java` wurde entfernt, da der Splash Screen jetzt über den `SwingWorker` gesteuert wird.