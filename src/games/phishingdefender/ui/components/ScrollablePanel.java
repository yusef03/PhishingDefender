package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Fix für Swing JScrollPane Layout-Probleme.
 * Implementiert Scrollable, um Breite an Viewport anzupassen.
 * Ermöglicht korrektes Word-Wrapping in HTML-Labels.
 *
 * @author yusef03
 * @version 2.0
 */
public class ScrollablePanel extends JPanel implements Scrollable {

    public ScrollablePanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public ScrollablePanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16; // Mausrad-Schritt
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100; // Page-Up/Down Schritt
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true; // Zwingt Breite auf Viewport-Breite -> Zeilenumbruch
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}