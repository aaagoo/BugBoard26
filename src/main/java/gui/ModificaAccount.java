package gui;

import controller.Controller;
import gui.util.*;
import modello.Account;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JScrollPane utentiScrollPane;
    private JScrollPane amministratoriScrollPane;
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

        StyleManager.styleTable(utentiTable, utentiScrollPane);
        StyleManager.styleTable(amministratoriTable, amministratoriScrollPane);

        caricaDatiAsincrono();

        aggiungiListenerSelezione(utentiTable);
        aggiungiListenerSelezione(amministratoriTable);

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

    private void aggiungiListenerSelezione(JTable tabella) {
        tabella.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = tabella.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    String nomeUtente = tabella.getValueAt(row, 0).toString();

                    Account utente = controller.getUtenteByNomeUtente(nomeUtente);

                    if (utente != null) {
                        nomeUtenteFIeld.setText(utente.getNomeUtente());
                        nomeFIeld.setText(utente.getNome());
                        cognomeFIeld.setText(utente.getCognome());
                        emailField.setText(utente.getEmail());
                        passwordField.setText(utente.getPassword());
                        ripPasswordField.setText(utente.getPassword());
                        Utility.caricaAvatar(imageLabel, utente.getAvatar(), 200, 200);
                        setAvatarSelezionato(utente.getAvatar());
                    }
                }
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
                    get(); // Controlla eccezioni
                    Utility.popolaTabellaAccount(utentiTable, utenti);
                    Utility.popolaTabellaAccount(amministratoriTable, admin);
                    
                    Utility.impostaLarghezzeColonne(utentiTable, 100, 100, 100, 200);
                    Utility.impostaLarghezzeColonne(amministratoriTable, 100, 100, 100, 200);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ModificaAccount.this, "Errore caricamento dati: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public void setAvatarSelezionato(String avatar) {
        this.avatarSelezionato = avatar;
    }
}
