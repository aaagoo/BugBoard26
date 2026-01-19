package gui.util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StyleManager {

    // Palette Colori
    public static final Color VERDE_ACQUA = new Color(54, 172, 150);
    public static final Color BIANCO_SPORCO = new Color(242, 243, 228);
    public static final Color TESTO_SCURO = Color.BLACK;
    public static final Color BIANCO = Color.WHITE;

    // Font
    public static final Font FONT_HEADER = new Font("Inter", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);


    public static void styleTable(JTable table, JScrollPane scrollPane) {
        if (table == null) return;

        // Header
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(VERDE_ACQUA);
        header.setForeground(BIANCO_SPORCO);
        header.setFont(FONT_HEADER);

        // ScrollPane
        if (scrollPane != null) {
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBorder(null);
            scrollPane.getViewport().setBackground(table.getBackground());
        }
        
        // Tabella
        table.setFillsViewportHeight(true);
    }

    /**
     * Applica lo stile personalizzato ai CheckBox.
     */
    public static void styleCheckBox(JCheckBox checkBox) {
        if (checkBox == null) return;

        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.setForeground(TESTO_SCURO);
        checkBox.setFont(FONT_NORMAL);

        // Icona non selezionata
        checkBox.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(VERDE_ACQUA);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(x + 1, y + 1, 14, 14, 4, 4);
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return 18; }
            @Override
            public int getIconHeight() { return 18; }
        });

        // Icona selezionata
        checkBox.setSelectedIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sfondo pieno
                g2.setColor(VERDE_ACQUA);
                g2.fillRoundRect(x + 1, y + 1, 14, 14, 4, 4);
                
                // Spunta bianca
                g2.setColor(BIANCO);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(x + 4, y + 8, x + 7, y + 11);
                g2.drawLine(x + 7, y + 11, x + 12, y + 5);
                
                g2.dispose();
            }

            @Override
            public int getIconWidth() { return 18; }
            @Override
            public int getIconHeight() { return 18; }
        });
    }
}
