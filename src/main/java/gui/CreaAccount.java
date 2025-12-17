package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.Controller;
import modello.Ruolo;


public class CreaAccount extends JFrame {

    private JPanel mainPanel;
    private JPanel operationsPanel;
    private JTextField nomeUtenteField;
    private JButton annullaButton;
    private JPanel buttonsPanel;
    private JButton creaAccountButton;
    private JButton cambiaAvatarButton;
    private JLabel imageLabel;
    private JPanel avatarPanel;
    private JTextField passwordField;
    private JTextField ripPasswordField;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField emailField;
    private JComboBox ruoloBox;

    public CreaAccount() {
        setContentPane(mainPanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        for (Ruolo ruolo : Ruolo.values()) {
            ruoloBox.addItem(ruolo.name());
        }

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
                String ruoloSelezionato = (String) ruoloBox.getSelectedItem();

                if (nomeUtente.isEmpty() || password.isEmpty() || nome.isEmpty() ||
                        cognome.isEmpty() || email.isEmpty() || ruoloSelezionato == null) {
                    JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(ripPassword)) {
                    JOptionPane.showMessageDialog(null, "Le password non coincidono", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Controller controller = Controller.getInstance();
                Ruolo ruolo = Ruolo.valueOf(ruoloSelezionato);

                String messaggio = controller.creaAccount(nomeUtente, password, nome, cognome, email, ruolo);

                if (messaggio.contains("successo")) {
                    JOptionPane.showMessageDialog(null, messaggio, "Successo", JOptionPane.INFORMATION_MESSAGE);
                    nomeUtenteField.setText("");
                    passwordField.setText("");
                    ripPasswordField.setText("");
                    nomeField.setText("");
                    cognomeField.setText("");
                    emailField.setText("");
                    ruoloBox.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(null, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }
}
