package gui;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
import gui.util.Utility;
import modello.Account;
import sessione.SessioneManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class HomeAmm extends BaseFrame {

    private JPanel infoutentePanel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JLabel ruoloLabel;
    private JPanel midPanel;
    private JPanel operationsPanel;
    private JButton gestisciUtentiButton;
    private JPanel dashboardPanel;
    private JTable dashboardTable;
    private JPanel botPanel;
    private JButton disconnettitiButton;
    private JPanel mainPanel;
    private JPanel topPanel;
    private JButton modificaButton;
    private JButton eliminaButton;
    private JScrollPane dashboardScroll;

    public HomeAmm() {
        super();
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        midPanel.setBorder(new RoundedPanel("pannello"));
        infoutentePanel.setBorder(new RoundedPanel("pannello"));
        botPanel.setBorder(new RoundedPanel("pannello"));
        operationsPanel.setBorder(new RoundedPanel("finestra"));
        dashboardPanel.setBorder(new RoundedPanel("finestra"));

        StyleManager.styleTable(dashboardTable, dashboardScroll);

        Account utente = SessioneManager.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
        }

        dashboardTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = dashboardTable.getSelectedRow();
                    if (row != -1) {
                        Object idObj = dashboardTable.getValueAt(row, 0);
                        Long issueId = null;
                        if (idObj instanceof Number) {
                            issueId = ((Number) idObj).longValue();
                        }
                        
                        if (issueId != null) {
                            new VIsualizzaIssue(issueId, VIsualizzaIssue.Provenienza.HOME);
                            dispose();
                        }
                    }
                }
            }
        });

        caricaDatiAsincrono();

        disconnettitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login();
                dispose();
            }
        });

        gestisciUtentiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestisciUtenti();
                dispose();
            }
        });

        if (eliminaButton != null) {
            eliminaButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new EliminaIssue(EliminaIssue.Provenienza.HOME);
                    dispose();
                }
            });
        }
        
        if (modificaButton != null) {
             modificaButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ModificaIssueSeleziona(ModificaIssueSeleziona.Provenienza.HOME);
                    dispose();
                }
            });
        }
    }

    private void caricaDatiAsincrono() {
        showLoading();

        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                return Controller.getInstance().getAllIssues();
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
                    JOptionPane.showMessageDialog(HomeAmm.this, 
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
