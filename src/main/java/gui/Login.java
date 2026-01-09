package gui;

import controller.Controller;
import gui.util.*;
import modello.Utente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setTitle("Login");
        setSize(1000,500);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        credentialsPanel.setBorder(new RoundedPanel("pannello"));

        Utility.caricaImmagine(imageLabel, "images/img_login.jpeg", 500, 500);

        accediButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nomeUtente = userField.getText();
                String password = new String(pswField.getPassword());

                if (nomeUtente.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Per favore, compila tutti i campi.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
                Utente utente = Controller.getInstance().login(nomeUtente, password);

                if (utente == null) {
                    JOptionPane.showMessageDialog(Login.this,
                            "Credenziali non valide",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Utility.redirectByRole(utente);
                dispose();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });

    }
}



