package gui;

import controller.Controller;
import gui.util.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;


public class ModificaAccount extends BaseFrame {

    private JPanel mainPanel;
    private JPanel datiPanel;
    private JPanel buttonsPanel;
    private JButton annullaButton;
    private JButton modificaButton;
    private JPanel tablePanel;
    private JPanel utentiPanel;
    private JTable utentiTable;
    private JPanel amministratoriPanel;
    private JTable amministratoriTable;
    private JTextField nomeFIeld;
    private JTextField nomeUtenteFIeld;
    private JTextField passwordField;
    private JTextField cognomeFIeld;
    private JTextField ripPasswordField;
    private JTextField emailField;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JButton modificaAvatarButton;
    private JPanel fieldPanel;
    private String avatarSelezionato;

    private Controller controller;

    public ModificaAccount() {
        super();
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        buttonsPanel.setBorder(new gui.util.RoundedPanel("pannello"));
        datiPanel.setBorder(new gui.util.RoundedPanel("pannello"));
        utentiPanel.setBorder(new gui.util.RoundedPanel("finestra"));
        amministratoriPanel.setBorder(new gui.util.RoundedPanel("finestra"));
        tablePanel.setBorder(new gui.util.RoundedPanel("pannello"));
        fieldPanel.setBorder(new gui.util.RoundedPanel("finestra"));
        imagePanel.setBorder(new gui.util.RoundedPanel("finestra"));

        controller = Controller.getInstance();
        
        // Caricamento asincrono dei dati
        caricaDatiAsincrono();

        Utility.selezionaUtenteECaricaDati(utentiTable, controller, nomeUtenteFIeld, nomeFIeld, cognomeFIeld, emailField, passwordField, ripPasswordField, imageLabel, this);
        Utility.selezionaUtenteECaricaDati(amministratoriTable, controller, nomeUtenteFIeld, nomeFIeld, cognomeFIeld, emailField, passwordField, ripPasswordField, imageLabel, this);

        avatarSelezionato = "user.png";
        Utility.caricaAvatar(imageLabel, "user.png", 200, 200);

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestisciUtenti();
                dispose();
            }
        });

        modificaAvatarButton.addActionListener(e -> {
            String selected = Utility.scegliAvatar(this, avatarSelezionato);
            if (selected != null) {
                avatarSelezionato = selected;
                Utility.caricaAvatar(imageLabel, selected, 200, 200);
            }
        });

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeUtente = nomeUtenteFIeld.getText().trim();
                String password = passwordField.getText();
                String repPassword = ripPasswordField.getText();
                String nome = nomeFIeld.getText().trim();
                String cognome = cognomeFIeld.getText().trim();
                String email = emailField.getText().trim();

                if (nomeUtente.isEmpty() || nome.isEmpty() || cognome.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Compila tutti i campi obbligatori", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.isEmpty() && !password.equals(repPassword)) {
                    JOptionPane.showMessageDialog(null, "Le password non corrispondono", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                showLoading();

                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        return controller.modificaAccount(nomeUtente, password, nome, cognome, email, avatarSelezionato);
                    }

                    @Override
                    protected void done() {
                        hideLoading();
                        try {
                            String messaggio = get();
                            if (messaggio.contains("successo")) {
                                JOptionPane.showMessageDialog(ModificaAccount.this, messaggio, "Successo", JOptionPane.INFORMATION_MESSAGE);
                                nomeUtenteFIeld.setText("");
                                nomeFIeld.setText("");
                                cognomeFIeld.setText("");
                                emailField.setText("");
                                passwordField.setText("");
                                ripPasswordField.setText("");
                                avatarSelezionato = "user.png";
                                Utility.caricaAvatar(imageLabel, "user.png", 200, 200);
                                // Ricarica la tabella per mostrare le modifiche
                                caricaDatiAsincrono();
                            } else {
                                JOptionPane.showMessageDialog(ModificaAccount.this, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            JOptionPane.showMessageDialog(ModificaAccount.this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
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
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Nota: Utility.caricaDatiUtenti chiama il controller che fa chiamate di rete.
                // Tuttavia, Utility.caricaDatiUtenti aggiorna anche la GUI (popolaTabellaAccount).
                // Questo non è ideale in doInBackground, ma Utility usa DefaultTableModel che è thread-safe per addRow?
                // No, Swing non è thread-safe.
                // Per farlo bene, dovrei separare il fetch dei dati dall'aggiornamento della tabella,
                // come abbiamo fatto per Dashboard.
                // Ma Utility.caricaDatiUtenti è un metodo void che fa tutto.
                // Per ora lo lascio qui, ma sappi che tecnicamente viola le regole Swing.
                // Se vuoi, posso rifattorizzare Utility.caricaDatiUtenti.
                // Dato che funziona in Dashboard (dove abbiamo separato), qui potremmo avere problemi se non separiamo.
                
                // Soluzione rapida: chiamiamo controller direttamente qui e usiamo Utility.popolaTabellaAccount in done()
                // Ma Utility.caricaDatiUtenti fa due chiamate (utenti e admin).
                
                // Facciamo così: invochiamo invokeLater dentro doInBackground per le parti GUI se usiamo Utility così com'è? No.
                // Meglio separare.
                return null;
            }

            @Override
            protected void done() {
                // Eseguo il caricamento qui nel thread EDT per sicurezza, avvolto dal loading.
                // Poiché non ho rifattorizzato Utility per separare i dati dalla view per gli account,
                // e non voglio rompere tutto ora, lo eseguo qui.
                // Bloccherà l'EDT per il tempo della richiesta, ma almeno c'è lo spinner (che però si bloccherà).
                // Per avere lo spinner fluido, DEVO rifattorizzare Utility o copiare la logica qui.
                
                // Rifaccio la logica qui per essere asincrono vero:
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Scarica dati
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
        };
        // worker.execute(); // Questo era il vecchio approccio.
        
        // Avvio diretto del worker interno
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

    public void setAvatarSelezionato(String avatar) {
        this.avatarSelezionato = avatar;
    }
}
