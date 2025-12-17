package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.util.TabellaUtility;
import controller.Controller;

public class EliminaAccount extends JFrame {

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
        setContentPane(mainPanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        controller = Controller.getInstance();
        TabellaUtility.caricaDatiUtenti(utentiTable, amministratoriTable, controller);

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
                TabellaUtility.caricaDatiUtenti(utentiTable, amministratoriTable, controller);
            }
        });
    }
}
