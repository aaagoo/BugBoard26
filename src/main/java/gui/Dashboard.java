package gui;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.Utility;
import modello.Account;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Dashboard extends BaseFrame {
    private JPanel botPanel;
    private JButton indietroButton;
    private JPanel midPanel;
    private JPanel dashboardPanel;
    private JTable dashboardTable;
    private JPanel topPanel;
    private JPanel infoutentePanel;
    private JPanel mainPanel;
    private JLabel userpngLabel;
    private JLabel ruoloLabel;
    private JLabel benvenutoLabel;
    private JButton eliminaButton;

    private Controller controller;

    public Dashboard() {
        super();
        controller = Controller.getInstance();

        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        midPanel.setBorder(new RoundedPanel("pannello"));
        infoutentePanel.setBorder(new RoundedPanel("pannello"));
        botPanel.setBorder(new RoundedPanel("pannello"));
        dashboardPanel.setBorder(new RoundedPanel("finestra"));

        Account utente = Controller.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
        }

        dashboardTable.getTableHeader().setReorderingAllowed(false);
        dashboardTable.getTableHeader().setResizingAllowed(false);
        JScrollPane scrollPane = (JScrollPane) dashboardTable.getParent().getParent();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBorder(null);

        caricaDatiAsincrono();

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUtente();
                dispose();
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EliminaIssueUtente();
                dispose();
            }
        });
    }

    private void caricaDatiAsincrono() {
        showLoading();

        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return controller.getAllIssues();
            }

            @Override
            protected void done() {
                hideLoading();
                try {
                    List<Map<String, Object>> dati = get();

                    Utility.popolaTabellaIssue(dashboardTable, dati);
                    Utility.impostaColorazioneRisolto(dashboardTable);
                    Utility.impostaLarghezzeColonne(dashboardTable, 15, 100, 20, 40, 60, 60, 60, 20);
                    
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(Dashboard.this, 
                            "Errore nel caricamento dei dati: " + ex.getMessage(), 
                            "Errore", 
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
