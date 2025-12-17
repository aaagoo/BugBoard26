package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.util.RoundedPanel;
import sessione.SessioneManager;
import modello.Utente;

public class HomeAmm extends JFrame {
    private JPanel mainPanel;
    private JPanel infoutentePanel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JButton areaPersonaleButton;
    private JPanel operationsPanel;
    private JButton gestisciIssueButton;
    private JButton gestisciUtentiButton;
    private JButton nuovaIssueButton;
    private JButton dashboardButton;
    private JTable dashboardTable;
    private JButton disconnettitiButton;
    private JPanel topPanel;
    private JPanel midPanel;
    private JPanel botPanel;
    private JLabel ruoloLabel;
    private JPanel dashboardPanel;

    public HomeAmm() {
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
        }

        nuovaIssueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NuovaIssue();
                dispose();
            }
        });

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

        areaPersonaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AreaPersonaleAmm();
                dispose();
            }
        });
    }
}
