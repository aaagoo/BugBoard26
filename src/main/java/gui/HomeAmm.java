package gui;

import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.Utility;
import modello.Utente;
import sessione.SessioneManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeAmm extends BaseFrame {

    private JPanel topPanel;
    private JPanel infoutentePanel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JLabel ruoloLabel;
    private JPanel midPanel;
    private JPanel operationsPanel;
    private JButton gestisciUtentiButton;
    private JPanel dashboardPanel;
    private JTable dashboardTable;
    private JPanel botPanel;
    private JButton disconnettitiButton;
    private JPanel mainPanel;

    public HomeAmm() {
        super();
        setContentPane(mainPanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        midPanel.setBorder(new RoundedPanel("pannello"));
        infoutentePanel.setBorder(new RoundedPanel("pannello"));
        botPanel.setBorder(new RoundedPanel("pannello"));
        operationsPanel.setBorder(new RoundedPanel("finestra"));
        dashboardPanel.setBorder(new RoundedPanel("finestra"));


        Utente utente = SessioneManager.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
        }

        disconnettitiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login();
                dispose();
            }
        });

        gestisciUtentiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestisciUtenti();
                dispose();
            }
        });
    }

}
