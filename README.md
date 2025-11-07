# 🛡️ Phishing Defender

Ein interaktives Lernspiel, das Spielern (insbesondere 6-14 Jährigen) beibringt, Phishing-E-Mails zu erkennen. Dieses Projekt wurde als Teil eines Uni-Projekts entwickelt.

## ✨ Features

* **Echtes Gameplay:** Analysiere E-Mails unter Zeitdruck (mit optionalem "Panik-Timer"-Sound).
* **Dynamische Datenbank:** Alle E-Mails werden aus einer externen `emails.json` geladen.
* **Professionelle Architektur:** Code ist sauber in `data`, `managers` und `ui` Pakete unterteilt.
* **Spieler-Management:** Separates Speichern von Fortschritten (Sterne) und Erfolgen für jeden Spieler.
* **Level-System:** 3 Level mit steigendem Schwierigkeitsgrad.
* **Belohnungen:** Schalte 13+ Achievements für besondere Leistungen frei.
* **Wettbewerb:** Vergleiche deine Punktzahl in einer globalen Highscore-Liste.
* **Lern-Modus:** Ein mehrseitiges Tutorial erklärt die Grundlagen von Phishing.

## 🎮 Steuerung

* **[A]** = Als "Sicher" markieren
* **[L]** = Als "Phishing" markieren
* **[LEERTASTE]** = Spiel pausieren
* **[ESC]** = Zurück (im Spiel)

## 🛠️ Build & Installation

Das Projekt verwendet Java (Swing) und die `Gson`-Bibliothek.

1.  Stelle sicher, dass Java (JDK 11 oder höher) installiert ist.
2.  Lade das Repository herunter.
3.  Kompiliere das Projekt (z.B. in IntelliJ IDEA).
4.  Die `gson-2.13.2.jar` (im `lib`-Ordner) muss im Classpath enthalten sein.

Eine fertige, lauffähige `.jar`-Datei findest du im **[Releases-Tab](https://github.com/yusef03/PhishingDefender/releases)**.

## 🧑‍💻 Autor

* **Yusef Bach** ([yusef03](https://github.com/yusef03))

---
*Dieses Repository enthält nur das Spiel "Phishing Defender" und nicht die gesamte "darkIT-spielesammlung".*
