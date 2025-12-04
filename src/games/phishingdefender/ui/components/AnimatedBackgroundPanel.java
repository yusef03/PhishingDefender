package games.phishingdefender.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animierter Hintergrund mit Partikeln.
 * Nutzt Double-Buffering für hohe Performance (30 FPS).
 *
 * @author yusef03
 * @version 2.0
 */
public class AnimatedBackgroundPanel extends JPanel {

    private final List<Particle> particles;
    private final Random random;
    private BufferedImage backgroundBuffer; // Statischer Cache

    public AnimatedBackgroundPanel() {
        particles = new ArrayList<>();
        random = new Random();

        // 100 Partikel initialisieren
        for (int i = 0; i < 100; i++) {
            particles.add(new Particle());
        }

        // Animation Loop (~30 FPS)
        Timer animationTimer = new Timer(33, e -> {
            updateParticles();
            repaint();
        });
        animationTimer.start();
    }

    // Zeichnet den statischen Teil (Verlauf + Gitter) in einen Puffer
    private void createBackgroundBuffer() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        backgroundBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = backgroundBuffer.createGraphics();

        // Verlauf
        GradientPaint gradient = new GradientPaint(
                0, 0, Theme.COLOR_BACKGROUND_DARK,
                0, h, new Color(10, 10, 10)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);

        // Cyber-Grid
        g2.setColor(new Color(0, 220, 120, 15));
        for (int i = 0; i < h; i += 120) {
            g2.drawLine(0, i, w, i);
        }
        g2.dispose();
    }

    private void updateParticles() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0) return;

        for (Particle p : particles) {
            p.update();
            if (p.y > h) p.reset(w, h);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Puffer neu erstellen bei Größenänderung
        if (backgroundBuffer == null ||
                backgroundBuffer.getWidth() != getWidth() ||
                backgroundBuffer.getHeight() != getHeight()) {
            createBackgroundBuffer();
        }

        // 1. Hintergrund aus Cache
        g2d.drawImage(backgroundBuffer, 0, 0, null);

        // 2. Dynamische Partikel
        for (Particle p : particles) {
            p.draw(g2d);
        }
    }

    // Interne Klasse für Partikel-Logik
    private class Particle {
        float x, y, speed;
        int size;
        Color color;

        Particle() { reset(800, 600); }

        void reset(int w, int h) {
            if (w <= 0) w = 1;
            x = random.nextInt(w);
            y = -random.nextInt(h > 0 ? h : 1);
            speed = 0.5f + random.nextFloat() * 2f;
            size = 2 + random.nextInt(4);

            int alpha = 100 + random.nextInt(156);
            color = new Color(100 + random.nextInt(100),
                    100 + random.nextInt(100),
                    100 + random.nextInt(100), alpha);
        }

        void update() { y += speed; }

        void draw(Graphics2D g) {
            g.setColor(color);
            g.fillOval((int)x, (int)y, size, size);
        }
    }
}