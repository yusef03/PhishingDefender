package games.phishingdefender.ui.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Zentrale Verwaltung für UI-Konstanten (Farben, Fonts).
 * Factory-Methoden für konsistentes "Flat Design".
 *
 * @author yusef03
 * @version 2.0
 */
public class Theme {

    // === HINTERGRUND ===
    public static final Color COLOR_BACKGROUND_DARK = new Color(18, 18, 18);
    public static final Color COLOR_PANEL_DARK = new Color(30, 30, 30);

    // === TEXT ===
    public static final Color COLOR_TEXT_PRIMARY = new Color(240, 240, 240);
    public static final Color COLOR_TEXT_SECONDARY = new Color(170, 170, 170);

    // === AKZENTE ===
    public static final Color COLOR_ACCENT_GREEN = new Color(0, 220, 120);
    public static final Color COLOR_ACCENT_GREEN_HOVER = new Color(20, 240, 140);

    public static final Color COLOR_ACCENT_BLUE = new Color(100, 200, 255);
    public static final Color COLOR_ACCENT_BLUE_HOVER = new Color(120, 220, 255);

    // Wird als "Orange" bezeichnet, ist aber aus Designgründen aktuell Grün
    public static final Color COLOR_ACCENT_ORANGE = COLOR_ACCENT_GREEN;
    public static final Color COLOR_ACCENT_ORANGE_HOVER = COLOR_ACCENT_GREEN_HOVER;

    // === BUTTON FARBEN ===
    public static final Color COLOR_BUTTON_NEUTRAL = new Color(70, 70, 70);
    public static final Color COLOR_BUTTON_NEUTRAL_HOVER = new Color(90, 90, 90);

    public static final Color COLOR_BUTTON_GREEN = COLOR_ACCENT_GREEN;
    public static final Color COLOR_BUTTON_GREEN_HOVER = COLOR_ACCENT_GREEN_HOVER;

    public static final Color COLOR_BUTTON_RED = new Color(220, 50, 50);
    public static final Color COLOR_BUTTON_RED_HOVER = new Color(240, 70, 70);

    // Alias-Farben für flexible Nutzung
    public static final Color COLOR_BUTTON_BLUE = COLOR_BUTTON_NEUTRAL;
    public static final Color COLOR_BUTTON_BLUE_HOVER = COLOR_BUTTON_NEUTRAL_HOVER;

    public static final Color COLOR_BUTTON_GREY = COLOR_BUTTON_NEUTRAL;
    public static final Color COLOR_BUTTON_GREY_HOVER = COLOR_BUTTON_NEUTRAL_HOVER;

    public static final Color COLOR_BUTTON_PURPLE = COLOR_BUTTON_NEUTRAL;
    public static final Color COLOR_BUTTON_PURPLE_HOVER = COLOR_BUTTON_NEUTRAL_HOVER;

    // === HUD FARBEN (Timer etc.) ===
    public static final Color COLOR_TIMER_HIGH = new Color(0, 220, 120);
    public static final Color COLOR_TIMER_MEDIUM = new Color(255, 140, 0);
    public static final Color COLOR_TIMER_LOW = new Color(220, 50, 50);

    // === FONTS ===
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 42);
    public static final Font FONT_BUTTON_LARGE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_BUTTON_MEDIUM = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_BUTTON_SMALL = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_HUD = new Font("Monospace", Font.BOLD, 34);

    // === BORDERS ===
    public static final Border PADDING_BUTTON_LARGE = BorderFactory.createEmptyBorder(15, 35, 15, 35);
    public static final Border PADDING_BUTTON_MEDIUM = BorderFactory.createEmptyBorder(12, 30, 12, 30);

    /**
     * Erstellt einen gestylten Button mit Hover-Effekten.
     */
    public static JButton createStyledButton(String text, Font font, Color normalColor, Color hoverColor, Border padding) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? hoverColor : normalColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(font);
        button.setForeground(COLOR_TEXT_PRIMARY);
        button.setBorder(padding);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Lädt ein Icon sicher aus den Ressourcen.
     */
    public static ImageIcon loadIcon(String filename, int size) {
        try {
            URL url = Theme.class.getResource("/games/phishingdefender/assets/images/" + filename);
            if (url == null) return null;
            ImageIcon icon = new ImageIcon(url);
            return new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }
}