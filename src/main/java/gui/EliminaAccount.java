package gui;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.util.*;
import controller.Controller;

public class EliminaAccount extends BaseFrame {

    private JPanel tablePanel;
    private JPanel utentiPanel;
    private JTable utentiTable;
    private JPanel amministratoriPanel;
    private JTable amministratoriTable;
    private JPanel operationsPanel;
    private JTextField nomeUtenteField;
    private JPanel buttonsPanel;
    private JButton annullaButton;
    private JButton eliminaButton;
    private JPanel mainPanel;

    private Controller controller;

    public EliminaAccount() {
        super();
        setContentPane(mainPanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        tablePanel.setBorder(new RoundedPanel("pannello"));
        utentiPanel.setBorder(new RoundedPanel("finestra"));
        amministratoriPanel.setBorder(new RoundedPanel("finestra"));
        operationsPanel.setBorder(new RoundedPanel("pannello"));
        buttonsPanel.setBorder(new RoundedPanel("pannello"));

        controller = Controller.getInstance();
        Utility.caricaDatiUtenti(utentiTable, amministratoriTable, controller);

        Utility.caricaDatiUtenti(utentiTable, amministratoriTable, controller);
        Utility.selezionaRigaTabella(utentiTable, nomeUtenteField, 0);
        Utility.selezionaRigaTabella(amministratoriTable, nomeUtenteField, 0);

        TableColumn column1 = utentiTable.getColumnModel().getColumn(3);
        column1.setPreferredWidth(200);
        TableColumn column2 = amministratoriTable.getColumnModel().getColumn(3);
        column2.setPreferredWidth(200);

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestisciUtenti();
                dispose();
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeUtente = nomeUtenteField.getText().trim();

                if (nomeUtente.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Inserisci un nome account", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String risultato = controller.eliminaAccount(nomeUtente);
                JOptionPane.showMessageDialog(null, risultato, "Risultato", JOptionPane.INFORMATION_MESSAGE);

                nomeUtenteField.setText("");
                Utility.caricaDatiUtenti(utentiTable, amministratoriTable, controller);
            }
        });
    }
}
