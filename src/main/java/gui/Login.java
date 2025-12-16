package gui;

import controller.Controller;
import modello.Utente;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Login extends JFrame {
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
    private JPanel mainpanel;


    public Login() {
        setContentPane(mainpanel);
        setTitle("Login");
        setSize(1000,500);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        ImageIcon imageIcon = new ImageIcon(getClass().getClassLoader().getResource("images/img_login.jpeg"));
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        imageLabel.setIcon(imageIcon);


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

                new Home();
                dispose();
            }
        });
    }




}



