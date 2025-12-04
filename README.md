# 🛡️ Phishing Defender

## 📢 Offizielle Projektvorstellung

**Phishing Defender** wird offiziell auf meiner persönlichen Webseite vorgestellt:

🔗 **Projektseite:**  
[Offizielle Projektseite von Phishing Defender](https://yusefbach.de/projects/phishing-defender.html)

Auf dieser Seite findest du:

- eine ausführliche Beschreibung des Projekts
- technische Details zur Umsetzung
- Screenshots & visuelle Einblicke
- Hintergründe zur Idee und Entwicklung
- zukünftige Erweiterungen & Roadmap

**Phishing Defender** ist ein interaktives Serious Game, das entwickelt wurde, um Spielern (Zielgruppe 8-14 Jahre) auf spielerische Weise Kompetenzen im Bereich Cyber-Security zu vermitteln. Der Spieler schlüpft in die Rolle eines "Cyber-Detektivs" und muss E-Mails analysieren, um Phishing-Angriffe abzuwehren.

**5372 Code-Zeilen**

**Projekt auch auffindbar unter https://yusefbach.de/projects/phishing-defender.html**

---

## 🎮 Features

- **Interaktives Gameplay:** Echtzeit-Analyse von E-Mails unter Zeitdruck.
- **Visuelles Feedback:** Animierte UI mit Partikel-Effekten, pulsierenden Elementen und "Flat Design".
- **Progression:** 3 Schwierigkeitsstufen, Highscore-System, Sterne-Bewertung und freischaltbare Achievements.
- **Persistenz:** Automatisches Speichern von Fortschritt, Einstellungen und Highscores (Asynchron & Thread-Safe).
- **Barrierefreiheit:** Klare visuelle Sprache (Farben, Icons) und intuitive Steuerung (Maus & Tastatur).

---

## 🛠️ Technische Highlights

Dieses Projekt demonstriert fortgeschrittene Java-Entwicklungskonzepte:

- **Advanced Swing Rendering:** \* Benutzerdefinierte Komponenten (`JComponent` Override).
  - Einsatz von `Graphics2D` (Anti-Aliasing, GradientPaint, AlphaComposite).
  - Double-Buffering für performante Hintergrund-Animationen.
- **Architektur:**
  - Klare Trennung von Daten (`Model`), Logik (`Manager`) und Anzeige (`View`).
  - **Multithreading:** Datei-Operationen (I/O) und Audio-Loading laufen in Hintergrund-Threads (`SwingWorker`, `Thread`), um die UI reaktionsfähig zu halten.
- **Datenhaltung:**
  - Dynamisches Laden von Level-Inhalten via **JSON** (Google Gson).
  - Speichern von Einstellungen und Spielständen via `Properties` und Serialisierung.
- **Audio:**
  - Integration der Java Sound API (`Clip`, `AudioSystem`) mit dynamischer Lautstärkeregelung (dB-Berechnung).

---

## 🚀 Installation & Start

### Voraussetzungen

- Java Runtime Environment (JRE) 17 oder höher.

### Starten

1.  Lade die `PhishingDefender.jar` herunter.
2.  Starte das Spiel per Doppelklick oder über die Konsole:
    ```bash
    java -jar PhishingDefender.jar
    ```

---

## 🕹️ Steuerung

| Taste             | Aktion                                 |
| :---------------- | :------------------------------------- |
| **[ A ]**         | E-Mail als **SICHER** markieren        |
| **[ L ]**         | E-Mail als **PHISHING** markieren      |
| **[ LEERTASTE ]** | Spiel pausieren / fortsetzen           |
| **[ ESC ]**       | Zurück zur Level-Auswahl               |
| **Maus**          | Interaktion mit UI-Elementen & Scanner |

---

## 👨‍💻 Autor

Entwickelt von **yusef03**.
_Projekt für das Modul PraxisProjekt WI/SE , 2025._
