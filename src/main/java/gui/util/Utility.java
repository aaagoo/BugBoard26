package gui.util;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;
import controller.Controller;
import javax.swing.*;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import modello.*;
import gui.HomeAmm;
import gui.HomeUtente;
import gui.ModificaAccount;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.table.TableColumn;



public class Utility {

    public static Image getIconaApplicazione() {
        try {
            java.net.URL url = Utility.class.getResource("/images/icona_bugboard.png");
            if (url != null) {
                return new ImageIcon(url).getImage();
            }
        } catch (Exception e) {
            System.err.println("Impossibile caricare l'icona dell'applicazione: " + e.getMessage());
        }
        return null;
    }

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
        String[] colonne = {"nomeutente", "nome", "cognome", "email"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0);

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

    public static void caricaDatiUtenti(JTable utentiTable, JTable amministratoriTable, Controller controller) {
        popolaTabellaAccount(utentiTable, controller.getUtenti());
        popolaTabellaAccount(amministratoriTable, controller.getAmministratori());
    }


    public static void caricaAvatar(JLabel label, String avatarName, int width, int height) {
        if (avatarName == null || avatarName.isEmpty()) {
            avatarName = "user.png";
        }

        String resourcePath = "/images/profileIcons/" + avatarName;
        var url = Utility.class.getResource(resourcePath);

        if (url == null) {
            label.setIcon(null);
            label.setText("No Img");
            return;
        }

        try {
            ImageIcon imageIcon = new ImageIcon(url);
            Image image = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(image));
            label.setText("");
        } catch (Exception e) {
            System.err.println("❌ Errore caricamento avatar: " + e.getMessage());
            e.printStackTrace();
            label.setIcon(null);
            label.setText("Error");
        }
    }


    public static String[] getAvatarFiles() {
        List<String> avatarNames = new ArrayList<>();
        String resourcePath = "/images/profileIcons/avatars.list";
        
        try (InputStream is = Utility.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    avatarNames.add(line.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Errore lettura avatars.list: " + e.getMessage());
            return new String[]{"user.png"}; 
        }
        
        return avatarNames.toArray(new String[0]);
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

    public static void redirectByRole(Account utente) {
        Ruolo ruolo = utente.getRuolo();

        if (ruolo == Ruolo.AMMINISTRATORE) {
            new HomeAmm();
        } else {
            new HomeUtente();
        }
    }

    public static void selezionaUtenteECaricaDati(JTable tabella, Controller controller,
                                                  JTextField nomeUtenteField, JTextField nomeField,
                                                  JTextField cognomeField, JTextField emailField,
                                                  JTextField passwordField, JTextField ripPasswordField,
                                                  JLabel avatarLabel, ModificaAccount modificaAccount) {
        tabella.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabella.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    String nomeUtente = tabella.getValueAt(row, 0).toString();
                    Account utente = controller.getUtenteByNomeUtente(nomeUtente);

                    if (utente != null) {
                        nomeUtenteField.setText(utente.getNomeUtente());
                        nomeField.setText(utente.getNome());
                        cognomeField.setText(utente.getCognome());
                        emailField.setText(utente.getEmail());
                        passwordField.setText(utente.getPassword());
                        ripPasswordField.setText(utente.getPassword());
                        caricaAvatar(avatarLabel, utente.getAvatar(), 200, 200);
                        modificaAccount.setAvatarSelezionato(utente.getAvatar());
                    }
                }
            }
        });
    }

    public static void caricaImmagine(JLabel label, String resourcePath, int width, int height) {
        try {
            ImageIcon imageIcon = new ImageIcon(Utility.class.getClassLoader().getResource(resourcePath));
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void caricaDatiIssue(JTable dashboardTable, Controller controller) {
        List<Map<String, Object>> dati = controller.getAllIssues();
        popolaTabellaIssue(dashboardTable, dati);
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
                    formattaData(riga.get("datacreazione")),
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

    public static void caricaIssueAssegnate(JTable dashboardTable, String nomeUtente, Controller controller) {
        List<Map<String, Object>> dati = controller.getIssueAssegnate(nomeUtente);
        String[] colonne = {"ID", "Titolo", "Priorità", "Tipo", "Creatore", "Data Creazione"};
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
                    formattaData(riga.get("datacreazione"))
            });
        }
        dashboardTable.setModel(model);
    }

    public static void caricaDatiIssueById(Long issueId, JLabel idLabel, JTextField titoloField,
                                           JTextArea descrizioneArea, JLabel prioritaLabel,
                                           JLabel tipoLabel, JLabel risoltoLabel, JLabel creatoreLabel,
                                           JLabel dataCreazioneLabel, JLabel dataRisoluzioneLabel,
                                           JFrame parent) {
        try {
            Map<String, Object> issue = Controller.getInstance().getIssueById(issueId);

            idLabel.setText("ID: " + issue.get("id"));
            titoloField.setText((String) issue.get("titolo"));
            descrizioneArea.setText((String) issue.get("descrizione"));
            prioritaLabel.setText("Priorità: " + issue.get("priorita"));
            tipoLabel.setText("Tipo: " + issue.get("tipo"));
            risoltoLabel.setText("Risolto: " + ((Boolean) issue.get("risolto") ? "Sì" : "No"));
            creatoreLabel.setText("Creatore: " + issue.get("creatoreusername"));

            dataCreazioneLabel.setText("Data Creazione: " + formattaData(issue.get("datacreazione")));
            dataRisoluzioneLabel.setText("Data Risoluzione: " + formattaData(issue.get("datarisoluzione")));

            titoloField.setEditable(false);
            descrizioneArea.setEditable(false);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Errore nel caricamento dell'issue: " + e.getMessage());
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

    private static String formattaData(Object dataObj) {
        if (dataObj == null) {
            return "N/A";
        }

        try {
            String dataStr = dataObj.toString();
            if (dataStr.contains(".")) {
                dataStr = dataStr.substring(0, dataStr.indexOf('.'));
            }
            LocalDateTime dateTime = LocalDateTime.parse(dataStr);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            return dataObj.toString();
        }
    }
}