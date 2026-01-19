package gui;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
import gui.util.Utility;
import modello.Account;
import modello.Ruolo;
import sessione.SessioneManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ModificaIssueSeleziona extends BaseFrame {

    public enum Provenienza {
        HOME,
        DASHBOARD
    }

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
    private JScrollPane dashboardScroll;

    private Controller controller;
    private Provenienza provenienza;

    public ModificaIssueSeleziona() {
        this(Provenienza.DASHBOARD);
    }

    public ModificaIssueSeleziona(Provenienza provenienza) {
        super();
        this.provenienza = provenienza;
        controller = Controller.getInstance();

        setContentPane(mainPanel);
        setTitle("Seleziona Issue da Modificare");
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

        StyleManager.styleTable(dashboardTable, dashboardScroll);

        caricaDatiAsincrono();

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (provenienza == Provenienza.DASHBOARD) {
                    new Dashboard();
                } else {
                    Account utenteCorrente = SessioneManager.getInstance().getUtenteCorrente();
                    Utility.redirectByRole(utenteCorrente);
                }
                dispose();
            }
        });

        dashboardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    apriModifica();
                }
            }
        });
    }

    private void caricaDatiAsincrono() {
        showLoading();
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
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
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ModificaIssueSeleziona.this, "Errore caricamento dati: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void apriModifica() {
        int row = dashboardTable.getSelectedRow();
        if (row != -1) {
            Object idObj = dashboardTable.getValueAt(row, 0);
            Long issueId = null;
            if (idObj instanceof Number) {
                issueId = ((Number) idObj).longValue();
            }
            
            String creatoreUsername = dashboardTable.getValueAt(row, 4).toString();
            Account utenteCorrente = controller.getUtenteCorrente();

            if (!utenteCorrente.getNomeUtente().equals(creatoreUsername) && 
                utenteCorrente.getRuolo() != Ruolo.AMMINISTRATORE) {
                JOptionPane.showMessageDialog(
                        ModificaIssueSeleziona.this,
                        "Non sei autorizzato a modificare questa issue.\nPuoi modificare solo le issue che hai creato.",
                        "Accesso negato",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            ModificaIssue.Provenienza provModifica = (provenienza == Provenienza.DASHBOARD) 
                    ? ModificaIssue.Provenienza.DASHBOARD 
                    : ModificaIssue.Provenienza.HOME;
            
            new ModificaIssue(issueId, provModifica);
            dispose();
        }
    }
}
