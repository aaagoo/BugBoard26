package gui.swing;

import controller.Controller;
import gui.util.RoundedPanel;
import gui.util.Utility;
import modello.Account;
import modello.Ruolo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class AzioniIssue extends JDialog {

    public enum Provenienza {
        HOME,
        DASHBOARD
    }

    private JPanel mainPanel;
    private JButton visualizzaButton;
    private JButton indietroButton;
    private JButton eliminaButton;
    private JButton modificaButton;
    private JPanel containerPanel;
    private JPanel buttonsPanel;
    private JLabel testoLabel;

    private Long issueId;
    private Provenienza provenienza;
    private Runnable onUpdate;

    public AzioniIssue(JFrame owner, Long issueId, Provenienza provenienza, Runnable onUpdate) {
        super(owner, "Azioni Issue", true);
        this.issueId = issueId;
        this.provenienza = provenienza;
        this.onUpdate = onUpdate;

        setContentPane(mainPanel);
        setSize(320, 400);
        setLocationRelativeTo(owner);
        setResizable(false);
        
        Image icon = Utility.getIconaApplicazione();
        if (icon != null) {
            setIconImage(icon);
        }

        containerPanel.setBorder(new RoundedPanel("pannello"));
        buttonsPanel.setBorder(new RoundedPanel("finestra"));

        testoLabel.setText("Issue ID: " + issueId);

        visualizzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                owner.dispose(); 
                new VisualizzaIssue(issueId,
                    provenienza == Provenienza.DASHBOARD ? VisualizzaIssue.Provenienza.DASHBOARD : VisualizzaIssue.Provenienza.HOME);
                dispose();
            }
        });

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkPermessi()) {
                    owner.dispose();
                    new ModificaIssue(issueId, provenienza == Provenienza.DASHBOARD ? ModificaIssue.Provenienza.DASHBOARD : ModificaIssue.Provenienza.HOME);
                    dispose();
                }
            }
        });

        eliminaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkPermessi()) {
                    int conferma = JOptionPane.showConfirmDialog(AzioniIssue.this, 
                            "Sei sicuro di voler eliminare questa issue?", 
                            "Conferma Eliminazione", 
                            JOptionPane.YES_NO_OPTION);
                    
                    if (conferma == JOptionPane.YES_OPTION) {
                        eliminaIssueAsincrono();
                    }
                }
            }
        });

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        setVisible(true);
    }

    private boolean checkPermessi() {
        try {
            Map<String, Object> issue = Controller.getInstance().getIssueById(issueId);
            String creatore = (String) issue.get("creatoreusername");
            Account utente = Controller.getInstance().getUtenteCorrente();
            
            if (!utente.getNomeUtente().equals(creatore) && utente.getRuolo() != Ruolo.AMMINISTRATORE) {
                JOptionPane.showMessageDialog(this, "Non hai i permessi per questa azione.", "Accesso Negato", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void eliminaIssueAsincrono() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return Controller.getInstance().eliminaIssue(issueId);
            }

            @Override
            protected void done() {
                try {
                    String risultato = get();
                    if (risultato.contains("successo")) {
                        JOptionPane.showMessageDialog(AzioniIssue.this, risultato, "Successo", JOptionPane.INFORMATION_MESSAGE);
                        if (onUpdate != null) onUpdate.run();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(AzioniIssue.this, risultato, "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AzioniIssue.this, "Errore: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
