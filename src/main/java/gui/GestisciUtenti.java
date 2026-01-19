package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import controller.Controller;
import gui.util.BaseFrame;
import gui.util.Utility;
import gui.util.RoundedPanel;
import javax.swing.table.TableColumn;

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
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                java.util.List<java.util.Map<String, Object>> utenti = controller.getUtenti();
                java.util.List<java.util.Map<String, Object>> admin = controller.getAmministratori();
                
                SwingUtilities.invokeLater(() -> {
                    Utility.popolaTabellaAccount(utentiTable, utenti);
                    Utility.popolaTabellaAccount(amministratoriTable, admin);
                    
                    TableColumn column1 = utentiTable.getColumnModel().getColumn(3);
                    column1.setPreferredWidth(200);
                    TableColumn column2 = amministratoriTable.getColumnModel().getColumn(3);
                    column2.setPreferredWidth(200);
                });
                return null;
            }
            
            @Override
            protected void done() {
                hideLoading();
            }
        }.execute();
    }
}
