package gui;

import controller.Controller;
import gui.util.*;
import modello.Account;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

public class Login extends BaseFrame {
    private JPanel textPanel;
    private JLabel title1;
    private JPanel credentialsPanel;
    private JPanel userPanel;
    private JTextField userField;
    private JLabel userLabel;
    private JPanel pswPanel;
    private JPasswordField pswField;
    private JLabel pswLabel;
    private JPanel accediPanel;
    private JButton accediButton;
    private JPanel registerPanel;
    private JPanel textArea;
    private JLabel text;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel mainPanel;


    public Login() {
        super();
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1000,500);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        credentialsPanel.setBorder(new RoundedPanel("pannello"));

        Utility.caricaImmagine(imageLabel, "images/img_login.png", 500, 500);

        accediButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eseguiLogin();
            }
        });

        userField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eseguiLogin();
            }
        });

        pswField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eseguiLogin();
            }
        });
        SwingUtilities.invokeLater(() -> userField.requestFocusInWindow());
    }

    private void eseguiLogin() {
        String nomeUtente = userField.getText();
        String password = new String(pswField.getPassword());

        if (nomeUtente.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Per favore, compila tutti i campi.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        showLoading();

        SwingWorker<Account, Void> worker = new SwingWorker<Account, Void>() {
            @Override
            protected Account doInBackground() throws Exception {
                return Controller.getInstance().login(nomeUtente, password);
            }

            @Override
            protected void done() {
                hideLoading();
                
                try {
                    Account utente = get();
                    
                    if (utente == null) {
                        JOptionPane.showMessageDialog(Login.this,
                                "Credenziali non valide",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        Utility.redirectByRole(utente);
                        dispose();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    JOptionPane.showMessageDialog(Login.this, 
                            "Errore di connessione: " + ex.getMessage(), 
                            "Errore", 
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
