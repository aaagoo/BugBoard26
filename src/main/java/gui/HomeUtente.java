package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import modello.Account;
import controller.Controller;
import gui.util.Utility;

public class HomeUtente extends BaseFrame {
    private JPanel mainPanel;
    private JPanel infoutentePanel;
    private JLabel benvenutoLabel;
    private JLabel userpngLabel;
    private JButton areaPersonaleButton;
    private JPanel operationsPanel;
    private JButton nuovaIssueButton;
    private JButton dashboardButton;
    private JTable dashboardTable;
    private JButton disconnettitiButton;
    private JPanel topPanel;
    private JPanel midPanel;
    private JPanel botPanel;
    private JLabel ruoloLabel;
    private JPanel dashboardPanel;

    public HomeUtente() {
        super();
        setContentPane(mainPanel);
        setTitle("BugBoard26");
        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        midPanel.setBorder(new RoundedPanel("pannello"));
        infoutentePanel.setBorder(new RoundedPanel("pannello"));
        botPanel.setBorder(new RoundedPanel("pannello"));
        operationsPanel.setBorder(new RoundedPanel("finestra"));
        dashboardPanel.setBorder(new RoundedPanel("finestra"));

        Account utente = Controller.getInstance().getUtenteCorrente();
        if (utente != null) {
            benvenutoLabel.setText(utente.getNome() + " " + utente.getCognome());
            ruoloLabel.setText(utente.getRuolo().toString());
            Utility.caricaAvatar(userpngLabel, utente.getAvatar(), 80, 80);
            Utility.caricaIssueAssegnate(dashboardTable, utente.getNomeUtente(), Controller.getInstance());
        }

        dashboardTable.getTableHeader().setReorderingAllowed(false);
        dashboardTable.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = (JScrollPane) dashboardTable.getParent().getParent();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBorder(null);

        Utility.impostaLarghezzeColonne(dashboardTable, 17, 70, 20, 50, 40, 20);

        dashboardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = dashboardTable.getSelectedRow();
                if (row != -1) {
                    Object idObj = dashboardTable.getModel().getValueAt(row, 0);
                    Long issueId = Long.parseLong(idObj.toString());
                    new VIsualizzaIssue(issueId);
                    dispose();
                }
            }
        });

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

        areaPersonaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AreaPersonale();
                dispose();
            }
        });

        dashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Dashboard();
                dispose();
            }
        });
    }
}
