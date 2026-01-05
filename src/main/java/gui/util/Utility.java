package gui.util;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;
import controller.Controller;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.Image;
import java.io.File;


public class Utility {

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

    public static void caricaAvatar(JLabel label, String avatarName, int width, int height) {
        try {
            String resourcePath = "/images/profileIcons/" + avatarName;
            ImageIcon imageIcon = new ImageIcon(Utility.class.getResource(resourcePath));

            if (imageIcon.getImage() == null) {
                System.err.println("Avatar non trovato: " + resourcePath);
                return;
            }

            Image image = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getAvatarFiles() {
        try {
            File avatarDir = new File(Utility.class.getResource("/images/profileIcons/").toURI());
            File[] files = avatarDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

            if (files != null) {
                String[] names = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    names[i] = files[i].getName();
                }
                return names;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }


    public static String scegliAvatar(JFrame parent, String avatarCorrente) {
        String[] avatarFiles = getAvatarFiles();

        if (avatarFiles.length == 0) {
            JOptionPane.showMessageDialog(parent, "Nessun avatar trovato");
            return null;
        }

        return (String) JOptionPane.showInputDialog(
                parent,
                "Seleziona un avatar:",
                "Scegli Avatar",
                JOptionPane.QUESTION_MESSAGE,
                null,
                avatarFiles,
                avatarCorrente
        );
    }


}
