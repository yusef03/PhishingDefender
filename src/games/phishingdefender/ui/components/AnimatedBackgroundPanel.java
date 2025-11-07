package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Ein JPanel mit einem animierten Hintergrund-Effekt.
 * (Version 2.0 - Performance-Optimiert mit Buffering)
 *
 * @author yusef03
 * @version 2.0
 */
public class AnimatedBackgroundPanel extends JPanel {

    private List<Particle> particles;
    private Timer animationTimer;
    private Random random;


    // erstellen ein "privates Bild" (Buffer) für Tapete
    private BufferedImage backgroundBuffer;


    public AnimatedBackgroundPanel() {
        particles = new ArrayList<>();
        random = new Random();

        // (Partikel erstellen - optimiert)
        for (int i = 0; i < 100; i++) {
            particles.add(new Particle());
        }

        // (Timer auf 30 FPS - optimiert)
        animationTimer = new Timer(33, e -> {
            updateParticles();
            repaint();
        });
        animationTimer.start();
    }

    /**
     * Malt den statischen Hintergrund (Gradient + Gitter) EINMALIG auf
     * unser privates Bild (den Buffer).
     */
    private void createBackgroundBuffer() {
        int w = getWidth();
        int h = getHeight();

        // Wenn das Fenster noch keine Größe hat, brich ab.
        if (w <= 0 || h <= 0) return;

        // Erstelle das Bild in der Größe des Fensters
        backgroundBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = backgroundBuffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Gradient-Hintergrund
        GradientPaint gradient = new GradientPaint(
                0, 0, Theme.COLOR_BACKGROUND_DARK,
                0, h, new Color(10, 10, 10)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);

        // 2. Cyber-Grid Linien
        g2.setColor(new Color(0, 220, 120, 15));
        for (int i = 0; i < h; i += 120) {
            g2.drawLine(0, i, w, i);
        }

        g2.dispose();
    }


    /**
     * updateParticles bleibt 100% gleich.
     */
    private void updateParticles() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;
        for (Particle p : particles) {
            p.update();
            if (p.y > h) {
                p.reset(w, h);
            }
        }
    }

    /**
     * paintComponent
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- OPTIMIERUNG ---
        if (backgroundBuffer == null || backgroundBuffer.getWidth() != getWidth() || backgroundBuffer.getHeight() != getHeight()) {
            createBackgroundBuffer();
        }

        // 2. Zeichne die fertige Tapete.EXTREM SCHNELL.
        g2d.drawImage(backgroundBuffer, 0, 0, null);


        // 3. Partikel zeichnen
        for (Particle p : particles) {
            p.draw(g2d);
        }
    }

    private class Particle {
        float x, y;
        float speed;
        int size;
        Color color;

        Particle() {
            reset(800, 600);
        }

        void reset(int panelWidth, int panelHeight) {
            if (panelWidth <= 0) panelWidth = 1;
            if (panelHeight <= 0) panelHeight = 1;

            x = random.nextInt(panelWidth);
            y = -random.nextInt(panelHeight);
            speed = 0.5f + random.nextFloat() * 2f;
            size = 2 + random.nextInt(4);

            int r = 100 + random.nextInt(100);
            int g = 100 + random.nextInt(100);
            int b = 100 + random.nextInt(100);
            int alpha = 100 + random.nextInt(156);
            color = new Color(r, g, b, alpha);
        }

        void update() {
            y += speed;
        }

        void draw(Graphics2D g2d) {
            if ((int)x % 5 == 0) {
                g2d.setColor(new Color(0, 220, 120, color.getAlpha()));
                g2d.fillRect((int)x, (int)y, size * 3, size * 2);
                int[] xPoints = {(int)x, (int)x + (size * 3) / 2, (int)x + size * 3};
                int[] yPoints = {(int)y, (int)y + size, (int)y};
                g2d.fillPolygon(xPoints, yPoints, 3);
            } else {
                g2d.setColor(color);
                g2d.fillOval((int)x, (int)y, size, size);
            }
        }
    }
}