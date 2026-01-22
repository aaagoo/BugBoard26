package gui.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import gui.util.*;
import controller.Controller;
import modello.Account;
import sessione.SessioneManager;

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
    private JScrollPane utentiScrollPane;
    private JScrollPane amministratoriScrollPane;

    private Controller controller;

    public EliminaAccount() {
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
        buttonsPanel.setBorder(new RoundedPanel("pannello"));

        controller = Controller.getInstance();
        
        StyleManager.styleTable(utentiTable, utentiScrollPane);
        StyleManager.styleTable(amministratoriTable, amministratoriScrollPane);
        
        caricaDatiAsincrono();

        Utility.selezionaRigaTabella(utentiTable, nomeUtenteField, 0);
        Utility.selezionaRigaTabella(amministratoriTable, nomeUtenteField, 0);

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
                String nomeUtenteDaEliminare = nomeUtenteField.getText().trim();

                if (nomeUtenteDaEliminare.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Inserisci un nome account", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int conferma = JOptionPane.showConfirmDialog(EliminaAccount.this, 
                        "Sei sicuro di voler eliminare l'account " + nomeUtenteDaEliminare + "?", 
                        "Conferma Eliminazione", 
                        JOptionPane.YES_NO_OPTION);
                
                if (conferma != JOptionPane.YES_OPTION) {
                    return;
                }

                showLoading();

                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        return controller.eliminaAccount(nomeUtenteDaEliminare);
                    }

                    @Override
                    protected void done() {
                        hideLoading();
                        try {
                            String risultato = get();
                            
                            if (risultato.contains("successo")) {
                                JOptionPane.showMessageDialog(EliminaAccount.this, risultato, "Successo", JOptionPane.INFORMATION_MESSAGE);

                                Account utenteCorrente = SessioneManager.getInstance().getUtenteCorrente();
                                if (utenteCorrente != null && utenteCorrente.getNomeUtente().equals(nomeUtenteDaEliminare)) {
                                    SessioneManager.getInstance().logout();
                                    new Login();
                                    dispose();
                                } else {
                                    nomeUtenteField.setText("");
                                    caricaDatiAsincrono();
                                }
                            } else {
                                JOptionPane.showMessageDialog(EliminaAccount.this, risultato, "Errore", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            JOptionPane.showMessageDialog(EliminaAccount.this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();
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
                    JOptionPane.showMessageDialog(EliminaAccount.this, "Errore caricamento dati: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}
