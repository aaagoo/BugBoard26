package gui.util;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundedPanel extends AbstractBorder {
    private static final int RADIUS = 20;

    public static final Color SFONDO = new Color(146, 250, 176);
    public static final Color PANNELLO = new Color(202, 250, 220);
    public static final Color FINESTRA = new Color(242, 243, 228);

    private Color backgroundColor;
    private Color outerColor;

    public RoundedPanel(String tipo) {
        switch (tipo.toLowerCase()) {
            case "pannello":
                this.backgroundColor = PANNELLO;
                this.outerColor = SFONDO;
                break;
            case "finestra":
                this.backgroundColor = FINESTRA;
                this.outerColor = PANNELLO;
                break;
        }
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(outerColor);
        g2d.fillRect(x, y, width, height);

        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(x, y, width - 1, height - 1, RADIUS, RADIUS);

        g2d.setColor(Color.lightGray);
        g2d.drawRoundRect(x, y, width - 1, height - 1, RADIUS, RADIUS);

        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(RADIUS / 2, RADIUS / 2, RADIUS / 2, RADIUS / 2);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
