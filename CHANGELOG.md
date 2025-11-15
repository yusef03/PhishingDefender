# Changelog - Phishing Defender

## [2.2.0] - 2025-11-14 (The Cyber-Visor Update)

Dieses Update ist ein massives visuelles und funktionales Overhaul des Kern-Gameplays.

### 🚀 Visuelles Redesign & Neue Features

* **Komplettes `GameScreen` Redesign ("Cyber-Visor" UI)**
    * Der `GameScreen` wurde von Grund auf neu gestaltet, um das "Cyber-Detektiv"-Thema zu verstärken. Das Layout nutzt nun ein `BorderLayout`, um die UI in ein zentrales E-Mail-Panel und angedockte "Widgets" aufzuteilen.

* **Neues Feature: Reaktive HUD-Widgets**
    * **Score-Widget (Links):** Zeigt nicht mehr nur den Punktestand, sondern auch:
        * Eine **"FIREWALL CHARGE"**-Leiste (`StreakBonusBar`), die den Fortschritt zum Bonus anzeigt und pulsiert, wenn sie voll ist.
        * Ein **"Score-Log"**, das alle erhaltenen Punkte auflistet.
    * **System-Integritäts-Widget (Rechts):** Ersetzt die alte `❤️❤️❤️`-Anzeige.
        * Implementiert das `IntegrityShieldPanel`: Ein animiertes Schild-Symbol, das bei 3 Leben grün leuchtet, bei 2 Leben orange wird und "bricht", und bei 1 Leben rot leuchtet und stark zerbrochen ist.
        * Enthält ein **"Fehler-Log"**, das die Gründe für verlorene Leben (z.B. "Falsch klassifiziert", "Zeit abgelaufen") auflistet.

* **Neues Feature: "Taktischer E-Mail-Scan"**
    * Der alte "Tipp anfordern"-Button wurde durch einen `🔬 E-MAIL SCANNEN`-Button ersetzt.
    * **Taktische Pause:** Das Klicken des Buttons **pausiert** sofort den Haupt-Spieltimer.
    * **Scan-Animation:** Eine visuelle Scan-Linie fährt über das *gesamte* E-Mail-Feld.
    * **Balancing:** Die Zeitstrafe wird erst *nach* Abschluss der Animation abgezogen, wenn der Tipp erscheint. Der Spieltimer läuft dann weiter. Die Anzeigedauer des Tipps wurde auf 6 Sekunden erhöht.

* **Feature-Erweiterung: Tutorial**
    * Der `TutorialScreen` wurde um zwei neue Seiten erweitert:
        * Seite 5: Erklärt die Kosten und Limitierungen des neuen Scan-Buttons.
        * Seite 6: Erklärt das neue `IntegrityShieldPanel`-System (Grün/Orange/Rot).

### ⚙️ Bug Fixes & Balancing

* **Kritischer Bug (Level-Freischaltung) GEFIXT:**
    * Ein Fehler wurde behoben, bei dem das Abschließen eines Levels nicht das nächste freigeschaltet hat.
    * *Ursache:* `ResultScreen` und `LevelSelectionScreen` erstellten fälschlicherweise neue `StarsManager`-Instanzen.
    * *Lösung:* Die zentrale `StarsManager`-Instanz wird nun vom `PhishingDefender` korrekt an alle Bildschirme weitergegeben.

* **Kritischer Bug (Audio) GEFIXT:**
    * Ein Fehler wurde behoben, bei dem die Hintergrundmusik trotz Stummschaltung nach einem Bildschirmwechsel neu startete.
    * *Lösung:* Der `MusicManager` prüft jetzt korrekt den Status im `SettingsManager`.

* **Visueller Bug (Timer-Animation) GEFIXT:**
    * Der sekundengenaue Timer-Text wurde durch eine flüssige `TimerBarPanel`-Komponente ersetzt, die alle 50ms aktualisiert wird, um eine "smoothe" Animation zu gewährleisten.

* **Visueller Bug (E-Mail-Anzeige) GEFIXT:**
    * Ein Bug wurde behoben, bei dem E-Mails im `JEditorPane` ohne Zeilenumbrüche angezeigt wurden.
    * *Lösung:* `\n`-Zeichen aus der `emails.json` werden jetzt korrekt in HTML-`<br>`-Tags umgewandelt.

* **Visueller Bug (Widget-Layout) GEFIXT:**
    * Lange Widget-Titel (z.B. "SYSTEM-INTEGRITÄT") wurden abgeschnitten.
    * *Lösung:* Die Titel verwenden jetzt HTML (`<br>`), um sauber in zwei Zeilen umgebrochen zu werden.
    * Die Ränder der Log-Fenster (`JScrollPane`) wurden entfernt (`setBorder(null)`), um sie sauber in das Widget-Design zu integrieren.

### 💻 Code & Architektur Refactoring

* **Architektur (Einstellungen):**
    * Die globalen `static`-Variablen für Musik-Einstellungen wurden aus dem `SettingsDialog` entfernt.
    * Eine neue Klasse `SettingsManager.java` wurde eingeführt, um die Einstellungen sauber zu kapseln.

* **Architektur (Gameplay-Werte):**
    * Alle "Magic Numbers" (Punkte, Bonus-Dauer, Zeitstrafen etc.) wurden aus dem `GameScreen` entfernt und in der `LevelConfig.java` zentralisiert.