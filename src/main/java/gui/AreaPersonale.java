package gui;

import gui.util.*;
import sessione.SessioneManager;
import modello.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AreaPersonale extends BaseFrame {
    private JPanel mainPanel;
    private JButton indietroButton;
    private JPanel utentePanel;
    private JPanel imagePanel;
    private JLabel imageLabel;
    private JPanel datiPanel;
    private JLabel nomeLabel;
    private JLabel cognomeLabel;
    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JPanel buttonPanel;
    private JButton modificaButton;


    public AreaPersonale() {
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


        Account utente = SessioneManager.getInstance().getUtenteCorrente();
        if (utente != null) {
            nomeLabel.setText(utente.getNome());
            cognomeLabel.setText(utente.getCognome());
            usernameLabel.setText(utente.getNomeUtente());
            emailLabel.setText(utente.getEmail());

            String avatarName = utente.getAvatar();
            Utility.caricaAvatar(imageLabel, avatarName, 220, 220);
        }

        indietroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HomeUtente();
                dispose();
            }
        });

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ModificaPersonale();
                dispose();
            }
        });
    }
}
