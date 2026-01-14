package gui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;

public class LoadingGlassPane extends JComponent {

    private Timer timer;
    private int angle = 0;
    private final int ROTATION_SPEED = 10; // Velocità rotazione
    private final Color BACKGROUND_COLOR = new Color(0, 0, 0, 150); // Nero semitrasparente
    private final Color SPINNER_COLOR = new Color(255, 255, 255); // Bianco

    public LoadingGlassPane() {
        // Blocca gli eventi del mouse
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {}
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        // Blocca gli eventi della tastiera
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                e.consume();
            }
        });
        
        setFocusTraversalKeysEnabled(false);

        // Timer per l'animazione
        timer = new Timer(20, e -> {
            angle = (angle + ROTATION_SPEED) % 360;
            repaint();
        });
    }

    public void start() {
        setVisible(true);
        requestFocusInWindow(); // Prendi il focus per bloccare TAB
        timer.start();
    }

    public void stop() {
        timer.stop();
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Disegna lo sfondo scuro
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 2. Calcola centro e dimensioni spinner
        int size = 60;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        int strokeWidth = 6;

        // 3. Disegna lo spinner (cerchio che gira)
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(SPINNER_COLOR);
        
        // Disegna un arco che ruota
        // x, y, w, h, startAngle, arcAngle, type
        g2.draw(new Arc2D.Double(x, y, size, size, angle, 270, Arc2D.OPEN));

        // Testo opzionale "Caricamento..."
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        String text = "Elaborazione in corso...";
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        int textY = y + size + 40;
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}
