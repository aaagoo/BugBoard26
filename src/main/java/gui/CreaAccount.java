package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import controller.Controller;
import gui.util.*;
import modello.*;


public class CreaAccount extends BaseFrame {

    private JPanel mainPanel;
    private JTextField nomeUtenteField;
    private JButton annullaButton;
    private JButton creaAccountButton;
    private JTextField passwordField;
    private JTextField ripPasswordField;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField emailField;
    private JPanel utentePanel;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JButton modificaAvatarButton;
    private JPanel datiPanel;
    private JPanel buttonPanel;
    private JComboBox ruoloComboBox;
    private String avatarSelezionato;

    public CreaAccount() {
        super();
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        utentePanel.setBorder(new RoundedPanel("pannello"));
        buttonPanel.setBorder(new RoundedPanel("pannello"));
        datiPanel.setBorder(new RoundedPanel("finestra"));
        imagePanel.setBorder(new RoundedPanel("finestra"));

        for (Ruolo ruolo : Ruolo.values()) {
            ruoloComboBox.addItem(ruolo.name());
        }

        avatarSelezionato = "user.png";
        Utility.caricaAvatar(imageLabel, avatarSelezionato, 220, 220);


        modificaAvatarButton.addActionListener(e -> {
            String selected = Utility.scegliAvatar(this, avatarSelezionato);
            if (selected != null) {
                avatarSelezionato = selected;
                Utility.caricaAvatar(imageLabel, selected, 220, 220);
            }
        });

        annullaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestisciUtenti();
                dispose();
            }
        });

        creaAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeUtente = nomeUtenteField.getText().trim();
                String password = passwordField.getText();
                String ripPassword = ripPasswordField.getText();
                String nome = nomeField.getText().trim();
                String cognome = cognomeField.getText().trim();
                String email = emailField.getText().trim();
                String ruoloSelezionato = (String) ruoloComboBox.getSelectedItem();

                if (nomeUtente.isEmpty() || password.isEmpty() || nome.isEmpty() ||
                        cognome.isEmpty() || email.isEmpty() || ruoloSelezionato == null) {
                    JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(ripPassword)) {
                    JOptionPane.showMessageDialog(null, "Le password non coincidono", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                showLoading();

                SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        Controller controller = Controller.getInstance();
                        Ruolo ruolo = Ruolo.valueOf(ruoloSelezionato);
                        return controller.creaAccount(nomeUtente, password, nome, cognome, email, ruolo, avatarSelezionato);
                    }

                    @Override
                    protected void done() {
                        hideLoading();
                        try {
                            String messaggio = get();
                            if (messaggio.contains("successo")) {
                                JOptionPane.showMessageDialog(CreaAccount.this, messaggio, "Successo", JOptionPane.INFORMATION_MESSAGE);
                                nomeUtenteField.setText("");
                                passwordField.setText("");
                                ripPasswordField.setText("");
                                nomeField.setText("");
                                cognomeField.setText("");
                                emailField.setText("");
                                ruoloComboBox.setSelectedIndex(0);
                                avatarSelezionato = "user.png";
                                Utility.caricaAvatar(imageLabel, "user.png", 220, 220);
                            } else {
                                JOptionPane.showMessageDialog(CreaAccount.this, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            JOptionPane.showMessageDialog(CreaAccount.this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();
            }
        });
    }
}
