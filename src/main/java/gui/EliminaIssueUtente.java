package gui;

import controller.Controller;
import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.Utility;
import modello.Account;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EliminaIssueUtente extends BaseFrame {
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

    private Controller controller;

    public EliminaIssueUtente() {
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

        Utility.caricaDatiIssue(dashboardTable, controller);
        Utility.impostaColorazioneRisolto(dashboardTable);

        dashboardTable.getTableHeader().setReorderingAllowed(false);
        dashboardTable.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = (JScrollPane) dashboardTable.getParent().getParent();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBorder(null);

        Utility.impostaLarghezzeColonne(dashboardTable, 15, 100, 20, 40, 60, 60, 60, 20);

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dashboard();
                dispose();
            }
        });

        dashboardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = dashboardTable.getSelectedRow();
                    if (row != -1) {
                        Long issueId = Long.parseLong(dashboardTable.getValueAt(row, 0).toString());
                        String creatoreUsername = dashboardTable.getValueAt(row, 4).toString();

                        Account utenteCorrente = controller.getUtenteCorrente();

                        if (!utenteCorrente.getNomeUtente().equals(creatoreUsername)) {
                            JOptionPane.showMessageDialog(
                                    EliminaIssueUtente.this,
                                    "Non sei autorizzato a eliminare questa issue.\nPuoi eliminare solo le issue che hai creato.",
                                    "Accesso negato",
                                    JOptionPane.WARNING_MESSAGE
                            );
                            return;
                        }

                        int conferma = JOptionPane.showConfirmDialog(
                                EliminaIssueUtente.this,
                                "Sei sicuro di voler eliminare l'issue:\n" +
                                        dashboardTable.getValueAt(row, 1) + "?",
                                "Conferma eliminazione",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                        if (conferma == JOptionPane.YES_OPTION) {
                            String risultato = controller.eliminaIssue(issueId);

                            if (risultato.contains("successo")) {
                                JOptionPane.showMessageDialog(
                                        EliminaIssueUtente.this,
                                        risultato,
                                        "Successo",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                Utility.caricaDatiIssue(dashboardTable, controller);
                            } else {
                                JOptionPane.showMessageDialog(
                                        EliminaIssueUtente.this,
                                        risultato,
                                        "Errore",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    }
                }
            }
        });
    }
}