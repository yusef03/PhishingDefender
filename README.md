    # 🛡️ Phishing Defender

**Autor: yusef03 (Dein Name)**
**Projekt: [DARK IT, Gruppe 03, Spielsammelung]**

---

## 1. Was ist Phishing Defender?

**Phishing Defender** ist ein interaktives Lernspiel (Serious Game), das in Java Swing entwickelt wurde. Das Ziel des Spiels ist es, Kindern und Jugendlichen (Zielgruppe 8-14 Jahre) auf spielerische Weise beizubringen, wie man Phishing-E-Mails erkennt.

Der Spieler schlüpft in die Rolle eines "Cyber-Detektivs" und muss in drei ansteigenden Schwierigkeitsstufen entscheiden, welche E-Mails "Sicher" und welche "Phishing" (Betrug) sind.

## 2. Features

* **Interaktives Gameplay:** Spieler müssen unter Zeitdruck E-Mails analysieren und Entscheidungen treffen.
* **3 Schwierigkeitsstufen:** Von offensichtlichen Fehlern (Level 1) bis hin zu subtilem Social Engineering (Level 3).
* **Lern-System:** Ein Pflicht-Tutorial für neue Spieler und eine "Tipp-Karte" nach jedem Fehler erklären, *warum* eine E-Mail Phishing war.
* **Speichersystem:** Das Spiel speichert den Fortschritt (Sterne) und die Erfolge für jeden Spieler-Namen separat.
* **Highscore-Liste:** Ein lokales Highscore-System (`highscores.txt`) speichert die besten Detektive.
* **Erfolgssystem (Achievements):** Spieler werden mit Trophäen für besondere Leistungen belohnt (z.B. "10 in Folge richtig").
* **Admin-Modus:** Eine passwortgeschützte Reset-Funktion im Einstellungsmenü, um das Spiel für Messe-Einsätze zurückzusetzen.
* **Performance-Optimierung:** Ein custom `AnimatedBackgroundPanel`, das "Buffering" nutzt, um einen flüssigen 30-FPS-Effekt ohne hohe CPU-Last zu erzeugen.

## 3. Wie starte ich das Spiel?

1.  Klone das Repository.
2.  Stelle sicher, dass die `gson-2.13.2.jar` (oder neuere Version) im `lib/`-Ordner als Bibliothek in IntelliJ eingebunden ist.
3.  Führe die `main`-Methode in der Klasse `games.phishingdefender.PhishingDefender` aus.

## 4. Admin-Funktion (Für Tester & Prüfer)

Um alle Spielstände (Highscores und Sterne/Tutorial-Status aller Spieler) zurückzusetzen:
1.  Starte das Spiel und gehe ins Hauptmenü.
2.  Klicke auf das Zahnrad-Icon (⚙️) unten rechts.
3.  Klicke auf den kleinen, unauffälligen roten Knopf (`●`) unten im Einstellungsfenster.
4.  Gib das Admin-Passwort ein: **`admin123`**
5.  Bestätige den Reset und starte das Spiel neu.

## 5. Verwendete Technologien

* **Sprache:** Java (JDK 17+)
* **UI:** Java Swing (für die gesamte Benutzeroberfläche)
* **Bibliotheken:**
    * **Gson (by Google):** Zum Einlesen der E-Mail-Datenbank aus der `assets/emails.json`.
    * **Java Sound API (`javax.sound.sampled`):** Für Hintergrundmusik und Sound-Effekte.