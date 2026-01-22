package gui.swing;

import controller.Controller;
import gui.util.*;
import modello.Account;
import sessione.SessioneManager;

import javax.swing.*;

public class ModificaPersonale extends BaseFrame {
    private JPanel utentePanel;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel datiPanel;
    private JPanel buttonPanel;
    private JButton indietroButton;
    private JButton modificaButton;
    private JPanel mainPanel;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField passwordField;
    private JButton modificaAvatarButton;
    private JTextField repPasswordFIeld;
    private String avatarSelezionato;

    public ModificaPersonale() {
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

        caricaDatiUtente();

        modificaAvatarButton.addActionListener(e -> {
            String selected = Utility.scegliAvatar(this, avatarSelezionato);
            if (selected != null) {
                avatarSelezionato = selected;
                Utility.caricaAvatar(imageLabel, selected, 220, 220);
            }
        });

        modificaButton.addActionListener(e -> salvaModifiche());

        indietroButton.addActionListener(e -> {
            new AreaPersonale();
            dispose();
        });
    }

    private void caricaDatiUtente() {
        Account utente = SessioneManager.getInstance().getUtenteCorrente();
        if (utente != null) {
            usernameField.setText(utente.getNomeUtente());
            passwordField.setText(utente.getPassword());
            repPasswordFIeld.setText(utente.getPassword());
            nomeField.setText(utente.getNome());
            cognomeField.setText(utente.getCognome());
            emailField.setText(utente.getEmail());
            avatarSelezionato = utente.getAvatar();
            Utility.caricaAvatar(imageLabel, utente.getAvatar(), 220, 220);
        }
    }

    private void salvaModifiche() {
        String nomeUtente = usernameField.getText();
        String password = passwordField.getText();
        String repPassword = repPasswordFIeld.getText();
        String nome = nomeField.getText();
        String cognome = cognomeField.getText();
        String email = emailField.getText();

        if (!password.isEmpty() && !password.equals(repPassword)) {
            JOptionPane.showMessageDialog(this, "Le password non coincidono");
            return;
        }

        String messaggio = Controller.getInstance().modificaAccount(nomeUtente, password, nome, cognome, email, avatarSelezionato);

        if (messaggio.contains("successo")) {
            JOptionPane.showMessageDialog(this, messaggio);
            new AreaPersonale();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, messaggio);
        }
    }
}
