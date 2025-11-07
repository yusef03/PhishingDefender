package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Eine spezielle JPanel-Implementierung, die das Scrollable-Interface nutzt.
 * Dieser "Hack" löst einen Swing-Bug, bei dem ein JScrollPane (der ein
 * BoxLayout enthält) fälschlicherweise Scrollbars anzeigt.
 *
 * setScrollableTracksViewportWidth(true) ist der Schlüssel: Es zwingt
 * das Panel, seine Breite an den Viewport anzupassen, wodurch der Inhalt
 * (z.B. HTML-Labels) korrekt umbricht und die Höhe richtig berechnet wird.
 *
 * @author yusefbach
 */
public class ScrollablePanel extends JPanel implements Scrollable {

    // Konstruktor, der einfach den BoxLayout.Y_AXIS setzt
    public ScrollablePanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    // Konstruktor, der ein beliebiges Layout-Management erlaubt
    public ScrollablePanel(LayoutManager layout) {
        super(layout);
    }


    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16; // Wie schnell man mit Klick auf Pfeil scrollt
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 100; // Wie schnell man mit Klick auf Leiste scrollt
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}