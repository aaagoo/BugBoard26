package gui;

import controller.Controller;
import gui.util.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
        Utility.caricaDatiUtenti(utentiTable, amministratoriTable, controller);
        Utility.selezionaUtenteECaricaDati(utentiTable, controller, nomeUtenteFIeld, nomeFIeld, cognomeFIeld, emailField, passwordField, ripPasswordField, imageLabel, this);
        Utility.selezionaUtenteECaricaDati(amministratoriTable, controller, nomeUtenteFIeld, nomeFIeld, cognomeFIeld, emailField, passwordField, ripPasswordField, imageLabel, this);


        TableColumn column1 = utentiTable.getColumnModel().getColumn(3);
        column1.setPreferredWidth(200);
        TableColumn column2 = amministratoriTable.getColumnModel().getColumn(3);
        column2.setPreferredWidth(200);

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

                String messaggio = controller.modificaAccount(nomeUtente, password, nome, cognome, email, avatarSelezionato);

                if (messaggio.contains("successo")) {
                    JOptionPane.showMessageDialog(null, messaggio, "Successo", JOptionPane.INFORMATION_MESSAGE);
                    nomeUtenteFIeld.setText("");
                    nomeFIeld.setText("");
                    cognomeFIeld.setText("");
                    emailField.setText("");
                    passwordField.setText("");
                    ripPasswordField.setText("");
                    avatarSelezionato = "user.png";
                    Utility.caricaAvatar(imageLabel, "user.png", 200, 200);
                } else {
                    JOptionPane.showMessageDialog(null, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    public void setAvatarSelezionato(String avatar) {
        this.avatarSelezionato = avatar;
    }
}
