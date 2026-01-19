package gui.util;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class StyleManager {

    // Palette Colori
    public static final Color VERDE_ACQUA = new Color(54, 172, 150);
    public static final Color BIANCO_SPORCO = new Color(242, 243, 228);

    // Font
    public static final Font FONT_HEADER = new Font("Inter", Font.BOLD, 14);


    public static void setupTheme() {
        try {
            FlatLightLaf.setup();

            // Colori
            UIManager.put("Component.accentColor", VERDE_ACQUA);
            UIManager.put("Button.accentColor", VERDE_ACQUA);
            UIManager.put("Focus.color", VERDE_ACQUA);
            UIManager.put("TabbedPane.selectedBackground", VERDE_ACQUA);

            // Forme
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);
            UIManager.put("ProgressBar.arc", 999);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 10);

            // Funzionalità
            UIManager.put("PasswordField.showRevealButton", true);
            UIManager.put("TextField.showClearButton", true);
            
            // Tabelle
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(1, 1));
            UIManager.put("Table.gridColor", new Color(230, 230, 230));

        } catch (Exception e) {
            System.err.println("Impossibile caricare FlatLaf: " + e.getMessage());
        }
    }

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

            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
            scrollPane.setWheelScrollingEnabled(true);

            table.addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    JScrollBar vertical = scrollPane.getVerticalScrollBar();
                    if (vertical != null && vertical.isVisible()) {
                        int units = e.getUnitsToScroll();
                        int increment = vertical.getUnitIncrement() * units;
                        vertical.setValue(vertical.getValue() + increment);
                    } else {
                        table.getParent().dispatchEvent(e);
                    }
                }
            });
        }

        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(220, 220, 220));
    }
}
