package gui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

public class SplashScreen extends JWindow {

    private Timer timer;
    private int angle = 0;
    private final int ROTATION_SPEED = 3;
    private final Color BACKGROUND_COLOR = new Color(54, 172, 150); 
    private final Color SPINNER_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = Color.WHITE;

    public SplashScreen() {
        Image icon = Utility.getIconaApplicazione();
        if (icon != null) {
            setIconImage(icon);
        }

        JPanel contentPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                g2.setColor(BACKGROUND_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());

                try {
                    java.net.URL imgUrl = getClass().getResource("/images/logo_bugboard.png");
                    if (imgUrl != null) {
                        ImageIcon logoIcon = new ImageIcon(imgUrl);
                        Image img = logoIcon.getImage();
                        
                        int targetWidth = 120;
                        int targetHeight = (targetWidth * logoIcon.getIconHeight()) / logoIcon.getIconWidth(); 
                        
                        g2.drawImage(img, (getWidth() - targetWidth) / 2, 30, targetWidth, targetHeight, null);
                    }
                } catch (Exception e) {
                }

                int size = 40;
                int x = (getWidth() - size) / 2;
                int y = 190; 
                int strokeWidth = 4;

                g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(SPINNER_COLOR);
                g2.draw(new Arc2D.Double(x, y, size, size, angle, 270, Arc2D.OPEN));

                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(TEXT_COLOR);
                FontMetrics fm = g2.getFontMetrics();
                String text = "Avvio del backend in corso...";
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = y + size + 30;
                g2.drawString(text, textX, textY);

                g2.dispose();
            }
        };

        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        
        setSize(400, 300);
        setLocationRelativeTo(null);
        setContentPane(contentPanel);

        timer = new Timer(10, e -> {
            angle = (angle + ROTATION_SPEED) % 360;
            repaint();
        });
    }

    public void start() {
        setVisible(true);
        toFront();
        timer.start();
    }

    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        setVisible(false);
        dispose();
    }
}
