package gui.util;

import com.formdev.flatlaf.FlatLightLaf;
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

            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.intercellSpacing", new Dimension(1, 1));
            UIManager.put("Table.gridColor", new Color(230, 230, 230)); // Grigio chiaro

        } catch (Exception e) {
            System.err.println("Impossibile caricare FlatLaf: " + e.getMessage());
        }
    }

    /**
     * Applica lo stile standard alle tabelle del progetto.
     */
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
            
            // Aumenta velocità scroll
            scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        }
        
        // Tabella
        table.setFillsViewportHeight(true);
        table.setShowGrid(true); // Forza la griglia
        table.setGridColor(new Color(220, 220, 220));
    }
}
