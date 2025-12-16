package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {

    private JPanel mainpanel;
    private JPanel infoutentePanel;
    private JLabel benvenuto;
    private JLabel userpngLabel;
    private JButton areaPersonaleButton;
    private JPanel operationsPanel;
    private JButton gestisciIssueButton;
    private JButton altroButton;
    private JButton nuovaIssueButton;
    private JButton dashboardButton;
    private JTable dashboardTable;
    private JButton disconnettitiButton;
    private JPanel topPanel;
    private JPanel midPanel;
    private JPanel botPanel;

    public Home() {
        setContentPane(mainpanel);
        setTitle("Home");
        setSize(1200,800);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);



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
    }

}
