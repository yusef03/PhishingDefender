package games.phishingdefender.ui.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Zentrale Klasse für UI-Konstanten wie Farben, Schriftarten und Ränder (Padding).
 * Enthält auch eine Factory-Methode (`createStyledButton`) zum Erstellen
 * von konsistent gestylten "Flat Design"-Buttons mit Hover-Effekt.
 * Hilft, das Erscheinungsbild des Spiels zentral zu verwalten.
 *
 * @author yusef03
 * @version 1.0
 */

public class Theme {

    // Hintergrund
    public static final Color COLOR_BACKGROUND_DARK = new Color(18, 18, 18);
    public static final Color COLOR_PANEL_DARK = new Color(30, 30, 30);

    // Text
    public static final Color COLOR_TEXT_PRIMARY = new Color(240, 240, 240);
    public static final Color COLOR_TEXT_SECONDARY = new Color(170, 170, 170);

    // Haupt-Akzent (Grün statt Orange)
    public static final Color COLOR_ACCENT_GREEN = new Color(0, 220, 120);
    public static final Color COLOR_ACCENT_GREEN_HOVER = new Color(20, 240, 140);

    // Neutraler Button (Grau statt Blau/Purple)
    public static final Color COLOR_BUTTON_NEUTRAL = new Color(70, 70, 70);
    public static final Color COLOR_BUTTON_NEUTRAL_HOVER = new Color(90, 90, 90);

    // Warn-Button (Rot)
    public static final Color COLOR_BUTTON_RED = new Color(220, 50, 50);
    public static final Color COLOR_BUTTON_RED_HOVER = new Color(240, 70, 70);


    // Orange -> Green
    public static final Color COLOR_ACCENT_ORANGE = COLOR_ACCENT_GREEN;
    public static final Color COLOR_ACCENT_ORANGE_HOVER = COLOR_ACCENT_GREEN_HOVER;

    // Green -> Green
    public static final Color COLOR_BUTTON_GREEN = COLOR_ACCENT_GREEN;
    public static final Color COLOR_BUTTON_GREEN_HOVER = COLOR_ACCENT_GREEN_HOVER;

    // Grey -> Neutral
    public static final Color COLOR_BUTTON_GREY = COLOR_BUTTON_NEUTRAL;
    public static final Color COLOR_BUTTON_GREY_HOVER = COLOR_BUTTON_NEUTRAL_HOVER;

    // Blue -> Neutral
    public static final Color COLOR_BUTTON_BLUE = COLOR_BUTTON_NEUTRAL;
    public static final Color COLOR_BUTTON_BLUE_HOVER = COLOR_BUTTON_NEUTRAL_HOVER;

    // (NEUE FARBE FÜR SCHRITT 2 - HIER PLATZIERT FÜR TIMER)
    public static final Color COLOR_ACCENT_BLUE = new Color(100, 200, 255);

    // Purple -> Neutral
    public static final Color COLOR_BUTTON_PURPLE = COLOR_BUTTON_NEUTRAL;
    public static final Color COLOR_BUTTON_PURPLE_HOVER = COLOR_BUTTON_NEUTRAL_HOVER;

    // NEUE TIMER-FARBEN
    public static final Color COLOR_TIMER_HIGH = new Color(0, 220, 120);
    public static final Color COLOR_TIMER_MEDIUM = new Color(255, 140, 0);
    public static final Color COLOR_TIMER_LOW = new Color(220, 50, 50);


    // === SCHRIFTARTEN ===
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 42);
    public static final Font FONT_BUTTON_LARGE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_BUTTON_MEDIUM = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_BUTTON_SMALL = new Font("SansSerif", Font.BOLD, 16);

    // NEUE SCHRIFTART FÜR TIMER
    public static final Font FONT_HUD = new Font("Monospace", Font.BOLD, 34);


    // === RÄNDER (Padding) ===
    public static final Border PADDING_BUTTON_LARGE = BorderFactory.createEmptyBorder(15, 35, 15, 35);
    public static final Border PADDING_BUTTON_MEDIUM = BorderFactory.createEmptyBorder(12, 30, 12, 30);
    public static Color COLOR_ACCENT_BLUE_HOVER;


    /**
     * FABRIK-METHODE für unsere "Flat Buttons".
     * (Bleibt exakt gleich, holt sich die Farben von oben)
     */
    public static JButton createStyledButton(String text, Font font, Color normalColor, Color hoverColor, Border padding) {

        JButton button = new JButton(text) {
            private boolean isHovered = false;

            // Dieser Block wird beim Erstellen des Buttons aufgerufen
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint(); // Neu zeichnen
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint(); // Neu zeichnen
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Hintergrund-Farbe (je nach Hover)
                if (isHovered) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(normalColor);
                }

                // Zeichne das abgerundete Rechteck
                // (Die 25, 25 ist die Stärke der Rundung)
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        // WICHTIGE EINSTELLUNGEN, damit Look funktioniert
        button.setFont(font);
        button.setForeground(COLOR_TEXT_PRIMARY); // Text bleibt weiß
        button.setBorder(padding);                // Padding für die Größe
        button.setContentAreaFilled(false);       // WICHTIG: Sag Swing, nicht den Standard-Hintergrund zu malen
        button.setFocusPainted(false);
        button.setOpaque(false);                  // WICHTIG
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}