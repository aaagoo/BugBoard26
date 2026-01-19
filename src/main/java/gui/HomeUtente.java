package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gui.util.BaseFrame;
import gui.util.RoundedPanel;
import gui.util.StyleManager;
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
    private JScrollPane dashboardScroll;
    private JButton notificheButton;

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

        StyleManager.styleTable(dashboardTable, dashboardScroll);

        Utility.impostaLarghezzeColonne(dashboardTable, 17, 70, 20, 50, 40, 20);

        dashboardTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    int row = dashboardTable.getSelectedRow();
                    if (row != -1) {
                        Object idObj = dashboardTable.getModel().getValueAt(row, 0);
                        Long issueId = Long.parseLong(idObj.toString());
                        new VIsualizzaIssue(issueId);
                        dispose();
                    }
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

        if (notificheButton != null) {
            notificheButton.addActionListener(e -> {
                new Notifiche();
                dispose();
            });
        }

        controllaNotifiche();
    }

    private void controllaNotifiche() {
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return Controller.getInstance().contaNotificheNonLette();
            }

            @Override
            protected void done() {
                try {
                    int count = get();
                    if (count > 0) {
                        if (notificheButton != null) {
                            notificheButton.setText("Notifiche (" + count + ")");
                            notificheButton.setBackground(new Color(172, 71, 53)); // Rosso richiesto
                        }
                        
                        JOptionPane.showMessageDialog(HomeUtente.this, 
                                "Hai " + count + " nuove notifiche.", 
                                "Nuove Notifiche", 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                    } else {
                        if (notificheButton != null) {
                            notificheButton.setText("Notifiche");
                            notificheButton.setBackground(new Color(54, 172, 150)); // Verde scuro default
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
