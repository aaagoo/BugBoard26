package gui.util;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;
import controller.Controller;
import java.sql.SQLException;

public class TabellaUtility {

    public static void popolaTabella(JTable tabella, List<Map<String, Object>> dati, String[] colonne) {
        DefaultTableModel model = new DefaultTableModel(colonne, 0);

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
        String[] colonne = {"nomeUtente", "nome", "cognome", "email"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0);

        for (Map<String, Object> riga : dati) {
            model.addRow(new Object[]{
                    riga.get("nomeUtente"),
                    riga.get("nome"),
                    riga.get("cognome"),
                    riga.get("email")
            });
        }

        tabella.setModel(model);
    }

    public static void caricaDatiUtenti(JTable utentiTable, JTable amministratoriTable, Controller controller) {
        try {
            popolaTabellaAccount(utentiTable, controller.getUtenti());
            popolaTabellaAccount(amministratoriTable, controller.getAmministratori());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
