package gui.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TableUtils {

    public static void popolaTabella(JTable tabella, List<Map<String, Object>> dati, String[] colonne) {
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Map<String, Object> riga : dati) {
            Object[] rigaData = new Object[colonne.length];
            for (int i = 0; i < colonne.length; i++) {
                rigaData[i] = riga.get(colonne[i].toLowerCase());
            }
            model.addRow(rigaData);
        }

        tabella.setModel(model);
    }

    public static void popolaTabellaAccount(JTable tabella, List<Map<String, Object>> dati) {
        String[] colonne = {"nomeutente", "nome", "cognome", "email"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Map<String, Object> riga : dati) {
            model.addRow(new Object[]{
                    riga.get("nomeutente"),
                    riga.get("nome"),
                    riga.get("cognome"),
                    riga.get("email")
            });
        }

        tabella.setModel(model);
    }

    public static void popolaTabellaIssue(JTable dashboardTable, List<Map<String, Object>> dati) {
        String[] colonne = {"ID", "Titolo", "Priorità", "Tipo", "Creatore", "Assegnatario", "Data Creazione", "Risolto"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Map<String, Object> riga : dati) {
            model.addRow(new Object[]{
                    riga.get("id"),
                    riga.get("titolo"),
                    riga.get("priorita"),
                    riga.get("tipo"),
                    riga.get("creatoreusername"),
                    riga.get("assegnatariousername"),
                    DateUtils.formattaData(riga.get("datacreazione")),
                    (Boolean) riga.get("risolto") ? "Sì" : "No"
            });
        }
        dashboardTable.setModel(model);
    }

    public static void impostaLarghezzeColonne(JTable table, int... larghezze) {
        if (larghezze == null || larghezze.length == 0) {
            return;
        }

        for (int i = 0; i < larghezze.length && i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(larghezze[i]);
        }
    }

    public static void impostaColorazioneRisolto(JTable table) {
        int colonnaRisolto = -1;

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equalsIgnoreCase("Risolto")) {
                colonnaRisolto = i;
                break;
            }
        }

        if (colonnaRisolto == -1) return;

        final int colonna = colonnaRisolto;

        table.getColumnModel().getColumn(colonna).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus,
                                                                    int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value != null) {
                    String valoreTesto = value.toString();
                    if (valoreTesto.equalsIgnoreCase("Sì")) {
                        c.setForeground(new java.awt.Color(0, 150, 0));
                    } else if (valoreTesto.equalsIgnoreCase("No")) {
                        c.setForeground(java.awt.Color.RED);
                    }
                }

                return c;
            }
        });
    }

    public static void selezionaRigaTabella(JTable tabella, JTextField field, int colonnaIndice) {
        tabella.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabella.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    Object valore = tabella.getValueAt(row, colonnaIndice);
                    field.setText(valore != null ? valore.toString() : "");
                }
            }
        });
    }
}
