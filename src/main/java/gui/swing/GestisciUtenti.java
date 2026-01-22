package gui.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.Controller;
import gui.util.BaseFrame;
import gui.util.StyleManager;
import gui.util.Utility;
import gui.util.RoundedPanel;

public class GestisciUtenti extends BaseFrame {

    private JPanel mainPanel;
    private JPanel operationsPanel;
    private JButton modificaAccountButton;
    private JButton creaAccountButton;
    private JButton indietroButton;
    private JPanel tablePanel;
    private JPanel utentiPanel;
    private JTable utentiTable;
    private JPanel amministratoriPanel;
    private JTable amministratoriTable;
    private JButton eliminaButton;
    private JScrollPane utentiScrollPane;
    private JScrollPane amministratoriScrollPane;

    private Controller controller;

    public GestisciUtenti() {
        super();
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        tablePanel.setBorder(new RoundedPanel("pannello"));
        utentiPanel.setBorder(new RoundedPanel("finestra"));
        amministratoriPanel.setBorder(new RoundedPanel("finestra"));
        operationsPanel.setBorder(new RoundedPanel("pannello"));

        controller = Controller.getInstance();

        StyleManager.styleTable(utentiTable, utentiScrollPane);
        StyleManager.styleTable(amministratoriTable, amministratoriScrollPane);

        caricaDatiAsincrono();

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeAmm();
                dispose();
            }
        });

        creaAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreaAccount();
                dispose();
            }
        });

        modificaAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ModificaAccount();
                dispose();
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EliminaAccount();
                dispose();
            }
        });
    }

    private void caricaDatiAsincrono() {
        showLoading();
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            java.util.List<java.util.Map<String, Object>> utenti;
            java.util.List<java.util.Map<String, Object>> admin;

            @Override
            protected Void doInBackground() throws Exception {
                utenti = controller.getUtenti();
                admin = controller.getAmministratori();
                return null;
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    get();
                    Utility.popolaTabellaAccount(utentiTable, utenti);
                    Utility.popolaTabellaAccount(amministratoriTable, admin);
                    
                    Utility.impostaLarghezzeColonne(utentiTable, 100, 100, 100, 200);
                    Utility.impostaLarghezzeColonne(amministratoriTable, 100, 100, 100, 200);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(GestisciUtenti.this, "Errore caricamento dati: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
